package ru.qascooter.tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.qascooter.dto.requestbody.AddCourier;

import java.util.stream.Stream;
import ru.qascooter.utilities.Utilities;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("1. Создание курьера: эндпоинт '/api/v1/courier'")
public class TestCourierEndpoint extends BaseApiTest{

    //тесты
    @Test
    @DisplayName("1.1. Cоздание курьера - позитивный кейс")
    @Description("Проверяемое требование: курьера можно создать, успешный запрос возвращает ok: true;")
    public void testCreateCourier_PositiveCase() {
        AddCourier addCourier = Utilities.createRandomCourier();

        sendPostRequestCreateCourier(addCourier);

        tryDeleteCourier(addCourier.getLogin(), addCourier.getPassword());

    }

    @Test
    @DisplayName("1.2. Нельзя создать двух одинаковых курьеров - негативный кейс")
    @Description("Проверяемое требование: нельзя создать двух одинаковых курьеров")
    public void testCreateTwoIdentialCourier_NegativeCase() {
        AddCourier addCourier = Utilities.createRandomCourier();

        sendPostRequestCreateCourier(addCourier);
        sendPostRequestCreateTwoIdenticalCourier(addCourier);

        tryDeleteCourier(addCourier.getLogin(), addCourier.getPassword());

    }

    @ParameterizedTest
    @MethodSource("invalidCourierData")
    @DisplayName("1.3. Проверить, что курьер не создается, если не переданы все обязательные поля")
    @Description("Проверяемой требование: чтобы создать курьера, нужно передать в ручку все обязательные поля," +
            " если одного из полей нет, запрос возвращает ошибку ")
    public void testThatCourierNotCreatedWithoutSomeRequiredField_NegativeCase(AddCourier addCourier, Integer expectedStatus){

        Response response = sendPostRequestCreateCourierWithoutSomeRequiredField(addCourier, expectedStatus);

        tryDeleteCourier(addCourier.getLogin(), addCourier.getPassword());

    }

    @Test
    @DisplayName("1.4. Нельзя создать пользователя с логином, который уже есть")
    @Description("Проверяемой требование: если создать пользователя с логином, который уже есть, возвращается ошибка.")
    public void testCreateCourier_loginIsAlreadyInSystem() {
        String loginCourier = Utilities.generateUniqueLogin();

        AddCourier firstCourier  = Utilities.createRandomCourier();

        sendPostRequestCreateCourier(firstCourier);

        AddCourier secondCourier = Utilities.createCourierWithSameLogin(firstCourier.getLogin());

        sendPostRequestCreateCourierloginIsAlreadyInSystem(secondCourier);

        tryDeleteCourier(firstCourier.getLogin(), firstCourier.getPassword());
        tryDeleteCourier(secondCourier.getLogin(), secondCourier.getPassword());

    }

    //  шаги, которые участвуют в тестах
    @Step("Отправка POST запроса создание курьера на эндпоинт: /api/v1/courier")
    public void sendPostRequestCreateCourier(AddCourier addCourier){

        Response response = qaScooterApiClient.sendPostRequestCreateCourier(addCourier);

        response.then().assertThat().statusCode(201);
        response.then().assertThat().body("ok", equalTo(true));

        Allure.step(String.format("Получен statusCode: %s. Новый курьер успешно создан.", response.statusCode()));

    }

    @Step("Отправка POST запроса создание курьера - идентичные данные")
    public void sendPostRequestCreateTwoIdenticalCourier(AddCourier addCourier){
        int expectedStatus = 409;

        Response response = qaScooterApiClient.sendPostRequestCreateCourier(addCourier);

        int actualStatus = response.statusCode();

        assertEquals(expectedStatus, actualStatus,
                String.format("Ожидали statusCode: %s.\n Получили statusCode: %s.\n Тело ответа: %s",
                        expectedStatus, actualStatus, response.body().asString()));

        Allure.step(String.format("Получен statusCode: %s. Нельзя создавать 2ух одинаковых курьеров", response.statusCode()));

    }

    @Step("Отправка POST запроса создание курьера - логин курьера уже есть в системе")
    public void sendPostRequestCreateCourierloginIsAlreadyInSystem(AddCourier addCourier){
        int expectedStatus = 409;

        Response response = qaScooterApiClient.sendPostRequestCreateCourier(addCourier);

        int actualStatus = response.statusCode();

        assertEquals(expectedStatus, actualStatus,
                String.format("Ожидали statusCode: %s.\n Получили statusCode: %s.\n Тело ответа: %s",
                        expectedStatus, actualStatus, response.body().asString()));

        Allure.step(String.format(
                "Получен statusCode: %s. Нельзя создавать  курьера, логин которого уже есть в системе",
                response.statusCode()));

    }

    @Step("Отправка POST запроса создание курьера без одного из полей в запросе")
    public Response sendPostRequestCreateCourierWithoutSomeRequiredField(AddCourier addCourier, int expectedStatus){

        Response response = qaScooterApiClient.sendPostRequestCreateCourier(addCourier);

        int actualStatus = response.statusCode();

        assertEquals(expectedStatus, actualStatus,
                String.format("Ожидали statusCode: %s.\n Получили statusCode: %s.\n Тело ответа: %s",
                        expectedStatus, actualStatus, response.body().asString()));
        return response;
    }



    // параметризация
    static Stream<Arguments> invalidCourierData() {

        AddCourier addCourier1  = new AddCourier(Utilities.generateUniqueLogin(), Utilities.generateUniquePassword(), null);
        AddCourier addCourier2  = new AddCourier(Utilities.generateUniqueLogin(), null, "Федор");
        AddCourier addCourier3  = new AddCourier(null, Utilities.generateUniquePassword(), "Мария");

        return Stream.of(
                Arguments.of(addCourier1, 400),
                Arguments.of(addCourier2, 400),
                Arguments.of(addCourier3, 400)
        );
    }

}
