package ch.zuehlke.bench.telegram;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/bot{token}")
@RegisterRestClient
public interface TelegramClient {


    @GET
    @Path("/sendMessage")
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject sendMessage(@PathParam("token") String token, @QueryParam("chat_id") String chatId, @QueryParam("text") String text);

    @POST
    @Path("/sendMessage")
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject sendMessageWithBody(@PathParam("token") String token, @QueryParam("chat_id") String chatId, @QueryParam("text") String text, String body);

    @GET
    @Path("/sendPhoto")
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject sendPhoto(@PathParam("token") String token, @QueryParam("chat_id") String chatId, @QueryParam("photo") String urlOfFoto);


}
