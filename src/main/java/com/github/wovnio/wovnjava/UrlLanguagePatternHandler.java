package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

abstract class UrlLanguagePatternHandler {
    /*
     * Return the language of the request.
     *
     * Return null if the given URL is outside the scope of URLs that wovnjava
     * is configured to handle. Such a request should not be intercepted.
     */
    abstract Lang getLang(String url);

    abstract String convertToDefaultLanguage(String url);

    abstract String convertToTargetLanguage(String url, Lang lang);

    public boolean shouldRedirectExplicitDefaultLangUrl(String url) {
        return false;
    }

    protected String resolvePatternMatch(String url, Pattern pattern) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String langMatch = matcher.group(1);
            if (!langMatch.isEmpty()) return langMatch;
        }
        return null;
    }
}
