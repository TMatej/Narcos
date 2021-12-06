package cz.muni.fi.pv217.narcos.medicine.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class MedicineResourceTest {

    @Test
    @TestSecurity(user = "testUser", roles = "User")
    public void testGetMedicineById() {
        given()
        .when()
                .get("/medicine/1")
        .then()
                .statusCode(200)
                .body("expirationDate", is("2025-06-01"))
                .body("form", is("Pill"))
                .body("id", is(1))
                .body("manufacturer", is("Egis Pharmaceuticals PLC"))
                .body("name", is("Egilok"))
                .body("quantity", is(100));
    }

    @Test
    @TestSecurity(user = "testUser", roles = "User")
    public void testGetMedicineByIdInvalid() {
        given()
        .when()
                .get("/medicine/45")
        .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testUser", roles = "User")
    public void testGetMedicineByName() {
        given()
        .when()
                .get("/medicine/name/Egilok")
        .then()
                .statusCode(200)
                .body("expirationDate", is("2025-06-01"))
                .body("form", is("Pill"))
                .body("id", is(1))
                .body("manufacturer", is("Egis Pharmaceuticals PLC"))
                .body("name", is("Egilok"))
                .body("quantity", is(100));
    }

    @Test
    @TestSecurity(user = "testUser", roles = "Admin")
    public void testUpdateMedicine() {
        given()
        .when()
                .get("/medicine/2")
        .then()
                .statusCode(200)
                .body("expirationDate", is("2019-12-01"))
                .body("form", is("Pill"))
                .body("id", is(2))
                .body("manufacturer", is("Zentiva"))
                .body("name", is("Cardilan"))
                .body("quantity", is(100));

        given()
                .body("{\"expirationDate\":\"2019-12-01\",\"form\":\"Pill\",\"id\": 2,\"manufacturer\":\"Zentiva\",\"name\":\"Cardilan\",\"quantity\":99}")
                .contentType(ContentType.JSON)
        .when()
                .put("/medicine/2")
        .then()
                .statusCode(200)
                .body(is("Medicine with id 2 updated!"));

        given()
        .when()
                .get("/medicine/2")
        .then()
                .statusCode(200)
                .body("expirationDate", is("2019-12-01"))
                .body("form", is("Pill"))
                .body("id", is(2))
                .body("manufacturer", is("Zentiva"))
                .body("name", is("Cardilan"))
                .body("quantity", is(99));
    }

    @Test
    @TestSecurity(user = "testUser", roles = "User")
    public void testUpdateMedicineWithoutPermission() {
        given()
                .body("{\"expirationDate\":\"2025-06-01\",\"form\":\"Pill\",\"id\": 1,\"manufacturer\":\"Egis Pharmaceuticals PLC\",\"name\":\"Egilok\",\"quantity\":99}")
                .contentType(ContentType.JSON)
                .when()
                .put("/medicine/1")
                .then()
                .statusCode(403);
    }
}