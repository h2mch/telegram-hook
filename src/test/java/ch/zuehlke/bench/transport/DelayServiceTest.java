package ch.zuehlke.bench.transport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class DelayServiceTest {

    DelayService delayService;

    FahrplanSBBClientMock sbbFahrplan;

    @Test
    void happyCase() {
        sbbFahrplan.setResponse(
                "<Journey fpTime=\"22:00\" fpDate=\"12.02.20\" delay=\"-\" platform=\"9\" targetLoc=\"Lausanne\" prod=\"IR 15#IR\" dir=\"Lausanne\" capacity=\"1|1\" is_reachable=\"0\" />\n" +
                        "<Journey fpTime=\"23:00\" fpDate=\"12.02.20\" delay=\"-\" platform=\"8\" targetLoc=\"Fribourg/Freiburg\" prod=\"IR 15#IR\" dir=\"Fribourg/Freiburg\" capacity=\"1|1\" is_reachable=\"0\" />\n" +
                        "<Journey fpTime=\"04:50\" fpDate=\"13.02.20\" delay=\"-\" platform=\"2\" targetLoc=\"Bern\" prod=\"RE 4356#RE\" dir=\"Bern\" capacity=\"2|3\" is_reachable=\"0\" />\n" +
                        "<Journey fpTime=\"06:00\" fpDate=\"13.02.20\" delay=\"-\" platform=\"8\" targetLoc=\"Gen&#232;ve-A&#233;roport\" prod=\"IR 15#IR\" dir=\"Gen&#232;ve-A&#233;roport\" capacity=\"1|1\" is_reachable=\"0\" />\n" +
                        "<Journey fpTime=\"07:00\" fpDate=\"13.02.20\" delay=\"-\" platform=\"8\" targetLoc=\"Gen&#232;ve-A&#233;roport\" prod=\"IR 15#IR\" dir=\"Gen&#232;ve-A&#233;roport\" capacity=\"1|1\" is_reachable=\"0\" />\n" +
                        "<Journey fpTime=\"08:00\" fpDate=\"13.02.20\" delay=\"-\" platform=\"8\" targetLoc=\"Gen&#232;ve-A&#233;roport\" prod=\"IR 15#IR\" dir=\"Gen&#232;ve-A&#233;roport\" capacity=\"2|1\" is_reachable=\"0\" />\n");

        String message = delayService.execute("Luzern", "Bern", "interregio");

        assertThat(message.lines().count(), is(1l));
        assertThat(message, containsString("22:00"));
    }

    @BeforeEach
    void setUp() {
        sbbFahrplan = new FahrplanSBBClientMock();
        delayService = new DelayService();
        delayService.sbbFahrplan = sbbFahrplan;
        delayService.openDataClient = new OpenDataClientMock();
    }

    @Test
    void productBits_empty() {
        String productBits = delayService.setProductBitForTrain("", "");
        assertThat(productBits, is(equalTo(DelayService.ALL_PRODUCTS)));
    }

    @Test
    void productBits_firstBit() {
        String prodcutBits = delayService.setProductBitForTrain("", "ice");
        assertThat(prodcutBits, is("1"));
        assertThat(prodcutBits.length(), is(1));
    }

    @Test
    void productBits_lastBit() {
        String prodcutBits = delayService.setProductBitForTrain("", "metro");
        assertThat(prodcutBits, startsWith("0"));
        assertThat(prodcutBits, endsWith("1"));
    }

    @Test
    void productBits_middleBit() {
        String prodcutBits = delayService.setProductBitForTrain("", "regioexpress");
        assertThat(prodcutBits, startsWith("0"));
        assertThat(prodcutBits, endsWith("1"));
    }

    @Test
    void productBits_moreProducts() {
        String prodcutBits = delayService.setProductBitForTrain("", "intercity");
        prodcutBits = delayService.setProductBitForTrain(prodcutBits, "ice");
        prodcutBits = delayService.setProductBitForTrain(prodcutBits, "postauto");

        assertThat(prodcutBits, startsWith("11")); //ice und intercity
        assertThat(prodcutBits, endsWith("1")); // postauto
        assertThat(prodcutBits, containsString("0"));
    }

    @Test
    void productBits_null() {
        String productBits = delayService.setProductBitForTrain("", null);
        assertThat(productBits, is(equalTo(DelayService.ALL_PRODUCTS)));
    }

    @Test
    void productBits_unknownProduct() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                delayService.setProductBitForTrain("", "unknown")
        );
    }
}