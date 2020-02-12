package ch.zuehlke.bench.transport;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ch.zuehlke.bench.telegram.TelegramCommand;

@ApplicationScoped
@Path("/delay")
public class DelayService implements TelegramCommand {

    public static final String ALL_PRODUCTS = "1111111111";

    @Inject
    @RestClient
    OpenDataClient openDataClient;

    @Inject
    @RestClient
    FahrplanSBBClient sbbFahrplan;

    @Override
    public String execute(String... parameter) {
        String from = parameter[0];
        String to = parameter[1];
        String products = "";
        if (parameter.length == 3) {
            products = setProductBitForTrain(products, parameter[2]);
        }
        return getNextDepartureSBB(from, to, products);
    }

    private String findStationId(String name) {

        switch (name.toLowerCase()) {
            case "luzern":
                return "8505000";
            case "zürich":
                return "8503000";
            case "bern":
                return "8507000";
            case "basel":
                return "8500010";

            default:
                throw new IllegalArgumentException("No mapping for " + name + " exists");
        }
    }

    @GET
    @Path("/sbb")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Journey> getNext10DepartureSBB(@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("products") String products) {
        if (from == null || from.isEmpty()) {
            throw new IllegalArgumentException("From can not be empty");
        }

        String response = sbbFahrplan.getConnections(
                products == null || products.isEmpty() ? "11111111" : products,
                "dep",
                1,
                10,
                1,
                "vs_java3",
                0,
                from,
                to);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Journey.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            List<Journey> journeys = new ArrayList<>();
            response.lines().forEach(line -> {
                try {
                    journeys.add((Journey) jaxbUnmarshaller.unmarshal(new StringReader(line)));
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            });
            return journeys;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @GET
    @Path("/opendata")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNextDepartureOpenData(@QueryParam("from") String from, @QueryParam("to") String to) {
        JsonObject response = openDataClient.getConnections(from, to, 1, 1);
        JsonObject nextConnection = response.getJsonArray("connections").getJsonObject(0);
        return buildDelayText(nextConnection);

    }

    private String getNextDepartureSBB(String from, String to, String products) {

        String fromStationId = findStationId(from);
        String toStationId = findStationId(to);
        List<Journey> next10DepartureSBB = getNext10DepartureSBB(fromStationId, toStationId, products);
        return next10DepartureSBB.stream().findFirst().get().toString();

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

    private String setBit(int i, String trainproduct) {
        char[] oldProduct = trainproduct.toCharArray();
        char[] newProduct;
        int length = trainproduct.length();
        if (i < length) {
            newProduct = oldProduct;
        } else {
            newProduct = new char[i + 1];
            for (int j = 0; j < length; j++) {
                newProduct[j] = oldProduct[j];
            }
            for (int j = length; j < i; j++) {
                newProduct[j] = '0';
            }
        }
        newProduct[i] = '1';
        return new String(newProduct);
    }

    String setProductBitForTrain(String productBit, String trainproduct) {

        if (trainproduct == null || trainproduct.isEmpty()) {
            return ALL_PRODUCTS;
        }

        switch (trainproduct.toLowerCase()) {
            case "ice":
            case "en":
            case "cnl":
            case "cis":
            case "es":
            case "met":
            case "nz":
            case "pen":
            case "tgv":
            case "tha":
            case "x2":
                return setBit(0, productBit);
            case "eurocity":
            case "intercity":
            case "intercitynight":
            case "supercity":
                return setBit(1, productBit);
            case "interregio":
                return setBit(2, productBit);
            case "schnellzug":
            case "regioexpress":
                return setBit(3, productBit);
            case "s-bahn":
            case "stadtexpress":
            case "regionalzug":
                return setBit(5, productBit);
            case "metro":
            case "tram":
                return setBit(9, productBit);
            case "bus":
            case "postauto":
                return setBit(6, productBit);
            case "schiff":
            case "fähre":
            case "dampfschiff":
                return setBit(4, productBit);
            case "luftseilbahn":
            case "standseilbahn":
            case "bergbahn":
                return setBit(7, productBit);
            default:
                throw new IllegalArgumentException("cannot handle: " + trainproduct);
        }
    }

    void setProductBits(final StringBuilder productBits, final char product) {
        switch (product) {
            case 'I':
                productBits.setCharAt(0, '1'); // ICE/EN/CNL/CIS/ES/MET/NZ/PEN/TGV/THA/X2
                productBits.setCharAt(1, '1'); // EuroCity/InterCity/InterCityNight/SuperCity
                break;
            case 'R':
                productBits.setCharAt(2, '1'); // InterRegio
                break;
            case 'E':
                productBits.setCharAt(3, '1'); // Schnellzug/RegioExpress
                break;
            case 'S': // S-Bahn/StadtExpress/Regionalzug
                productBits.setCharAt(5, '1');
                break;
            case 'U': // Metro
            case 'T': // Tram
                productBits.setCharAt(9, '1');
                break;
            case 'B': // Bus
            case 'P': // Postauto
                productBits.setCharAt(6, '1');
                break;
            case 'F': // Schiff/Fähre/Dampfschiff
                productBits.setCharAt(4, '1');
                break;
            case 'C': // Luftseilbahn/Standseilbahn/Bergbahn
                productBits.setCharAt(7, '1');
                break;
            default:
                throw new IllegalArgumentException("cannot handle: " + product);
        }
    }

}
