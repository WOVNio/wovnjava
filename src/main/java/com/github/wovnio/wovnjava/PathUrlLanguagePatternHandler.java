package com.github.wovnio.wovnjava;

import java.util.Map;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class PathUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private SupportedLanguages supportedLanguages;
    private String sitePrefixPath;
    private Pattern getLangPattern;
    private Pattern matchSitePrefixPathPattern;

    PathUrlLanguagePatternHandler(Lang defaultLang, SupportedLanguages supportedLanguages, String sitePrefixPath) {
        this.defaultLang = defaultLang;
        this.supportedLanguages = supportedLanguages;
        this.sitePrefixPath = sitePrefixPath;
        this.getLangPattern = this.buildGetLangPattern(sitePrefixPath);
        this.matchSitePrefixPathPattern = this.buildMatchSitePrefixPathPattern(sitePrefixPath);
    }

    Lang getLang(String url) {
        String languageIdentifier = this.getLangMatch(url, this.getLangPattern);
        Lang lang = this.supportedLanguages.get(languageIdentifier);
        if (lang != null) {
            return lang;
        } else if (this.supportedLanguages.hasLangCodeAliasForDefaultLang) {
            return null;
        } else {
            return this.supportedLanguages.getDefault();
        }
    }

    String convertToDefaultLanguage(String url) {
        Lang currentLang = this.getLang(url);
        if (currentLang == null || currentLang == this.defaultLang) {
            return url;
        }

        String newUrl = this.removeLang(url, currentLang.code);
        if (this.supportedLanguages.hasLangCodeAliasForDefaultLang) {
            return this.insertLang(newUrl, this.supportedLanguages.getAlias(this.defaultLang));
        } else {
            return newUrl;
        }
    }

    String convertToTargetLanguage(String url, Lang targetLang) {
        String languageIdentifier = this.getLangMatch(url, this.getLangPattern);
        Lang currentLang = this.supportedLanguages.get(languageIdentifier);
        if (currentLang != null) {
            url = this.removeLang(url, languageIdentifier);
        }
        return this.insertLang(url, this.supportedLanguages.getAlias(targetLang));
    }

    private String removeLang(String url, String lang) {
        if (lang.isEmpty()) return url;

        Pattern removeLangPattern = buildRemoveLangPattern(lang);
        Matcher matcher = removeLangPattern.matcher(url);
        return matcher.replaceFirst("$1$2$3$5");
    }

    private String insertLang(String url, String lang) {
        return this.matchSitePrefixPathPattern.matcher(url).replaceFirst("$1$2$3/" + lang + "$4");
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
        return !this.supportedLanguages.hasLangCodeAliasForDefaultLang && languageIdentifier == this.defaultLang.code;
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
