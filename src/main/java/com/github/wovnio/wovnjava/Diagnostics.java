package com.github.wovnio.wovnjava;

import java.nio.charset.Charset;
import java.util.Locale;

public class Diagnostics {
    public static String getDiagnosticInfo() {
        Locale locale = Locale.getDefault();
        Charset charset = Charset.defaultCharset();

        StringBuilder sb = new StringBuilder();
        sb.append("**** Diagnostic info ****\n");
        if (locale != null) {
            sb.append("Locale: " + locale.toString() + "\n");
        }
        if (charset != null) {
            sb.append("Default charset: " + charset.displayName() + "\n");
        }
        sb.append("**** End diagnostic info ****\n");

        return sb.toString();
    }
}