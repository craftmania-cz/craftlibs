package cz.craftmania.craftlibs.utils;

import java.util.Random;

public class StringUtils {

    /**
     * @param length Length of string
     * @return Random alphanumeric string of certain length
     */
    public static String randomAlphanumeric(int length) {
        Random random = new Random();
        return random.ints(48, 122 + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
