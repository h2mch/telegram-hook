package ch.zuehlke.bench.skiarea;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TitlisServiceIT {

    @Test
    void extract_of_lift_information() throws IOException {

        TitlisService ts = new TitlisService();
        String information = ts.extractLiveLiftsInformation();

        assertFalse(information.isEmpty());
        assertTrue(information.lines().count() >= 2);
    }
}