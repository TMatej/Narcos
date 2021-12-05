package cz.fi.muni.pv217.narcos.medicine.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class MedicineResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/medicine")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy"));
    }

}