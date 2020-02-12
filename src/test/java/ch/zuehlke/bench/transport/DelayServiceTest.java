package ch.zuehlke.bench.transport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DelayServiceTest {


    DelayService delayService;

    @Test
    void setProductBits() {
        StringBuilder products = new StringBuilder("0000000000");
        for (char c : "IB".toCharArray()) {
            delayService.setProductBits(products, c);
        }

        System.out.println(products.toString());


    }

    @BeforeEach
    void setUp() {
        delayService = new DelayService();
    }
}