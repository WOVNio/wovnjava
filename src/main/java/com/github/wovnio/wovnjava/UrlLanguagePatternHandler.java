package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

abstract class UrlLanguagePatternHandler {
    abstract String getLang(String url);

    abstract String removeLang(String url, String lang);

    abstract String insertLang(String url, String lang);

    protected String getLangMatch(String url, Pattern pattern) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String lang = matcher.group(1);
            if (lang != null && lang.length() > 0 && Lang.getLang(lang) != null) {
                String langCode = Lang.getCode(lang);
                if (langCode != null && langCode.length() > 0) {
                    return langCode;
                }
            }
        }
        return "";
    }
}
