package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class QueryPatternHandler extends PatternHandler {
    static final String QueryPatternRegex = "(?:(?:\\?.*&)|\\?)wovn=([^&]+)(?:&|$)";

    private Pattern getLangPattern;

    QueryPatternHandler() {
        this.getLangPattern = Pattern.compile(QueryPatternRegex);
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
