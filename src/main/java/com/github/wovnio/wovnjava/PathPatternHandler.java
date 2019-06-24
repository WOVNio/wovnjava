package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class PathPatternHandler extends PatternHandler {
    static final String PATH_GET_LANG_PATTERN_REGEX = "/([^/.?]+)";

    private Pattern getLangPattern;

    PathPatternHandler(String sitePrefixPath) {
        this.getLangPattern = Pattern.compile(sitePrefixPath + PATH_GET_LANG_PATTERN_REGEX);
    }

    String getLang(String url) {
        return this.getLangMatch(url, this.getLangPattern);
    }

    String removeLang(String url) {
        return "site.com/path";
    }

    String insertLang(String url, String lang) {
        return "site.com/en/path";
    }
}
