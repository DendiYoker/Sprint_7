package ru.qascooter.tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.qascooter.dto.requestbody.AddCourier;
import ru.qascooter.dto.responcebody.LoginResponse;
import ru.qascooter.utilities.Utilities;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("2. Логин курьера: эндпоинт '/api/v1/courier/login'")
public class TestLoginEndpoint extends BaseApiTest {

    @Test
    @DisplayName("2.1. Курьер может авторизоваться - позитивный кейс")
    @Description("Проверяемое требование: курьер может авторизоваться, успешный запрос возвращает id")
    public void testLoginCourier() {
        AddCourier addCourier = Utilities.createRandomCourier();

        sendPostRequestCreateCourier(addCourier);
        sendPostLoginCourier(addCourier.getLogin(), addCourier.getPassword());

        tryDeleteCourier(addCourier.getLogin(), addCourier.getPassword());
    }


    @Test
    @DisplayName("2.2. Негативный кейс переданы не все поля")
    @Description("Проверяемое требование: для авторизации нужно передать все обязательные поля, если какого-то поля нет, запрос возвращает ошибку;")
    public void testLoginCourier_WithoutSomeRequiredField() {
        AddCourier addCourier = Utilities.createRandomCourier();

        sendPostRequestCreateCourier(addCourier);

        Allure.step("В запросе не передан поле login", () -> {
            sendPostLoginCourierWithoutSomeRequiredField(null, addCourier.getPassword());
        });

        Allure.step("В запросе не передан поле password", () -> {
            sendPostLoginCourierWithoutSomeRequiredField(addCourier.getLogin(), null);
        });

        tryDeleteCourier(addCourier.getLogin(), addCourier.getPassword());
    }

    @Test
    @DisplayName("2.3. Негативный кейс - указан неверный логин или пароль")
    @Description("Проверяемое требование: система вернёт ошибку, если неправильно указать логин или пароль;")
    public void testLoginCourier_IncorrectLoginOrPassword() {
        AddCourier addCourier = Utilities.createRandomCourier();

        sendPostRequestCreateCourier(addCourier);

        Allure.step("Авторизация под неверным логином", () -> {
            sendPostLoginIncorrectPasswordOrLogin("negativ_login", addCourier.getPassword());
        });

        Allure.step("Авторизация под верным логином, но неверным паролем", () -> {
            sendPostLoginIncorrectPasswordOrLogin(addCourier.getLogin(), "negativ_password");
        });

        tryDeleteCourier(addCourier.getLogin(), addCourier.getPassword());
    }

    @Test
    @DisplayName("2.4. Негативный кейс - авторизация под не существующем пользователем")
    @Description("Проверяемое требование: если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;")
    public void testLoginCourier_AuthorizationNonExistingUser() {
        AddCourier addCourier = Utilities.createRandomCourier();
        // пробуем на всякий удалить из системы
        tryDeleteCourier(addCourier.getLogin(), addCourier.getPassword());

        sendPostLoginIncorrectPasswordOrLogin(addCourier.getLogin(), addCourier.getPassword());

        tryDeleteCourier(addCourier.getLogin(), addCourier.getPassword());
    }


    // шаги
    @Step("Отправка POST запроса создание курьера на эндпоинт: /api/v1/courier")
    public void sendPostRequestCreateCourier(AddCourier addCourier){
        Response response = qaScooterApiClient.sendPostRequestCreateCourier(addCourier);

        response.then().assertThat().statusCode(201);

        Allure.step(String.format("Получен statusCode: %s. Новый курьер успешно создан.", response.statusCode()));
    }

    @Step("Авторизация под существующем курьером")
    protected void sendPostLoginCourier(String login, String password) {

        Response response = qaScooterApiClient.sendPostRequestloginCourier(login, password);

        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("id", notNullValue());

        LoginResponse loginResponse = response.as(LoginResponse.class);
        Allure.step(String.format("Авторизация прошла успешно, id курьера '%s'", loginResponse.getId()));

    }

    @Step("Авторизация c неверным паролем/логином/несуществующем пользователем")
    protected void sendPostLoginIncorrectPasswordOrLogin(String login, String password) {
        int expectedStatus = 404;
        Response response = qaScooterApiClient.sendPostRequestloginCourier(login, password);
        int actualStatus = response.statusCode();

        assertEquals(expectedStatus, actualStatus,
                String.format("Ожидали statusCode: %s.\n Получили statusCode: %s.\n Тело ответа: %s",
                        expectedStatus, actualStatus, response.body().asString()));

    }


    @Step("Авторизация с неполными данными")
    protected void sendPostLoginCourierWithoutSomeRequiredField(String login, String password) {
        int expectedStatus = 400;
        Response response = qaScooterApiClient.sendPostRequestloginCourier(login, password);
        int actualStatus = response.statusCode();

        assertEquals(expectedStatus, actualStatus,
                String.format("Ожидали statusCode: %s.\n Получили statusCode: %s.\n Тело ответа: %s",
                        expectedStatus, actualStatus, response.body().asString()));


    }



}
