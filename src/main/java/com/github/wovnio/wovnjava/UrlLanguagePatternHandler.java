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

    /*
     * Return the input URL converted into its form in default language
     *
     * If the input URL cannot be intercepted (does not match "site prefix path"
     * for example), return the input URL unchanged.
     */
    abstract String convertToDefaultLanguage(String url);

    /*
     * Return the input URL converted into the declared target language
     *
     * If the input URL cannot be intercepted (does not match "site prefix path"
     * for example), return the input URL unchanged.
     */
    abstract String convertToTargetLanguage(String url, Lang lang);

    /*
     * Return true if the input URL is identified as being in original language
     * but another form of the URL is preferred, such that the server should send
     * a HTTP 302 redirect to the preferred URL for the resource.
     *
     * (Currently only relevant for explicitly declared default language in path pattern.)
     */
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
