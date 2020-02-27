package ch.zuehlke.bench.skiarea;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class TitlisServiceTest {

    //    @Test
//    @Disabled("Issue with dependency injection on restclient")
    void extract_of_lift_information() {

        given()
                .when().get("/ski/titlis")
                .then()
                .statusCode(200)
                .body(containsString("Letztes Update"));
    }
}

