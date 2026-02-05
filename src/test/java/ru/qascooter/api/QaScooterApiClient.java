package ru.qascooter.api;


import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.qascooter.dto.requestbody.AddCourier;
import ru.qascooter.dto.requestbody.CreateOrders;
import ru.qascooter.dto.requestbody.LoginCourier;

import static io.restassured.RestAssured.given;

public class QaScooterApiClient {

    public QaScooterApiClient() {
        RestAssured.baseURI = QaScooterApiConfig.BASE_URL;
        RestAssured.filters(new AllureRestAssured()
                .setRequestTemplate("http-request.ftl")
                .setResponseTemplate("http-response.ftl"));
    }

    //создать курьера
    public Response sendPostRequestCreateCourier(AddCourier addCourier){

        String endpoint = QaScooterApiConfig.COURIER_ADD_ENDPOINT;

        return given()
                .header("Content-Type", "application/json")
                .body(addCourier)
                .when()
                .post(endpoint);
    }

    //залогиниться под курьером
    public Response sendPostRequestloginCourier(String login, String password){
        String endpoint = QaScooterApiConfig.COURIER_LOGIN_ENDPOINT;
        LoginCourier loginCourier = new LoginCourier(login, password);

        return given()
                .header("Content-Type", "application/json")
                .body(loginCourier)
                .when()
                .post(endpoint);
    }

    //удалить курьера
    public Response sendDeleteRequestCourier(String id){
        String endpoint = QaScooterApiConfig.COURIER_DEL_ENDPOINT.replace("{id}", id);

        return given()
                .header("Content-Type", "application/json")
                .when()
                .delete(endpoint);
    }

    //Создать заказ
    public Response sendPostOrders(CreateOrders createOrders){
        String endpoint = QaScooterApiConfig.ORDERS;

        return given()
                .header("Content-Type", "application/json")
                .body(createOrders)
                .when()
                .post(endpoint);
    }


    //Получить список из 10 заказов
    public Response sendGetRequestOrdersList(){
        String endpoint = QaScooterApiConfig.ORDERS;

        return given()
                .header("Content-Type", "application/json")
                .queryParam("limit", 10).queryParam("page", 0)
                .when()
                .get(endpoint);
    }



}
