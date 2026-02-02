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
import ru.qascooter.dto.responcebody.LoginResponse;

import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("1. Тестирование эндпоинта '/api/v1/courier'")
public class TestEndpointCourier extends BaseApiTest{


    private static String generateUniqueLogin() {
        return "courier_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static String generateUniquePassword(){
        return UUID.randomUUID().toString().substring(0, 6);
    }

    private static String generateUniqueName(){
        return "Петр_" + UUID.randomUUID().toString().substring(0, 4);
    }

    private static AddCourier createRandomCourier() {
        return new AddCourier(generateUniqueLogin(), generateUniquePassword(), generateUniqueName());
    }

    private static AddCourier createCourierWithSameLogin(String login) {
        return new AddCourier(login, generateUniquePassword(), generateUniqueName());
    }

    //тесты
    @Test
    @DisplayName("1.1. Cоздание курьера - позитивный кейс")
    @Description("Проверяемое требование: курьера можно создать, успешный запрос возвращает ok: true;")
    public void testCreateCourier_PositiveCase() {
        AddCourier addCourier = createRandomCourier();

        sendPostRequestCreateCourier(addCourier);

        tryDeleteCourier(addCourier.getLogin(), addCourier.getPassword());

    }

    @Test
    @DisplayName("1.2. Нельзя создать двух одинаковых курьеров - негативный кейс")
    @Description("Проверяемое требование: нельзя создать двух одинаковых курьеров")
    public void testCreateTwoIdentialCourier_NegativeCase() {
        AddCourier addCourier = createRandomCourier();

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
        String loginCourier = generateUniqueLogin();

        AddCourier firstCourier  = createRandomCourier();

        sendPostRequestCreateCourier(firstCourier);

        AddCourier secondCourier = createCourierWithSameLogin(firstCourier.getLogin());

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

    @Step("Если курьер создан, то удалить его после тестирования")
    private void tryDeleteCourier(String login, String password) {
        Allure.step("Попытка авторизации курьера для получения id", () -> {
            Response loginResponse = qaScooterApiClient.sendPostRequestloginCourier(login, password);

            if (loginResponse.statusCode() == 200) {
                LoginResponse loginResponseBody = loginResponse.as(LoginResponse.class);
                String id = loginResponseBody.getId();
                Allure.step(String.format("Id курьера найдено '%s'", id));

                Allure.step("Отправка DELETE запроса для удаления курьера с id: " + id, () -> {
                    Response deleteResponse = qaScooterApiClient.sendDeleteRequestCourier(id);
                    deleteResponse.then().assertThat().statusCode(200);
                });

                Allure.step(String.format("Курьер под номером \"%s\" удален", id));
            } else {
                Allure.step(String.format("Курьер '%s' не найден в системе (статус: %d), удаление не требуется",
                        login, loginResponse.statusCode()));
                Allure.step(String.format("Ответ на запрос логирования '%s'", loginResponse.statusCode()));
            }
        });
    }

    // параметризация
    static Stream<Arguments> invalidCourierData() {

        AddCourier addCourier1  = new AddCourier(generateUniqueLogin(), generateUniquePassword(), null);
        AddCourier addCourier2  = new AddCourier(generateUniqueLogin(), null, "Федор");
        AddCourier addCourier3  = new AddCourier(null, generateUniquePassword(), "Мария");

        return Stream.of(
                Arguments.of(addCourier1, 400),
                Arguments.of(addCourier2, 400),
                Arguments.of(addCourier3, 400)
        );
    }

}
