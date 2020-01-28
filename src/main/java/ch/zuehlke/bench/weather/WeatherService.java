package ch.zuehlke.bench.weather;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonNumber;
import javax.json.JsonObject;

@ApplicationScoped
public class WeatherService {

    private static final Logger LOG = Logger.getLogger(WeatherService.class);

    @Inject
    @RestClient
    MeteoSwissClient meteoSwissClient;

    public String getCurrentWeather(String station) {
        Set<String> stations = new HashSet<>(1);
        stations.add(station);
        try {
            JsonObject stationOverview = meteoSwissClient.getStationOverview(stations);
            try {
                JsonObject currentWeather = stationOverview.getJsonObject(station);
                JsonNumber timestamp = currentWeather.getJsonNumber("time");
                ZonedDateTime zonedDateTime = Instant.ofEpochSecond(timestamp.longValue() / 1000).atZone(ZoneId.systemDefault());
                return station + " " + currentWeather.getJsonNumber("temperature") + "°C (" + zonedDateTime.toLocalTime() + ")";
            } catch (Exception e) {
                LOG.errorf(e, "Could not process response '%s' for station '%s'", stationOverview, station);
            }
        } catch (Exception e) {
            LOG.errorf(e, "Could not call station overview for station '%s'", station);
        }
        return "";
    }
}
