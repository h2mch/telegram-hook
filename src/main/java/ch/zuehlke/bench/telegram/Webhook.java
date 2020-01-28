package ch.zuehlke.bench.telegram;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.Optional;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import ch.zuehlke.bench.transport.DelayService;
import ch.zuehlke.bench.weather.WeatherService;

@Path("/webhook")
public class Webhook {


    private static final Logger LOG = Logger.getLogger(Webhook.class);

    @Inject
    @RestClient
    TelegramClient telegramClient;

    @Inject
    WeatherService weatherService;

    @Inject
    DelayService delayService;

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
    public void telegram(JsonObject jsonObject) {
        LOG.infof("Received Message \n %s", jsonObject);
        String message;
        long chatId;

        Optional<String> body = Optional.empty();
        Optional<String> response = Optional.empty();

        TelegramCommand.COMMAND command;

        if (jsonObject.containsKey("callback_query")) {
            try {
                JsonObject callback_query = jsonObject.getJsonObject("callback_query");
                chatId = callback_query.getJsonObject("message").getJsonObject("chat").getJsonNumber("id").longValue();
                String callbackData = callback_query.getJsonString("data").getString();
                command = TelegramCommand.COMMAND.parseCommand(callbackData);
            } catch (Exception e) {
                LOG.warnf(e, "Could not parse callback message '%s'", jsonObject);
                return;
            }
        } else {
            try {
                JsonObject telegramMessage = jsonObject.getJsonObject("message");
                message = telegramMessage.getString("text");
                chatId = telegramMessage.getJsonObject("chat").getJsonNumber("id").longValue();

                switch (message.toLowerCase()) {
                    case "/delay":
                        body = Optional.of("{" +
                                "  \"reply_markup\": {" +
                                "    \"inline_keyboard\": [[" +
                                "      {" +
                                "        \"text\": \"Luzern->Bern\"," +
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.LUZ_BRN.getParameter() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Bern->Luzern\"," +
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.BRN_LUZ.getParameter() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Luzern->Zueri\"," +
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.LUZ_ZRH.getParameter() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Zueri->Luzern\"," +
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.ZRH_LUZ.getParameter() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Luzern->Basel\"," +
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.LUZ_BAS.getParameter() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Basel->Luzern\"," +
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.BAS_LUZ.getParameter() + "\"" +
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
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.LUZ.getParameter() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Titlis\"," +
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.TIT.getParameter() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Engelberg\"," +
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.ENG.getParameter() + "\"" +
                                "      }," +
                                "      {" +
                                "        \"text\": \"Bern\"," +
                                "        \"callback_data\": \"" + TelegramCommand.COMMAND.BER.getParameter() + "\"" +
                                "      }]" +
                                "    ]" +
                                "  }" +
                                "}");

                        break;
                    case "/show":
                        response = Optional.of("/temp or /delay");
                        break;
                    default:
                        LOG.debugf("No Command Message '%s'", message);
                }

                if (body.isPresent()) {
                    LOG.debugf("Message Body: '%s'", body.get());
                    telegramClient.sendMessageWithBody(botToken, "" + chatId, response.orElse("choice..."), body.get());
                    return;
                }

                command = TelegramCommand.COMMAND.parseCommand(message);
            } catch (Exception e) {
                LOG.warnf(e, "Could not parse message '%s'", jsonObject);
                return;
            }
        }

        try {
            telegramClient.sendMessage(botToken, chatId + "", response.orElse(produceResponse(command)));
        } catch (Exception e) {
            LOG.errorf(e, "Could not send telegram message");
        }

    }

    private String produceResponse(TelegramCommand.COMMAND telegramCommand) {
        String response;
        switch (telegramCommand) {
            case LUZ_BRN:
                response = delayService.getNextDeparture("Luzern", "Bern");
                break;
            case BRN_LUZ:
                response = delayService.getNextDeparture("Bern", "Luzern");
                break;
            case LUZ_ZRH:
                response = delayService.getNextDeparture("Luzern", "Zürich HB");
                break;
            case ZRH_LUZ:
                response = delayService.getNextDeparture("Zürich HB", "Luzern");
                break;
            case LUZ_BAS:
                response = delayService.getNextDeparture("Luzern", "Basel");
                break;
            case BAS_LUZ:
                response = delayService.getNextDeparture("Basel", "Luzern");
                break;
            case TIT:
            case ENG:
            case LUZ:
                response = weatherService.getCurrentWeather(telegramCommand.getParameter());
                break;
            default:
                response = "not supported command";
        }
        return response;

    }

    private static boolean isStringUpperCase(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (!Character.isUpperCase(charArray[i]))
                return false;
        }
        return true;
    }
}