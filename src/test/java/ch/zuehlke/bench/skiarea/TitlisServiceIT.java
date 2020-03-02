package ch.zuehlke.bench.skiarea;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class TitlisServiceIT {

    @Test
    void extract_of_lift_information() {

        given()
                .when().get("/ski/titlis")
                .then()
                .statusCode(200)
                .body(containsString("Letztes Update"));
    }
}

