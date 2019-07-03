package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class QueryUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Pattern getLangPattern;

    QueryUrlLanguagePatternHandler() {
        this.getLangPattern = this.buildGetLangPattern();
    }

    String getLang(String url) {
        return this.getLangMatch(url, this.getLangPattern);
    }

    String removeLang(String url, String lang) {
        return url.replaceFirst("(^|\\?|&)wovn=" + lang + "(&|$)", "$1")
                  .replaceAll("(\\?|&)$", "");
    }

    String insertLang(String url, String lang) {
        return "site.com/path?wovn=en";
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
