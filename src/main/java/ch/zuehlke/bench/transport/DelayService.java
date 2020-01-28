package ch.zuehlke.bench.transport;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

@ApplicationScoped
public class DelayService {

    @Inject
    @RestClient
    OpenDataClient openDataClient;


    public String getNextDeparture(String from, String to) {
        JsonObject response = openDataClient.getConnections(from, to, 1, 1);
        JsonObject nextConnection = response.getJsonArray("connections").getJsonObject(0);
        return buildDelayText(nextConnection);

    }

    public String getNextDepartureFromLuzernToBern() {
        return getNextDeparture("Luzern", "Bern");
    }

    public String getNextDepartureFromBernToLuzern() {
        return getNextDeparture("Bern", "Luzern");
    }

    private String buildDelayText(JsonObject connection) {
        JsonObject from = connection.getJsonObject("from");
        long departureTimestamp = from.getJsonNumber("departureTimestamp").longValue();
        String departure = from.getJsonObject("station").getString("name");

        ZonedDateTime zonedDateTime = Instant.ofEpochSecond(departureTimestamp).atZone(ZoneId.systemDefault());

        StringBuilder sb = new StringBuilder();

        if (from.isNull("delay") || from.getJsonNumber("delay").intValue() == 0) {
            sb.append("No delay");
        } else {
            sb.append(from.getJsonNumber("delay").longValue()).append(" minutes delay");
        }
        sb.append(". ").append(departure).append(" dep ").append(zonedDateTime.toLocalTime());

        if (!from.isNull("platform")) {
            sb.append(" at platform ").append(from.getString("platform"));
        }
        return sb.toString();
    }
}
