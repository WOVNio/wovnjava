package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class SubdomainUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private LanguageAliases languageAliases;
    private Pattern getLangPattern;

    SubdomainUrlLanguagePatternHandler(Lang defaultLang, LanguageAliases languageAliases) {
        this.defaultLang = defaultLang;
        this.languageAliases = languageAliases;
        this.getLangPattern = this.buildGetLangPattern();
    }

    Lang getLang(String url) {
        String languageIdentifier = this.resolvePatternMatch(url, this.getLangPattern);
        Lang lang = this.languageAliases.getLanguageFromAlias(languageIdentifier);
        if (lang != null) {
            return lang;
        } else if (this.languageAliases.hasAliasForDefaultLang) {
            return null;
        } else {
            return this.defaultLang;
        }
    }

    String convertToDefaultLanguage(String url) {
        Lang currentLang = this.getLang(url);
        if (currentLang == null) {
            return url;
        }

        String newUrl = this.removeLang(url, currentLang);
        if (this.languageAliases.hasAliasForDefaultLang) {
            return this.insertLang(newUrl, this.defaultLang);
        } else {
            return newUrl;
        }
    }

    String convertToTargetLanguage(String url, Lang targetLang) {
        if (targetLang == this.defaultLang) {
            return this.convertToDefaultLanguage(url);
        }

        String languageIdentifier = this.resolvePatternMatch(url, this.getLangPattern);
        Lang currentLang = this.languageAliases.getLanguageFromAlias(languageIdentifier);
        if (currentLang != null) {
            String newUrl = this.removeLang(url, currentLang);
            return this.insertLang(newUrl, targetLang);
        } else if (this.languageAliases.hasAliasForDefaultLang) {
            return url;
        } else {
            return this.insertLang(url, targetLang);
        }
    }

    private String removeLang(String url, Lang lang) {
        String langCode = this.languageAliases.getAliasFromLanguage(lang);

        return Pattern.compile("(^|(//))" + langCode + "\\.", Pattern.CASE_INSENSITIVE)
                      .matcher(url).replaceFirst("$1");
    }

    private String insertLang(String url, Lang lang) {
        String langCode = this.languageAliases.getAliasFromLanguage(lang);
        if (url.contains("://")) {
            return url.replaceFirst("://", "://" + langCode + ".");
        } else if (url.startsWith("/")) {
            return url;
        } else {
            return langCode + "." + url;
        }
    }

    private Pattern buildGetLangPattern() {
        Pattern p = Pattern.compile(
                "^(?:.*://)?" + /* schema, optional non-capturing group */
                "([^.]+)\\." /* capture first subdomain, before first `.` */
        );
        return p;
    }
}
