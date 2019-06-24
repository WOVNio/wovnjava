package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class SubdomainPatternHandler extends PatternHandler {
    static final String SUBDOMAIN_GET_LANG_PATTERN_REGEX = "^([^.]+)\\.";

    private Pattern getLangPattern;

    SubdomainPatternHandler() {
        this.getLangPattern = Pattern.compile(SUBDOMAIN_GET_LANG_PATTERN_REGEX);
    }

    String getLang(String url) {
        return this.getLangMatch(url, this.getLangPattern);
    }

    String removeLang(String url) {
        return "site.com/path";
    }

    String insertLang(String url, String lang) {
        return "en.site.com/path";
    }
}

