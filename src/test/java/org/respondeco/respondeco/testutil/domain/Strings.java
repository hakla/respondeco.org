package org.respondeco.respondeco.testutil.domain;

import java.util.Random;

/**
 * Created by clemens on 30/09/15.
 */
public class Strings {

    public static Integer RANDOM_STRING_MIN_SIZE = 10;
    public static Integer RANDOM_STRING_MAX_SIZE = 20;

    private static String lastRandomLowercaseLetters = null;
    private static String lastRandomEmail = null;

    public static String randomLowercaseLetters() {
        return randomLowercaseLetters(RANDOM_STRING_MIN_SIZE, RANDOM_STRING_MAX_SIZE);
    }

    public static String randomLowercaseLetters(Integer min, Integer max) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        Integer bound = min + random.nextInt(max - min);
        for(int i = 0;i < bound; i++) {
            builder.append(Character.toChars('a' + random.nextInt('z' - 'a'))[0]);
        }
        lastRandomLowercaseLetters = builder.toString();
        return lastRandomLowercaseLetters;
    }

    public static String lastRandomLowercaseLetters() {
        return lastRandomLowercaseLetters;
    }

    public static String randomEmail() {
        lastRandomEmail = randomLowercaseLetters() + "@" + randomLowercaseLetters() + "." + randomLowercaseLetters(2,3);
        return lastRandomEmail;
    }

    public static String lastRandomEmail() {
        return lastRandomEmail;
    }

}
