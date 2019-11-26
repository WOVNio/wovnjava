package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

abstract class UrlLanguagePatternHandler {
    /*
     * Return the language declared by the url,
     * or null if the url does not specify a valid language
     */
    abstract Lang getLang(String url);

    abstract String convertToDefaultLanguage(String url);

    abstract String convertToTargetLanguage(String url, Lang lang);

    public boolean canInterceptUrl(String url) {
        return true;
    }

    public boolean shouldRedirectExplicitDefaultLangUrl(String url) {
        return false;
    }

    protected Lang getLangMatch(String url, Pattern pattern) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String langMatch = matcher.group(1);
            return Lang.get(langMatch);
        }
        return null;
    }
}
