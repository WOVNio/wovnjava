package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class PathUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private ArrayList<Lang> supportedLangs;
    private String sitePrefixPath;
    private Pattern getLangPattern;
    private Pattern matchSitePrefixPathPattern;

    PathUrlLanguagePatternHandler(Lang defaultLang, ArrayList<Lang> supportedLangs, String sitePrefixPath) {
        this.defaultLang = defaultLang;
        this.supportedLangs = supportedLangs;
        this.sitePrefixPath = sitePrefixPath;
        this.getLangPattern = this.buildGetLangPattern(sitePrefixPath);
        this.matchSitePrefixPathPattern = this.buildMatchSitePrefixPathPattern(sitePrefixPath);
    }

    Lang getLang(String url) {
        Lang lang = this.getLangMatch(url, this.getLangPattern);
        return (lang != null && this.supportedLangs.contains(lang)) ? lang : null;
    }

    String removeLang(String url, String lang) {
        if (lang.isEmpty()) return url;

        Pattern removeLangPattern = buildRemoveLangPattern(lang);
        Matcher matcher = removeLangPattern.matcher(url);
        return matcher.replaceFirst("$1$2$3$5");
    }

    String insertLang(String url, String lang) {
        return this.matchSitePrefixPathPattern.matcher(url).replaceFirst("$1$2$3/" + lang + "$4");
    }

    public boolean canInterceptUrl(String url) {
        return this.matchSitePrefixPathPattern.matcher(url).lookingAt();
    }

    /*
     * Redirect to same URL without language code if the language code
     * found in the URL path is for default language
     */
    public boolean shouldRedirectToDefaultLang(String url) {
        Lang pathLang = this.getLangMatch(url, this.getLangPattern);
        return pathLang == this.defaultLang;
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
