package com.github.wovnio.wovnjava;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class Utf8 {
    private String encoding;

    Utf8(String encoding) {
        this.encoding = encoding;
    }

    // https://docs.oracle.com/javase/jp/6/technotes/guides/intl/encoding.doc.html
    static final String[] DEFAULT_CANDIDATE_ENCODINGS = new String[] {
            "UTF-8",
            "Shift_JIS",
            "windows-31j",
            "EUC-JP",
            "x-euc-jp-linux",
            "x-eucJP-Open",
    };

    static String detectEncoding(byte[] data) {
        for (String encoding : DEFAULT_CANDIDATE_ENCODINGS) {
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

    public String toStringUtf8(byte[] data) {
        String encoding;

        if (this.encoding == "") {
            encoding = detectEncoding(data);
            WovnLogger.log("Using automatic encoding detection.");
        } else {
            encoding = this.encoding;
            WovnLogger.log("Encoding is set by config.");
        }

        WovnLogger.log("encoding: " + encoding);
        
        try {
            return new String(data, encoding);
        } catch (UnsupportedEncodingException e) {
            WovnLogger.log("UnsupportedEncodingException while encoding to UTF-8: ", e);
            return new String(data);
        }
    }
}
