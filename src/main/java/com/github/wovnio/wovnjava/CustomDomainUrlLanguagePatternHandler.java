package com.github.wovnio.wovnjava;

import java.net.URL;
import java.net.MalformedURLException;

class CustomDomainUrlLanguagePatternHandler extends UrlLanguagePatternHandler {
    private Lang defaultLang;
    private CustomDomainLanguages customDomainLanguages;

    CustomDomainUrlLanguagePatternHandler(Lang defaultLang, CustomDomainLanguages customDomainLanguages) {
        this.defaultLang = defaultLang;
        this.customDomainLanguages = customDomainLanguages;
    }

    Lang getLang(String urlString) {
        URL url = getUrlObject(urlString);
        if (url == null) return null;

        CustomDomainLanguage customDomainLanguage = this.customDomainLanguages.getCustomDomainLanguageByUrl(url);
        if (customDomainLanguage == null) return null;

        return customDomainLanguage.lang;
    }

    String convertToDefaultLanguage(String urlString) {
        return convertToTargetLanguage(urlString, this.defaultLang);
    }

    String convertToTargetLanguage(String urlString, Lang lang) {
        URL url = getUrlObject(urlString);
        if (url == null) return urlString;

        CustomDomainLanguage currentCDL = this.customDomainLanguages.getCustomDomainLanguageByUrl(url);
        if (currentCDL == null) return urlString;

        CustomDomainLanguage targetCDL = this.customDomainLanguages.getCustomDomainLanguageByLang(lang.code); //TODO: change to pass Lang object
        if (targetCDL == null || currentCDL == targetCDL) return urlString;

        Pattern p = Pattern.compile("^(" + currentCDL.path + ")(/|$)");
        String newFile = p.matcher(url.getFile()).replaceFirst(targetCDL.path + "$2");

        try {
            URL result = new URL(url.getProtocol(), targetCDL.host, url.getPort(), newFile);
            return result.toString();
        } catch (MalformedURLException e) {
            return urlString;
        }
    }

    private URL getUrlObject(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
