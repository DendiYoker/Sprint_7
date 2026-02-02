package ru.qascooter.tests;

import org.junit.jupiter.api.BeforeAll;
import ru.qascooter.api.QaScooterApiClient;

public class BaseApiTest {

    protected static QaScooterApiClient qaScooterApiClient;

    @BeforeAll
    static void SetUp() {
        qaScooterApiClient = new QaScooterApiClient();
    }


}
