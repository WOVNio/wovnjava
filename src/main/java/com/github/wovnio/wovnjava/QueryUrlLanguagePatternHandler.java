package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class QueryUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Pattern getLangPattern;
    private Pattern hasQueryPattern;

    QueryUrlLanguagePatternHandler() {
        this.getLangPattern = this.buildGetLangPattern();
        this.hasQueryPattern = Pattern.compile("\\?");
    }

    String getLang(String url) {
        return this.getLangMatch(url, this.getLangPattern);
    }

    String removeLang(String url, String lang) {
        if (lang.isEmpty()) return url;

        return url.replaceFirst("(^|\\?|&)wovn=" + lang + "(&|$)", "$1")
                  .replaceAll("(\\?|&)$", "");
    }

    String insertLang(String url, String lang) {
        if (this.hasQueryPattern.matcher(url).find()) {
            return url + "&wovn=" + lang;
        } else {
            return url + "?wovn=" + lang;
        }
    }

    private Pattern buildGetLangPattern() {
        Pattern p = Pattern.compile(
                "(?:(?:\\?.*&)|\\?)" + /* `?` or `?.*&`, non-capturing group */
                "wovn=" + /* wovn language parameter */
                "([^&]+)" /* match until `&` or end-of-string */
        );
        return p;
    }
}
