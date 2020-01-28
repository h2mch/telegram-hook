package ch.zuehlke.bench.weather;


import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Set;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/v1")
@RegisterRestClient
public interface MeteoSwissClient {

    /**
     * @param plz Postcode with two additional 00. f.e. 605200 for 6052 (Hergiswil)
     * @return A Json representation of the current weather.
     */
    @GET
    @Path("/plzDetail")
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject getWeather(@QueryParam("plz") String plz);


    /**
     *
     * @param stationId Shortname of Station. f.e. LUZ (Luzern); PIL (Pilatus); ALP (Alpnach); ENG (Engelberg)
     * @return
     */
    @GET
    @Path("/stationOverview")
    JsonObject getStationOverview(@QueryParam("station") Set<String> stationId);

}
