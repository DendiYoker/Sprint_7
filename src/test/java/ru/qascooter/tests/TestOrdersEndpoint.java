package ru.qascooter.tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.qascooter.api.QaScooterApiClient;
import ru.qascooter.dto.requestbody.AddCourier;
import ru.qascooter.dto.requestbody.CreateOrders;
import ru.qascooter.utilities.Utilities;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("3. Создание заказа: эндпоинт '/api/v1/orders' и 4. Получение списка товаров")
public class TestOrdersEndpoint extends BaseApiTest{

    //тесты
    @ParameterizedTest
    @MethodSource("colorProvider")
    @DisplayName("3.1 Создание заказа - позитивный кейс")
    @Description("Проверяемое требование: можно указать один из цветов — BLACK или GREY" +
            "можно указать оба цвета; можно совсем не указывать цвет; тело ответа содержит track.")
    public void testCreateOrder(String[] color, String testCase) {
        Allure.step(String.format("Проверяем случай: %s.\n color содержит: %s",testCase, Arrays.toString(color)));

        Response response = sendPostRequestCreateOrders(Utilities.createOrdersBody(color));

        Allure.step("Проверка полученного результата", () -> {
            response.then().assertThat().statusCode(201);
            response.then().assertThat().body("track", notNullValue());
        });

        Allure.step(String.format("Заказ создан, Получили statusCode: %s.\n Тело ответа: %s",
                response.statusCode(), response.body().asString()));

    }

    @Test
    @DisplayName("4.1 Получение списка заказов")
    @Description("Проверь, что в тело ответа возвращается список заказов.")
    public void testGetOrdersList() {

        Response response = sendGetRequestListOrders();

        Allure.step("Проверка полученного результата", () -> {
            response.then().assertThat().statusCode(200);
            response.then()
                    .assertThat()
                    .body("orders", notNullValue())  // поле orders не null
                    .body("orders", instanceOf(java.util.List.class)); // это список

            int ordersCount = response.jsonPath().getList("orders").size();
            Allure.step(String.format("Получено заказов: %d", ordersCount));
        });

    }

    //  шаги, которые участвуют в тестах

    @Step("Отправка POST запроса создание заказа")
    public Response sendPostRequestCreateOrders(CreateOrders createOrders){

        return qaScooterApiClient.sendPostOrders(createOrders);

    }

    @Step("Отправка GET запроса получения списка заказов")
    public Response sendGetRequestListOrders(){

        return qaScooterApiClient.sendGetRequestOrdersList();

    }


    // параметризация
    static Stream<Arguments> colorProvider() {

        return Stream.of(
                Arguments.of(new String[]{"BLACK"}, "Заказ с черным самокатом"),
                Arguments.of(new String[0], "Заказ без выбора цвета"),
                Arguments.of(new String[]{"BLACK", "GREY"}, "Заказ с двумя цветами")
        );
    }

}
