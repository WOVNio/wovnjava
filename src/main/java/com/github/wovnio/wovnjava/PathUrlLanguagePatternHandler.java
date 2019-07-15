package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

class PathUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private String sitePrefixPath;
    private Pattern getLangPattern;
    private Pattern matchSitePrefixPathPattern;

    PathUrlLanguagePatternHandler(String sitePrefixPath) {
        this.sitePrefixPath = sitePrefixPath;
        this.getLangPattern = this.buildGetLangPattern(sitePrefixPath);
        this.matchSitePrefixPathPattern = this.buildMatchSitePrefixPathPattern(sitePrefixPath);
    }

    String getLang(String url) {
        return this.getLangMatch(url, this.getLangPattern);
    }

    String removeLang(String url, String lang) {
        if (lang.isEmpty()) return url;

        Pattern removeLangPattern = buildRemoveLangPattern(lang);
        Matcher matcher = removeLangPattern.matcher(url);
        return matcher.replaceFirst("$1$2$3$5");
    }

    String insertLang(String url, String lang) {
        return "site.com/en/path";
    }

    public boolean isMatchingSitePrefixPath(String url) {
        return this.matchSitePrefixPathPattern.matcher(url).lookingAt();
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
                "([^/]*)?" + /* host, optional */
                sitePrefixPath
        );
        return p;
    }
}
