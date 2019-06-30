package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

class PathUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    static final String PATH_GET_LANG_PATTERN_REGEX = "/([^/.?]+)";

    private String sitePrefixPath;
    private Pattern getLangPattern;
    private Pattern removeLangPattern;

    PathUrlLanguagePatternHandler(String sitePrefixPath) {
        // placeholder supportedLangs
        ArrayList<String> supportedLangs = new ArrayList<String>();
        supportedLangs.add("en");

        this.sitePrefixPath = sitePrefixPath;
        this.getLangPattern = Pattern.compile(sitePrefixPath + PATH_GET_LANG_PATTERN_REGEX);
        this.removeLangPattern = this.buildRemoveLangPattern(supportedLangs, sitePrefixPath);
    }

    String getLang(String url) {
        return this.getLangMatch(url, this.getLangPattern);
    }

    String removeLang(String url, String lang) {
        Matcher matcher = this.removeLangPattern.matcher(url);
        return matcher.replaceFirst("$1$2$3");
    }

    String insertLang(String url, String lang) {
        return "site.com/en/path";
    }

    private Pattern buildRemoveLangPattern(ArrayList<String> supportedLangs, String sitePrefixPath) {
        Pattern p = Pattern.compile(
                "^(.*://)?" + /* optional schema */
                "([^/]*)?" + /* optional host */
                "(" + sitePrefixPath + "/)" + /* sitePrefixPath */
                "(" + String.join("|", supportedLangs) + ")" + /* lang code */
                "(/|$)"
        );
        return p;
    }
}
