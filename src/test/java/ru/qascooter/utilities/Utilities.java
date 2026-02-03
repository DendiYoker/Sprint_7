package ru.qascooter.utilities;

import io.restassured.RestAssured;
import ru.qascooter.api.QaScooterApiConfig;
import ru.qascooter.dto.requestbody.AddCourier;
import ru.qascooter.dto.requestbody.CreateOrders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;

public class Utilities {

    private Utilities() {}

    public static String generateUniqueLogin() {
        return "courier_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateUniquePassword(){
        return UUID.randomUUID().toString().substring(0, 6);
    }

    public static String generateUniqueName(){
        return "Петр_" + UUID.randomUUID().toString().substring(0, 4);
    }

    public static String generateUniqueLastName(){
        return "Касперский_" + UUID.randomUUID().toString().substring(0, 3);
    }

    public static String generateUniqueAddress(){
        return "Москва, ул. Московская, д. " + UUID.randomUUID().toString().substring(0, 3);
    }

    public static String generateUniquePhoneNumber(){
        return "+7 800 355 3" + UUID.randomUUID().toString().substring(3, 3);
    }

    public static String getRandomComment(){

        return given()
                .header("Content-Type", "application/json")
                .get( "https://api.kanye.rest")
                .then()
                .extract()
                .path("quote");
    }

    public static String getTomorrowDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return tomorrow.format(DateTimeFormatter.ISO_LOCAL_DATE); // "yyyy-MM-dd"
    }

    public static AddCourier createRandomCourier() {
        return new AddCourier(generateUniqueLogin(), generateUniquePassword(), generateUniqueName());
    }

    public static AddCourier createCourierWithSameLogin(String login) {
        return new AddCourier(login, generateUniquePassword(), generateUniqueName());
    }

    public static CreateOrders createOrdersBody(String[] color) {

        Random random;
        return new CreateOrders(
                generateUniqueName(),
                generateUniqueLastName(),
                generateUniqueAddress(),
                ThreadLocalRandom.current().nextInt(1, 300),
                generateUniquePhoneNumber(),
                ThreadLocalRandom.current().nextInt(1, 10),
                getTomorrowDate(),
                getRandomComment(),
                color);
    }

}
