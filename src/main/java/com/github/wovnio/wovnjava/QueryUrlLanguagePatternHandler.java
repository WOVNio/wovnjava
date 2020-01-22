package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;

class QueryUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private SupportedLanguages supportedLanguages;
    private Pattern getLangPattern;
    private Pattern hasQueryPattern;

    QueryUrlLanguagePatternHandler(Lang defaultLang, SupportedLanguages supportedLanguages) {
        this.defaultLang = defaultLang;
        this.supportedLanguages = supportedLanguages;
        this.getLangPattern = this.buildGetLangPattern();
        this.hasQueryPattern = Pattern.compile("\\?");
    }

    Lang getLang(String url) {
        String languageIdentifier = this.getLangMatch(url, this.getLangPattern);
        Lang lang = this.supportedLanguages.get(languageIdentifier);
        if (lang != null) {
            return lang;
        } else if (this.supportedLanguages.hasLangCodeAliasForDefaultLang) {
            return null;
        } else {
            return this.defaultLang;
        }
    }

    String convertToDefaultLanguage(String url) {
        String languageIdentifier = this.getLangMatch(url, this.getLangPattern);
        String plainUrl = (languageIdentifier != null) ? this.removeLang(url, languageIdentifier) : url;

        if (this.supportedLanguages.hasLangCodeAliasForDefaultLang) {
            return this.insertLang(plainUrl, this.defaultLang);
        } else {
            return plainUrl;
        }
    }

    String convertToTargetLanguage(String url, Lang targetLang) {
        String languageIdentifier = this.getLangMatch(url, this.getLangPattern);
        if (languageIdentifier != null) {
            url = this.removeLang(url, languageIdentifier);
        }
        return this.insertLang(url, targetLang);
    }

    private String removeLang(String url, String langCode) {
        if (langCode.isEmpty()) return url;

        return url.replaceFirst("(^|\\?|&)wovn=" + langCode + "(&|$)", "$1")
                  .replaceAll("(\\?|&)$", "");
    }

    private String insertLang(String url, Lang lang) {
        String langCode = this.supportedLanguages.getAlias(lang);
        if (this.hasQueryPattern.matcher(url).find()) {
            return url + "&wovn=" + langCode;
        } else {
            return url + "?wovn=" + langCode;
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
