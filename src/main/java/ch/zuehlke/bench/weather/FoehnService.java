package ch.zuehlke.bench.weather;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.zuehlke.bench.telegram.TelegramCommand;
import io.quarkus.arc.Unremovable;

@ApplicationScoped
@Unremovable
public class FoehnService implements TelegramCommand {

    private static final String CONFIG_FOEHN_URL = "weather.foehn.url";

    @ConfigProperty(name = CONFIG_FOEHN_URL)
    Optional<String> url;

    @Inject
    @RestClient
    MeteoCentraleClient meteoCentraleClient;

    @Override
    public String execute(String... parameter) {
        return url.get() + "?" + LocalDate.now().toEpochDay();
    }

}
