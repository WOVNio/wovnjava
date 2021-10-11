package com.github.wovnio.wovnjava;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

final class FormUrlEncoding {
    public static String encode(Map<String, String> params) throws UnsupportedOperationException {
        return params.entrySet().stream()
            .map(p -> encodeValue(p.getKey()) + "=" + encodeValue(p.getValue()))
            .reduce((p1, p2) -> p1 + "&" + p2)
            .orElse("");
    }

    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
