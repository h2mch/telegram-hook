package ch.zuehlke.bench.weather;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;

import static io.netty.util.internal.SystemPropertyUtil.contains;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
class WeatherServiceIT {

    @Inject
    WeatherService weatherService;

    @Test
    void getCurrentWeather() {
        String luz = weatherService.getCurrentWeather("LUZ");
        assertThat(luz, contains("LUZ"));
    }
}