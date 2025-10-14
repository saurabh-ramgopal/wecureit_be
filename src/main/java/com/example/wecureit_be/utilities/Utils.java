package com.example.wecureit_be.utilities;
import java.util.Random;
import java.util.UUID;

public class Utils {

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static int generateFiveDigitNumber() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }
}