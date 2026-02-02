package ru.qascooter.tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.qascooter.api.QaScooterApiConfig;
import ru.qascooter.dto.requestbody.AddCourier;

import static io.restassured.RestAssured.given;

@DisplayName("Тестирование эндпоинта '/api/v1/courier/login'")
public class TestLoginEndpoint extends BaseApiTest {
    private String login;
    private String password;
    private String name;


    @Test
    @DisplayName("Cоздания курьера - позитивный кейс")
    public void testLoginCourier_PositivScenario() {
        AddCourier addCourier = new AddCourier("Dendi_Yoker1", "12345", "Денис");

        Response response = sendPostRequestAddNewCourier(addCourier);

        Allure.step("Проверка ответа на запрос создания нового курьера", () -> {
            response.then().assertThat().statusCode(201);
        });

    }

    @Step("Отправка POST запрос создание курьера на эндпоинт: /api/v1/courier/login")
    public Response sendPostRequestAddNewCourier(AddCourier addCourier){

        String endpoint = QaScooterApiConfig.getCourierAddEndpoint();

        return given()
                .header("Content-Type", "application/json")
                .body(addCourier)
                .when()
                .post(endpoint);
    }

    @Step("Удалить курьера после тесирования")
    private void deleteCourier(String id){    }



}
