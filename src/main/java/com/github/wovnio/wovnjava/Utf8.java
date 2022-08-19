package com.github.wovnio.wovnjava;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Utf8 {

    // https://docs.oracle.com/javase/jp/6/technotes/guides/intl/encoding.doc.html
    static final String[] ENCODINGS = new String[] {
            "UTF-8",
            "Shift_JIS",
            "windows-31j",
            "EUC-JP",
            "x-euc-jp-linux",
            "x-eucJP-Open",
    };

    static String detectEncoding(byte[] data) {
        for (String encoding : ENCODINGS) {
            byte[] converted;
            try {
                converted = (new String(data, encoding)).getBytes(encoding);
            } catch (UnsupportedEncodingException e) {
                WovnLogger.log("UnsupportedEncodingException while detecting encoding: ", e);
                continue;
            }
            if (Arrays.equals(converted, data)) {
                return encoding;
            }
        }
        return "UTF-8";
    }

    static String toStringUtf8(byte[] data) {
        String encoding = detectEncoding(data);

        WovnLogger.log("encoding: " + encoding);

        if (encoding.equals("UTF-8")) {
            return new String(data);
        }
        try {
            return new String(data, encoding);
        } catch (UnsupportedEncodingException e) {
            WovnLogger.log("UnsupportedEncodingException while encoding to UTF-8: ", e);
            return new String(data);
        }
    }
}
