package ru.qascooter.tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import ru.qascooter.api.QaScooterApiClient;
import ru.qascooter.dto.responcebody.LoginResponse;

public class BaseApiTest {

    protected static QaScooterApiClient qaScooterApiClient;

    @BeforeAll
    protected static void SetUp() {
        qaScooterApiClient = new QaScooterApiClient();
    }

    @Step("Если курьер создан, то удалить его после тестирования")
    protected void tryDeleteCourier(String login, String password) {
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


}
