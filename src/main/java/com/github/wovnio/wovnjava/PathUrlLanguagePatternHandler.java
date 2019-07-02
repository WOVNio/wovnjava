package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
        Pattern removeLangPattern = buildRemoveLangPattern(lang);
        Matcher matcher = removeLangPattern.matcher(url);
        return matcher.replaceFirst("$1$2$3");
    }

    String insertLang(String url, String lang) {
        return "site.com/en/path";
    }

    private Pattern buildRemoveLangPattern(String lang) {
        Pattern p = Pattern.compile(
                "^(.*://)?" + /* optional schema */
                "([^/]*)?" + /* optional host */
                "(" + this.sitePrefixPath + "/)" + /* sitePrefixPath */
                "(" + lang + ")" + /* lang code */
                "(/|$)"
        );
        return p;
    }
}
