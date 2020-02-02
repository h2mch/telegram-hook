package ch.zuehlke.bench.telegram;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.Optional;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/webhook")
public class TelegramController {


    private static final Logger LOG = Logger.getLogger(TelegramController.class);

    @Inject
    @RestClient
    TelegramClient telegramClient;

    @Inject
    CommandExecutor commandExecutor;

    @ConfigProperty(name = "telegram.token")
    String botToken;


    /**
     * Is called be Telegram.
     * <p>
     * The endpoint has be configured in the telegram network. GET https://api.telegram.org/bot<<TOKEN_NUMBER:TOKEN_HASH>>/setWebhook?url=https://webhook-3vvvk4kveq-ew.a.run.app/webhook
     * <p>
     * { "update_id": 876641959, "message": { "message_id": 223, "from": { "id": 77667638, "is_bot":
     * false, "first_name": "Heinz", "last_name": "Marti", "username": "h2mch", "language_code":
     * "en" }, "chat": { "id": 77667638, "first_name": "Heinz", "last_name": "Marti", "username":
     * "h2mch", "type": "private" }, "date": 1579614020, "text": "Luzern ab" } }
     *
     * @param jsonObject a telegram json message
     */
    @POST
    public void webHook(JsonObject jsonObject) {
        LOG.infof("Received Message \n %s", jsonObject);
        String message;
        long chatId;

        Optional<String> body = Optional.empty();
        Optional<String> response = Optional.empty();
        Optional<CommandExecutor.COMMAND> command;

        String userData;

        if (jsonObject.containsKey("callback_query")) {
            try {
                JsonObject callback_query = jsonObject.getJsonObject("callback_query");
                chatId = callback_query.getJsonObject("message").getJsonObject("chat").getJsonNumber("id").longValue();
                userData = callback_query.getJsonString("data").getString();
                command = CommandExecutor.parseCommand(userData);
            } catch (Exception e) {
                LOG.warnf(e, "Could not parse callback message '%s'", jsonObject);
                return;
            }
        } else {
            try {
                JsonObject telegramMessage = jsonObject.getJsonObject("message");
                userData = telegramMessage.getString("text");
                chatId = telegramMessage.getJsonObject("chat").getJsonNumber("id").longValue();

                switch (userData.toLowerCase()) {
                    case "/delay":
                        body = Optional.of("{" +
                                "  \"reply_markup\": {" +
                                "    \"inline_keyboard\": [[" +
                                "      {" +
                                "        \"text\": \"Luzern->Bern\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.LUZ_BRN.name() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Bern->Luzern\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.BRN_LUZ.name() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Luzern->Zueri\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.LUZ_ZRH.name() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Zueri->Luzern\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.ZRH_LUZ.name() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Luzern->Basel\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.LUZ_BAS.name() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Basel->Luzern\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.BAS_LUZ.name() + "\"" +
                                "      }" +
                                "    ]]" +
                                "  }" +
                                "}");
                        break;
                    case "/temp":
                        body = Optional.of("{" +
                                "  \"reply_markup\": {" +
                                "    \"inline_keyboard\": [[" +
                                "      {" +
                                "        \"text\": \"Luzern\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.LUZ.name() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Titlis\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.TIT.name() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Engelberg\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.ENG.name() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Bern\"," +
                                "        \"callback_data\": \"/" + CommandExecutor.COMMAND.BER.name() + "\"" +
                                "      }]" +
                                "    ]" +
                                "  }" +
                                "}");

                        break;
                    case "/show":
                        response = Optional.of("/temp or /delay");
                        break;
                    default:
                        LOG.debugf("No Command Message '%s'", userData);
                }

                if (body.isPresent()) {
                    LOG.debugf("Message Body: '%s'", body.get());
                    telegramClient.sendMessageWithBody(botToken, "" + chatId, response.orElse("choice..."), body.get());
                    return;
                }

                command = CommandExecutor.parseCommand(userData);
            } catch (Exception e) {
                LOG.warnf(e, "Could not parse message '%s'", jsonObject);
                return;
            }
        }

        if (command == null) {
            LOG.warnf("No Command received. message '%s'", userData);
            return;
        }
        if (!command.isPresent()) {
            LOG.warnf("Command not implemented. message '%s'", userData);
            return;
        }

        try {
            telegramClient.sendMessage(botToken, chatId + "", response.orElse(
                    commandExecutor.execute(command.get())));
        } catch (Exception e) {
            LOG.errorf(e, "Could not send telegram message");
        }

    }
}