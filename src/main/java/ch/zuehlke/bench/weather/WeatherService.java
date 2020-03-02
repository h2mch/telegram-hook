package ch.zuehlke.bench.weather;

import org.eclipse.microprofile.config.inject.ConfigProperty;
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import ch.zuehlke.bench.telegram.TelegramClient;
import ch.zuehlke.bench.telegram.TelegramCommand;
import io.quarkus.arc.Unremovable;

@ApplicationScoped
@Path("/temp")
@Unremovable
public class WeatherService implements TelegramCommand {

    private static final Logger LOG = Logger.getLogger(WeatherService.class);

    @Inject
    @RestClient
    MeteoSwissClient meteoSwissClient;

    @Inject
    @RestClient
    MeteoCentraleClient meteoCentraleClient;

    @Inject
    @RestClient
    TelegramClient telegramClient;


    @ConfigProperty(name = "telegram.token")
    String botToken;

    @POST
    @Path("/{stationId}")
    public void triggerCurrentTempt(@PathParam("stationId") String stationId, @QueryParam("chatId") String chatId) {
        Set<String> stations = new HashSet<>(1);
        stations.add(stationId);
        String currentWeather = getCurrentWeather(stationId);
        telegramClient.sendMessage(botToken, chatId, currentWeather);
    }


    @Override
    public String execute(String... parameter) {
        return getCurrentWeather(parameter[0]);
    }

    public String getCurrentWeather(String station) {
        Set<String> stations = new HashSet<>(1);
        stations.add(station);
        try {
            JsonObject stationOverview = meteoSwissClient.getStationOverview(stations);
            try {
                JsonObject currentWeather = stationOverview.getJsonObject(station);
                JsonNumber timestamp = currentWeather.getJsonNumber("time");
                ZonedDateTime zonedDateTime = Instant.ofEpochSecond(timestamp.longValue() / 1000).atZone(ZoneId.of("CET"));
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
