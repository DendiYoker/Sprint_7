package ru.qascooter.api;

public class QaScooterApiConfig {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";

    private static final String COURIER_ADD_ENDPOINT = "/api/v1/courier";

    private static final String COURIER_DEL_ENDPOINT = "/api/v1/courier/{id}";

    private static final String COURIER_LOGIN_ENDPOINT = "/api/v1/courier/login";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getCourierAddEndpoint() {
        return COURIER_ADD_ENDPOINT;
    }

    public static String getCourierDeleteEndpoint(String id) {
        return COURIER_DEL_ENDPOINT.replace("{id}", id);
    }

    public static String getCourierLoginEndpoint(){
        return COURIER_LOGIN_ENDPOINT;
    }

}
