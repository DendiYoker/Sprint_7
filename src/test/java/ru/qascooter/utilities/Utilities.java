package ru.qascooter.utilities;

import com.github.javafaker.Faker;
import ru.qascooter.dto.requestbody.AddCourier;
import ru.qascooter.dto.requestbody.CreateOrders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;

public class Utilities {

    private static final Faker FAKER = new Faker(new Locale("ru"));
    private static final Faker FAKER_EN = new Faker();

    private Utilities() {}

    public static String generateUniqueLogin() {
        return FAKER_EN.name().username().toLowerCase() +
                ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    public static String generateUniquePassword(){
        return UUID.randomUUID().toString().substring(0, 6);
    }

    public static String generateUniqueName(){
        return  FAKER.name().firstName();
    }

    public static String generateUniqueLastName(){
        return FAKER.name().lastName();
    }

    public static String generateUniqueAddress(){
        return FAKER.address().fullAddress();
    }

    public static String generateUniquePhoneNumber(){
        return FAKER.phoneNumber().phoneNumber();
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
        return LocalDate.now()
                .plusDays(1)
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
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
