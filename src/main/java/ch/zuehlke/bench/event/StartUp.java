package ch.zuehlke.bench.event;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import ch.zuehlke.bench.telegram.TelegramClient;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class StartUp {

    private static final Logger LOGGER = Logger.getLogger("StartUp");

    @Inject
    @RestClient
    TelegramClient telegramClient;

    @ConfigProperty(name = "telegram.token")
    String botToken;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
        //telegramClient.sendMessage(botToken, "77667638", "application start");
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
        //telegramClient.sendMessage(botToken, "77667638", "application stop");
    }

}