package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

/*
 * Note: For query pattern, language alias for default language has no meaning.
 * Setting it will not cause errors, but it will never be used for default language.
 */
class QueryUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private LanguageAliases languageAliases;
    private Pattern getLangPattern;
    private Pattern hasQueryPattern;

    QueryUrlLanguagePatternHandler(Lang defaultLang, LanguageAliases languageAliases) {
        this.defaultLang = defaultLang;
        this.languageAliases = languageAliases;
        this.getLangPattern = this.buildGetLangPattern();
        this.hasQueryPattern = Pattern.compile("\\?");
    }

    Lang getLang(String url) {
        String languageIdentifier = this.findLanguageIdentifier(url, this.getLangPattern);
        Lang lang = this.languageAliases.getLanguageFromAlias(languageIdentifier);
        return (lang != null) ? lang : this.defaultLang;
    }

    String convertToDefaultLanguage(String url) {
        String languageIdentifier = this.findLanguageIdentifier(url, this.getLangPattern);
        return (languageIdentifier != null) ? this.removeLangCode(url, languageIdentifier) : url;
    }

    String convertToTargetLanguage(String url, Lang targetLang) {
        if (targetLang == this.defaultLang) {
            return this.convertToDefaultLanguage(url);
        }

        String languageIdentifier = this.findLanguageIdentifier(url, this.getLangPattern);
        if (languageIdentifier != null) {
            url = this.removeLangCode(url, languageIdentifier);
        }
        return this.insertLang(url, targetLang);
    }

    private String removeLangCode(String url, String langCode) {
        if (langCode.isEmpty()) return url;

        return url.replaceFirst("(^|\\?|&)wovn=" + langCode + "(&|$)", "$1")
                  .replaceAll("(\\?|&)$", "");
    }

    private String insertLang(String url, Lang lang) {
        String langCode = this.languageAliases.getAliasFromLanguage(lang);
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
