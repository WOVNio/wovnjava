package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class PathUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private LanguageAliases languageAliases;
    private String sitePrefixPath;
    private Pattern getLangPattern;
    private Pattern matchSitePrefixPathPattern;

    PathUrlLanguagePatternHandler(Lang defaultLang, LanguageAliases languageAliases, String sitePrefixPath) {
        this.defaultLang = defaultLang;
        this.languageAliases = languageAliases;
        this.sitePrefixPath = sitePrefixPath;
        this.getLangPattern = this.buildGetLangPattern(sitePrefixPath);
        this.matchSitePrefixPathPattern = this.buildMatchSitePrefixPathPattern(sitePrefixPath);
    }

    Lang getLang(String url) {
        if (!this.matchSitePrefixPathPattern.matcher(url).lookingAt()) {
            return null;
        }

        String languageIdentifier = this.getLangMatch(url, this.getLangPattern);
        Lang lang = this.languageAliases.getLang(languageIdentifier);
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
        String languageIdentifier = this.getLangMatch(url, this.getLangPattern);
        Lang currentLang = this.languageAliases.getLang(languageIdentifier);
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
        String langCode = this.languageAliases.getAlias(lang);
        Pattern removeLangPattern = buildRemoveLangPattern(langCode);
        Matcher matcher = removeLangPattern.matcher(url);
        return matcher.replaceFirst("$1$2$3$5");
    }

    private String insertLang(String url, Lang lang) {
        String langCode = this.languageAliases.getAlias(lang);
        return this.matchSitePrefixPathPattern.matcher(url).replaceFirst("$1$2$3/" + langCode + "$4");
    }

    public boolean canInterceptUrl(String url) {
        return this.getLang(url) != null;
    }

    /*
     * Redirect to same URL without language code if the language code
     * found in the URL path is for default language
     */
    public boolean shouldRedirectExplicitDefaultLangUrl(String url) {
        String languageIdentifier = this.getLangMatch(url, this.getLangPattern);
        return !this.languageAliases.hasAliasForDefaultLang && this.defaultLang.code.equals(languageIdentifier);
    }

    private Pattern buildGetLangPattern(String sitePrefixPath) {
        Pattern p = Pattern.compile(
                "^(?:.*://)?" + /* schema, optional non-capturing group */
                "(?:[^/]*)?" + /* host, optional non-capturing group */
                "(?:" + sitePrefixPath + "/)" + /* sitePrefixPath, non-capturing group */
                "([^/.?]+)" /* capture next path section */
        );
        return p;
    }

    private Pattern buildRemoveLangPattern(String lang) {
        Pattern p = Pattern.compile(
                "^(.*://)?" + /* optional schema */
                "([^/?]*)?" + /* optional host */
                "(" + this.sitePrefixPath + ")" +
                "(/" + lang + ")" +
                "(/|\\?|$)" /* next path, query, or end-of-string */
        );
        return p;
    }

    private Pattern buildMatchSitePrefixPathPattern(String sitePrefixPath) {
        Pattern p = Pattern.compile(
                "^(.*://)?" + /* schema, optional */
                "([^/?]*)?" + /* host, optional */
                "(" + sitePrefixPath + ")" +
                "(/|\\?|$)" /* next path, query, or end-of-string */
        );
        return p;
    }
}
