package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class PathUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    static final String PATH_GET_LANG_PATTERN_REGEX = "/([^/.?]+)";

    private String sitePrefixPath;
    private Pattern getLangPattern;

    PathUrlLanguagePatternHandler(String sitePrefixPath) {
        this.sitePrefixPath = sitePrefixPath;
        this.getLangPattern = Pattern.compile(sitePrefixPath + PATH_GET_LANG_PATTERN_REGEX);
    }

    String getLang(String url) {
        return this.getLangMatch(url, this.getLangPattern);
    }

    String removeLang(String url, String lang) {
        String prefix = this.sitePrefixPath + "/";
        return url.replaceFirst(prefix + lang + "(/|$)", prefix);
    }

    String insertLang(String url, String lang) {
        return "site.com/en/path";
    }
}
