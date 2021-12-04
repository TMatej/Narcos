package cz.fi.muni.pv217.narcos.user.service;

import cz.fi.muni.pv217.narcos.user.DTOs.LoginInformationDTO;
import cz.fi.muni.pv217.narcos.user.DTOs.RegisterInformationDTO;
import cz.fi.muni.pv217.narcos.user.entity.Role;
import cz.fi.muni.pv217.narcos.user.resource.AuthResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
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
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjg4ODgvbG9naW4iLCJ1cG4iOiJleGFtcGxlQGRvbWFpbi5jb20iLCJncm91cHMiOlsiQWRtaW4iXSwic3ViIjoiMSIsImlhdCI6MTYzODYzNzEzNSwiZXhwIjoxNjM4NjM3NDM1LCJqdGkiOiI1MzZmODk1NC02ODU0LTQzYzYtOWQ1MC1lOWM1NTFiZWIxNjQifQ.YYJAjVMdIQsAG4p4BPxd7IfnA36oumMIMKU6E3DwZrk6xBo5DK9qT1IZfyFJJ8o5c9IJ-0Ilt79XzO2ac9Q_b57jMv26xENwn62W4QOK5TT8nqeDDbMmpRryJ7S5hQC2o5uGXd46Yx38bW9jBy5ZKl0dyoTTQ_Pz6CmitJn6vHBgeDFI5D8ULAKcByTP4Rjuem49svImcAHX6k2cS47s8g6B23hHN4Tf8ipS3_7trA4dlp7YEMX3F6o6KQYwu-ALNbJw8JZ-dQnTGF5gSfm-EW97lS5OiVl_aGCJ55z8Y9EBHloE6wRuVKnYMB4baxSaZJ8tqE4eoa_brFW9EQC3QyBfnobPTt9Ckv1m7fqYV_ezOqWd7a70QE1Bhco8VC72-El2tR2BQLh8mZTi3VhlTeeGWjL9FlRAoutBOuLYyq_4HWR2_GqA7F06y5JmRrpyn31nCHbVSf1h7DI5cXc41qJryswe33PKuNmWLXsO7czzxmmY7zsXTqD0hZ7jAPmBsACgjIWW8eQT1L_Jbk1aEojQ_fJPmKBYOvM7BLilMLQxFQeWB79bplvt4mN3gNZg66mq8Krh5-Ar9k1ZLBV5xdpk9jkK1cc_k8RkoXk8CNvrt6wVS-OL68OPIy7wXXvlgSIUEPSujRC-_9Oax8FJUjQ1p6Pvf7WIHV9E9y2x6vs";

        given()
                .body("{\"email\": \"example@domain.com\",\"password\": \"password\"}")
                .when()
                    .post("/login")
                .then()
                    .statusCode(200)
                    .body(is(token));
    }

    @Test
    public void testRegistrationEndpoint() {
        given()
                .body("{ \"email\": \"admin@domain.com\", \"password\": \"my_password\", \"firstName\": \"Admin\", \"lastName\": \"First\", \"role\": 1 }")
                .when()
                    .post("/register")
                .then()
                    .statusCode(200)
                    .body(is("User with username 'admin@domain.com' created!"));
    }
}
