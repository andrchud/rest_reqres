package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class RegistrationTests {
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Успешная регистрация")
    void registrationTest() {
        String registrationData = "{\"email\": \"eve.holt@reqres.in\",\"password\": \"pistol\"}";

        given()
                .body(registrationData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("id", is(4))
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @DisplayName("Регистрация без пароля")
    void registrationWithoutPasswordTest() {
        String registrationData = "{\"email\": \"eve.holt@reqres.in\"}";

        given()
                .body(registrationData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    @DisplayName("Cписок ползователей не пустой")
    void listUsersNotNullTest() {
        given()
                .log().uri()

                .when()
                .get("/users?page=2")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data", notNullValue());

    }

    @Test
    @DisplayName("Успешное создание пользователя")
    void createUserTest() {

        String createUserData = "{\"name\": \"Andrey\", \"job\": \"Senior QA\"}";

        given()
                .body(createUserData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("Andrey"))
                .body("job", is("Senior QA"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().path("id");
    }

    @Test
    @DisplayName("Создание пользователя с некорректным JSON")
    void createUserWithInvalidJsonTest() {

        String createUserData = "{\"name\": Andrey}";

        given()
                .body(createUserData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(400);
    }

}
