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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.zuehlke.bench.telegram.TelegramCommand;
import io.quarkus.arc.Unremovable;

@ApplicationScoped
@Path("/ski/titlis")
@Unremovable
public class TitlisService implements TelegramCommand {

    private static final Logger LOG = Logger.getLogger(TitlisService.class);

    private static final String CONFIG_TITLIS_URL = "skiarea.titlis.url";

    @ConfigProperty(name = CONFIG_TITLIS_URL)
    Optional<String> url;

    @Override
    public String execute(String... parameter) {
        try {
            return extractLiveLiftsInformation();
        } catch (IOException e) {
            LOG.warnf(e, "Could not retrieve information");
        }
        return "Command not working.";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String extractLiveLiftsInformation() throws IOException {
        Connection connect = Jsoup.connect(url.orElseThrow(() -> new InformationNotFoundException("URL '" + CONFIG_TITLIS_URL + "' not configured")));
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
