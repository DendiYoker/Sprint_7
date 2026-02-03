package ru.qascooter.utilities;

import ru.qascooter.dto.requestbody.AddCourier;

import java.util.UUID;

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

    public static AddCourier createRandomCourier() {
        return new AddCourier(generateUniqueLogin(), generateUniquePassword(), generateUniqueName());
    }

    public static AddCourier createCourierWithSameLogin(String login) {
        return new AddCourier(login, generateUniquePassword(), generateUniqueName());
    }

}
