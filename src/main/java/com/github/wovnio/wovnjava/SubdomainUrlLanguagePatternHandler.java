package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;

class SubdomainUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private ArrayList<Lang> supportedLangs;
    private Pattern getLangPattern;

    SubdomainUrlLanguagePatternHandler(Lang defaultLang, ArrayList<Lang> supportedLangs) {
        this.defaultLang = defaultLang;
        this.supportedLangs = supportedLangs;
        this.getLangPattern = this.buildGetLangPattern();
    }

    Lang getLang(String url) {
        Lang lang = this.getLangMatch(url, this.getLangPattern);
        return (lang != null && this.supportedLangs.contains(lang)) ? lang : null;
    }

    String removeLang(String url, String lang) {
        if (lang.isEmpty()) return url;

        return Pattern.compile("(^|(//))" + lang + "\\.", Pattern.CASE_INSENSITIVE)
                      .matcher(url).replaceFirst("$1");
    }

    String insertLang(String url, String lang) {
        if (url.contains("://")) {
            return url.replaceFirst("://", "://" + lang + ".");
        } else if (url.startsWith("/")) {
            return url;
        } else {
            return lang + "." + url;
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
