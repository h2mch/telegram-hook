package ch.zuehlke.bench.event;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import ch.zuehlke.bench.skiarea.TitlisService;
import ch.zuehlke.bench.transport.DelayService;
import ch.zuehlke.bench.weather.FoehnService;
import ch.zuehlke.bench.weather.WeatherService;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class StartUp {

    private static final Logger LOGGER = Logger.getLogger("StartUp");


    @Inject
    WeatherService weatherService;

    @Inject
    DelayService delayService;

    @Inject
    TitlisService ts;

    @Inject
    FoehnService fs;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");


    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

}