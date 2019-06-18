package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;

class SubdomainPatternHandler extends PatternHandler {
    static final String SubdomainPatternRegex = "^([^.]+)\\.";

    SubdomainPatternHandler(ArrayList<String> supportedLangs) {
        this.getLangPattern = Pattern.compile(SubdomainPatternRegex);
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

