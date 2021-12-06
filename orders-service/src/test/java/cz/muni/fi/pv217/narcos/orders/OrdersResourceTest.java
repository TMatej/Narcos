package cz.muni.fi.pv217.narcos.orders;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class OrdersResourceTest {

    @Test
    public void testCreateOrder() {
        given()
            .body("{\"userId\": 1, \"items\": [{\"medicationId\": 20, \"count\": 2}]}")
            .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/orders/new")
                .then()
                .statusCode(201);
        given()
                .when().get("/orders/1")
                .then()
                .statusCode(200)
                .body("id", is(1));
    }

    @Test
    public void testGettingAllOrders() {
        given()
              .when().get("/orders")
              .then()
                 .statusCode(200)
                 .body("$.size()", is(1));
    }


}
