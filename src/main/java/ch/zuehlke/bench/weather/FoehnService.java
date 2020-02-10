package ch.zuehlke.bench.weather;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.io.File;
import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.zuehlke.bench.telegram.TelegramCommand;

@ApplicationScoped
public class FoehnService implements TelegramCommand {

    public static final String FOEHN_DIAGRAMM = "http://www.meteocentrale.ch/uploads/pics/uwz-ch_foehn_de.png%3F?";
    private static final Logger LOG = Logger.getLogger(FoehnService.class);
    @Inject
    @RestClient
    MeteoCentraleClient meteoCentraleClient;

    @Override
    public String execute(String... parameter) {
        return FOEHN_DIAGRAMM + LocalDate.now().toEpochDay();
    }

    public String getCurrentFoehnDiagramm() {

        File foehnDiagram = meteoCentraleClient.getFoehnDiagram();
        if (foehnDiagram == null) {
            return "Could not find image";
        }

        return "found ;-)";
    }
}
