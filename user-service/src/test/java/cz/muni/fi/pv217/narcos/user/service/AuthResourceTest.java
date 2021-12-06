package cz.muni.fi.pv217.narcos.user.service;

import cz.muni.fi.pv217.narcos.user.resource.AuthResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author Matej Turek
 */
@QuarkusTest
@TestHTTPEndpoint(AuthResource.class)
public class AuthResourceTest {

    @Test
    public void testLoginEndpoint() {
        given()
                .body("{\"email\": \"admin@localhost.com\",\"password\": \"password\"}")
                .contentType(ContentType.JSON)
        .when()
                .post("/login")
        .then()
                .statusCode(200);
    }

    @Test
    public void testLoginEndpointInvalidCredentials() {
        given()
                .body("{\"email\": \"admin@localhost.com\",\"password\": \"password123\"}")
                .contentType(ContentType.JSON)
        .when()
                .post("/login")
        .then()
                .statusCode(401);
    }

    @Test
    public void testLoginEndpointInvalidUser() {
        given()
                .body("{\"email\": \"someadmin@localhost.com\",\"password\": \"password123\"}")
                .contentType(ContentType.JSON)
        .when()
                .post("/login")
        .then()
                .statusCode(404);
    }

    @Test
    public void testRegistrationEndpoint() {
        given()
                .body("{ \"email\": \"newadmin@localhost.com\", \"password\": \"my_password\", \"firstName\": \"Admin\", \"lastName\": \"First\", \"role\": \"User\" }")
                .contentType(ContentType.JSON)
        .when()
                .post("/register")
        .then()
                .statusCode(200)
                .body(is("User with username 'newadmin@localhost.com' created!"));
    }

    @Test
    public void testRegistrationEndpointExistingUser() {
        given()
                .body("{ \"email\": \"admin@localhost.com\", \"password\": \"my_password\", \"firstName\": \"Admin\", \"lastName\": \"First\", \"role\": \"User\" }")
                .contentType(ContentType.JSON)
                .when()
                .post("/register")
                .then()
                .statusCode(400);
    }
}
