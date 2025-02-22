package tests;

import io.restassured.RestAssured;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import specs.ApiSpecs;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("API")
public class RegistrationTests {
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Успешная регистрация")
    void registrationTest() {
        RegistrationRequestModel requestModel = new RegistrationRequestModel();
        requestModel.setEmail("eve.holt@reqres.in");
        requestModel.setPassword("pistol");
        RegistrationResponseModel response = step("Успешная регистрация", () ->
            given(ApiSpecs.baseRequestSpec)
                    .body(requestModel)
                    .when()
                    .post("/register")
                    .then()
                    .spec(ApiSpecs.successResponseSpec)
                    .extract().as(RegistrationResponseModel.class)
        );
        step("Проверка токена в ответе", () ->
                assertThat(response).extracting("token","id").containsExactly("QpwL5tke4Pnpja7X4", 4)
        );
    }

    @Test
    @DisplayName("Регистрация без пароля")
    void registrationWithoutPasswordTest() {
        RegistrationRequestModel requestModel = new RegistrationRequestModel();
        requestModel.setEmail("eve.holt@reqres.in");

        ErrorResponseModel response = step("Не успешная регистрация без пароля", () ->
        given(ApiSpecs.baseRequestSpec)
                .body(requestModel)
                .when()
                .post("/register")
                .then()
                .spec(ApiSpecs.errorResponseSpec)
                .extract().as(ErrorResponseModel.class)
        );

        step("Проверка ощибки в ответе", () ->
                assertThat(response.getError()).isEqualTo("Missing password")
        );

    }

    @Test
    @DisplayName("Cписок ползователей не пустой")
    void listUsersNotNullTest() {
        step("Получение списка пользователей и проверка что он не пустой", () -> {
        given(ApiSpecs.baseRequestSpec)
                .when()
                .queryParam("page","2")
                .get("/users")
                .then()
                .spec(ApiSpecs.successResponseSpec)
                .body("data", notNullValue());
        });

    }

    @Test
    @DisplayName("Успешное создание пользователя")
    void createUserTest() {
        CreateUserRequestModel requestModel = new CreateUserRequestModel();
        requestModel.setName("Andrey");
        requestModel.setJob("Senior QA");

        CreateUserResponseModel response = step("Успешное создание пользователя", () ->
        given(ApiSpecs.baseRequestSpec)
                .body(requestModel)
                .when()
                .post("/users")
                .then()
                .spec(ApiSpecs.createdResponseSpec).
                extract().as(CreateUserResponseModel.class)
        );

        step("Проверка полей ответа", () -> {
            assertThat(response.getName()).isEqualTo("Andrey");
            assertThat(response.getJob()).isEqualTo("Senior QA");
            assertThat(response.getId()).isNotNull();
            assertThat(response.getCreatedAt()).isNotNull();
        });

    }

    @Test
    @DisplayName("Создание пользователя с пустым name")
    void createUserWithInvalidJsonTest() {
        InvalidCreateUserRequestModel requestModel = new InvalidCreateUserRequestModel();

        InvalidCreateUserResponseModel response = step("Успешное создание пользователя", () ->
        given(ApiSpecs.baseRequestSpec)
                .body(requestModel)
                .when()
                .post("/users")
                .then()
                .spec(ApiSpecs.createdResponseSpec)
                .extract().as(InvalidCreateUserResponseModel.class)
        );

        step("Проверкаа полей ответа", () -> {
            assertThat(response.getName()).isNull();
            assertThat(response.getId()).isNotNull();
            assertThat(response.getCreatedAt()).isNotNull();
        });
    }

}
