package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class QueryUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    static final String QUERY_GET_LANG_PATTERN_REGEX = "(?:(?:\\?.*&)|\\?)wovn=([^&]+)(?:&|$)";

    private Pattern getLangPattern;

    QueryUrlLanguagePatternHandler() {
        this.getLangPattern = Pattern.compile(QUERY_GET_LANG_PATTERN_REGEX);
    }

    String getLang(String url) {
        return this.getLangMatch(url, this.getLangPattern);
    }

    String removeLang(String url) {
        return "site.com/path";
    }

    String insertLang(String url, String lang) {
        return "site.com/path?wovn=en";
    }
}
