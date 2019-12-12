package com.github.wovnio.wovnjava;

import java.util.List;
import java.lang.StringBuilder;

final class StringUtil {
    /*
     * Java 7 does not have a built-in String join, so we implement our own.
     * Implementation comes from https://www.mkyong.com/java/java-how-to-join-list-string-with-commas/
     */
    public static String join(String separator, List<String> input) {
        if (input == null || input.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.size(); i++) {

            sb.append(input.get(i));

            // if not the last item
            if (i != input.size() - 1) {
                sb.append(separator);
            }

        }

        return sb.toString();
    }
}
