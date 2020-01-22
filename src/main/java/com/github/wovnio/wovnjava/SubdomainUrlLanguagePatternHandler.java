package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;

class SubdomainUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private SupportedLanguages supportedLanguages;
    private Pattern getLangPattern;

    SubdomainUrlLanguagePatternHandler(Lang defaultLang, SupportedLanguages supportedLanguages) {
        this.defaultLang = defaultLang;
        this.supportedLanguages = supportedLanguages;
        this.getLangPattern = this.buildGetLangPattern();
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
        Lang currentLang = this.getLang(url);
        if (currentLang == null || currentLang == this.defaultLang) {
            return url;
        }

        String newUrl = this.removeLang(url, currentLang);
        if (this.supportedLanguages.hasLangCodeAliasForDefaultLang) {
            return this.insertLang(newUrl, this.defaultLang);
        } else {
            return newUrl;
        }
    }

    String convertToTargetLanguage(String url, Lang targetLang) {
        String languageIdentifier = this.getLangMatch(url, this.getLangPattern);
        Lang currentLang = this.supportedLanguages.get(languageIdentifier);
        if (currentLang != null) {
            url = this.removeLang(url, currentLang);
        }
        return this.insertLang(url, targetLang);
    }

    private String removeLang(String url, Lang lang) {
        String langCode = this.supportedLanguages.getAlias(lang);

        return Pattern.compile("(^|(//))" + langCode + "\\.", Pattern.CASE_INSENSITIVE)
                      .matcher(url).replaceFirst("$1");
    }

    private String insertLang(String url, Lang lang) {
        String langCode = this.supportedLanguages.getAlias(lang);
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
