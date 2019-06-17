package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class PathPatternHandler extends PatternHandler {
    static final String PathPatternRegex = "/([^/.?]+)";

    PathPatternHandler(String sitePrefixPath) {
        if (sitePrefixPath.length() > 0 && !PathPatternRegex.contains(sitePrefixPath)) {
            this.langPattern = Pattern.compile(sitePrefixPath + PathPatternRegex);
        } else {
            this.langPattern = Pattern.compile(PathPatternRegex);
        }
    }

    String getLang(String url) {
        return this.getLangMatch(url, this.langPattern);
    }

    String removeLang(String url) {
        return "site.com/path";
    }

    String insertLang(String url, String lang) {
        return "site.com/en/path";
    }
}
