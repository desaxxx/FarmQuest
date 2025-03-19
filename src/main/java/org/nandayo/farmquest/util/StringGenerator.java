package org.nandayo.farmquest.util;

import org.nandayo.farmquest.FarmQuest;

import java.util.Random;

public class StringGenerator {

    static public String getRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = FarmQuest.getInstance().random;
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
