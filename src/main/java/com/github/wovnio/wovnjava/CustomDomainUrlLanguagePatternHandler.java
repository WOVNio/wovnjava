package com.github.wovnio.wovnjava;

class CustomDomainUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private CustomDomainLanguages customDomainLanguages;

    CustomDomainUrlLanguagePatternHandler(CustomDomainLanguages customDomainLanguages) {
        this.customDomainLanguages = customDomainLanguages;
    }

    String getLang(String url) {
        return "";
    }

    String removeLang(String url, String lang) {
        return url;
    }

    String insertLang(String url, String lang) {
        return url;
    }
}
