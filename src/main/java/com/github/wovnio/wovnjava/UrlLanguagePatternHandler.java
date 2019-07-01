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
            String langMatch = matcher.group(1);
            Lang lang = Lang.getLang(langMatch);
            if (lang != null) {
                return lang.code;
            }
        }
        return "";
    }
}
