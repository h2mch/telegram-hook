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

        System.out.println(prodcutBits);
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

    @BeforeEach
    void setUp() {
        delayService = new DelayService();
        delayService.sbbFahrplan = new FahrplanSBBClientMock();
        delayService.openDataClient = new OpenDataClientMock();
    }
}