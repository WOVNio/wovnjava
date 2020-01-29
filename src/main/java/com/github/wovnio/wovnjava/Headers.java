package com.github.wovnio.wovnjava;

import java.util.LinkedHashMap;
import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

class Headers {
    Settings settings;

    private HttpServletRequest request;
    private UrlLanguagePatternHandler urlLanguagePatternHandler;
    private UrlContext urlContext;

    /* The language of the client request URL */
    private final Lang requestLang;
    /* The URL that the client originally requested */
    private final String clientRequestUrlInDefaultLanguage;

    /* Should send HTTP 302 redirect to page in default language */
    private final boolean shouldRedirectExplicitDefaultLangUrl;
    /* Is current context URL path the same as the equivalent path in default language */
    private boolean isPathInDefaultLanguage;

    private final boolean isValidRequest;

    Headers(HttpServletRequest request, Settings settings, UrlLanguagePatternHandler urlLanguagePatternHandler) {
        this.settings = settings;
        this.request = request;
        this.urlLanguagePatternHandler = urlLanguagePatternHandler;

        String clientRequestUrl = UrlResolver.computeClientRequestUrl(request, settings);
        this.requestLang = this.urlLanguagePatternHandler.getLang(clientRequestUrl);
        this.clientRequestUrlInDefaultLanguage = this.urlLanguagePatternHandler.convertToDefaultLanguage(clientRequestUrl);

        String currentContextUrl = request.getRequestURL().toString();
        String currentContextUrlInDefaultLanguage = this.urlLanguagePatternHandler.convertToDefaultLanguage(currentContextUrl);

        try {
            this.urlContext = new UrlContext(new URL(currentContextUrlInDefaultLanguage));
        } catch (MalformedURLException e) {
            this.urlContext = null;
        }

        try {
            String currentContextPath = new URL(currentContextUrl).getPath();
            this.isPathInDefaultLanguage = currentContextPath.equalsIgnoreCase(this.getCurrentContextUrlInDefaultLanguage().getPath());
        } catch (MalformedURLException e) {
            this.isPathInDefaultLanguage = false;
        }

        this.shouldRedirectExplicitDefaultLangUrl = this.urlLanguagePatternHandler.shouldRedirectExplicitDefaultLangUrl(clientRequestUrl);

        this.isValidRequest = this.requestLang != null && this.urlContext != null;
    }

    /*
     * Convert a redirect URL into the language of the current request
     *
     * Take as input a location string of any form (relative path, absolute path, absolute URL).
     * If the location needs a Wovn language code, return an absolute URL string of that location
     * with language code of the current request language. Else return the location as-is.
     */
    public String locationWithLangCode(String location) {
        if (location == null || !this.isValidRequest) return location;

        if (this.requestLang == this.settings.defaultLang) return location;

        URL url = this.urlContext.resolve(location);

        boolean shouldAddLanguageCode = url != null
                                        && this.urlContext.isSameHost(url)
                                        && this.urlLanguagePatternHandler.getLang(url.toString()) != null;

        if (!shouldAddLanguageCode) return location;

        return this.urlLanguagePatternHandler.convertToTargetLanguage(url.toString(), this.requestLang);
    }

    URL convertToDefaultLanguage(URL url) {
        String urlInDefaultLang = this.urlLanguagePatternHandler.convertToDefaultLanguage(url.toString());
        try {
            return new URL(urlInDefaultLang);
        } catch (MalformedURLException e) {
            return url;
        }
    }

    public Lang getRequestLang() {
        return this.requestLang;
    }

    public String getClientRequestUrlInDefaultLanguage() {
        return this.clientRequestUrlInDefaultLanguage;
    }

    public String getClientRequestPathInDefaultLanguage() {
        return UrlPath.getPath(this.clientRequestUrlInDefaultLanguage);
    }

    public URL getCurrentContextUrlInDefaultLanguage() {
        return this.urlContext.getURL();
    }

    public boolean getShouldRedirectExplicitDefaultLangUrl() {
        return this.shouldRedirectExplicitDefaultLangUrl;
    }

    public boolean getIsPathInDefaultLanguage() {
        return this.isPathInDefaultLanguage;
    }

    public boolean getIsValidRequest() {
        return this.isValidRequest;
    }

    public LinkedHashMap<String, String> getHreflangUrlMap() {
        LinkedHashMap<String, String> hreflangs = new LinkedHashMap<String, String>();
        for (Lang supportedLang : this.settings.supportedLangs) {
            String hreflangCode = supportedLang.codeISO639_1;
            String url = this.urlLanguagePatternHandler.convertToTargetLanguage(this.clientRequestUrlInDefaultLanguage, supportedLang);
            hreflangs.put(hreflangCode, url);
        }
        String hreflangCodeDefaultLang = this.settings.defaultLang.codeISO639_1;
        String urlDefaultLang = this.clientRequestUrlInDefaultLanguage;
        hreflangs.put(hreflangCodeDefaultLang, urlDefaultLang);
        return hreflangs;
    }
}
