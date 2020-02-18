package ch.zuehlke.bench.skiarea;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.zuehlke.bench.telegram.TelegramCommand;

@ApplicationScoped
public class TitlisService implements TelegramCommand {


    private static final Logger LOG = Logger.getLogger(TitlisService.class);

    @Inject
    @ConfigProperty(name = "skiarea.titlis.url")
    Optional<String> url;

    private Connection connect;

    public TitlisService() {
        if (url != null && url.isPresent()) {
            connect = Jsoup.connect(url.get());
        }
    }

    TitlisService(Connection connect) {
        this.connect = connect;
    }

    @Override
    public String execute(String... parameter) {
        try {
            return extractLiveLiftsInformation();
        } catch (IOException e) {
            LOG.warnf(e, "Could not retrieve information");
        }
        return "Command not working.";
    }


    public String extractLiveLiftsInformation() throws IOException {
        if (url.isEmpty()) {
            return "url for titlis not configured";
        }
        Document doc = connect.get();
        Elements callout_primary = doc.getElementsByClass("callout primary");
        if (callout_primary == null || callout_primary.size() != 1) {
            LOG.warnf("Command not find information on page. %s", doc.body());
            throw new InformationNotFoundException("Could not extract Information from url '" + url + "'.");
        }
        String text = callout_primary.get(0).text();
        String lastUpdate = callout_primary.next().text();
        return text + "\n" + lastUpdate;
    }

}
