package ch.zuehlke.bench.transport;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/v1")
@RegisterRestClient
public interface OpenDataClient {


    /**
     * TelegramClient
     * Interface for https://transport.opendata.ch/docs.html#connections
     */
    @GET
    @Path("/connections")
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject getConnections(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("direct") int direct,
            @QueryParam("limit") int limit);

}
