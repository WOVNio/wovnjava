package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;

class QueryUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private ArrayList<Lang> supportedLangs;
    private Pattern getLangPattern;
    private Pattern hasQueryPattern;

    QueryUrlLanguagePatternHandler(Lang defaultLang, ArrayList<Lang> supportedLangs) {
        this.defaultLang = defaultLang;
        this.supportedLangs = supportedLangs;
        this.getLangPattern = this.buildGetLangPattern();
        this.hasQueryPattern = Pattern.compile("\\?");
    }

    Lang getLang(String url) {
        Lang lang = this.getLangMatch(url, this.getLangPattern);
        return (lang != null && this.supportedLangs.contains(lang)) ? lang : null;
    }

    String convertToDefaultLanguage(String url) {
        Lang currentLang = this.getLang(url);
        if (currentLang == null) {
            return url;
        } else {
            return this.removeLang(url, currentLang.code);
        }
    }

    private String removeLang(String url, String lang) {
        if (lang.isEmpty()) return url;

        return url.replaceFirst("(^|\\?|&)wovn=" + lang + "(&|$)", "$1")
                  .replaceAll("(\\?|&)$", "");
    }

    String insertLang(String url, String lang) {
        if (this.hasQueryPattern.matcher(url).find()) {
            return url + "&wovn=" + lang;
        } else {
            return url + "?wovn=" + lang;
        }
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
