package ch.zuehlke.bench.transport;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/bin")
@RegisterRestClient(configKey = "fahrplan-client")
public interface FahrplanClient {

    @GET
    @Path("/stboard.exe/dn")
    @Produces(MediaType.APPLICATION_JSON)
    String getConnections(
            @QueryParam("productsFilter") String productsFilter,
            @QueryParam("boardType") String boardType,
            @QueryParam("disableEquivs") int disableEquivs,
            @QueryParam("maxJourneys") int limit,
            @QueryParam("start") int start,
            @QueryParam("L") String styleSheet,
            @QueryParam("hcount") int showOldDepature,
            @QueryParam("input") String stationIdFrom,
            @QueryParam("dirInput") String stationIdDirection);

}
