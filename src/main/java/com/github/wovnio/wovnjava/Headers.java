package com.github.wovnio.wovnjava;

import java.util.HashMap;
import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

class Headers {
    Settings settings;

    private HttpServletRequest request;
    private UrlLanguagePatternHandler urlLanguagePatternHandler;
    private UrlContext urlContext;

    /* The language code found in the client request URL */
    private final String requestLang;
    /* The URL that the client originally requested */
    private final String clientRequestUrlInDefaultLanguage;

    /* Should send HTTP 302 redirect to page in default language */
    private final boolean shouldRedirectToDefaultLang;
    /* Should forward the request internally to equivalent path in default language */
    private boolean shouldForwardToPathInDefaultLanguage;

    private final boolean isValidRequest;

    Headers(HttpServletRequest request, Settings settings, UrlLanguagePatternHandler urlLanguagePatternHandler) {
        this.settings = settings;
        this.request = request;
        this.urlLanguagePatternHandler = urlLanguagePatternHandler;

        String clientRequestUrl = UrlResolver.computeClientRequestUrl(request, settings);
        this.requestLang = this.urlLanguagePatternHandler.getLang(clientRequestUrl);
        this.clientRequestUrlInDefaultLanguage = this.urlLanguagePatternHandler.removeLang(clientRequestUrl, this.requestLang);

        String currentContextUrl = request.getRequestURL().toString();
        String currentContextUrlInDefaultLanguage = this.urlLanguagePatternHandler.removeLang(currentContextUrl, this.requestLang);

        try {
            this.urlContext = new UrlContext(new URL(currentContextUrlInDefaultLanguage));
        } catch (MalformedURLException e) {
            this.urlContext = null;
        }

        try {
            String currentContextPath = new URL(currentContextUrl).getPath();
            this.shouldForwardToPathInDefaultLanguage = !currentContextPath.equalsIgnoreCase(this.getCurrentContextPathInDefaultLanguage());
        } catch (MalformedURLException e) {
            this.shouldForwardToPathInDefaultLanguage = false;
        }

        this.shouldRedirectToDefaultLang = settings.urlPattern.equals("path") && this.requestLang.equals(settings.defaultLang);

        this.isValidRequest = this.urlContext != null && this.urlLanguagePatternHandler.canInterceptUrl(clientRequestUrl);
    }

    String langCode() {
        String pl = this.requestLang;
        if (pl != null && pl.length() > 0) {
            return pl;
        } else {
            return settings.defaultLang;
        }
    }

    /*
     * Take as input a location string of any form (relative path, absolute path, absolute URL).
     * If the location needs a Wovn language code, return an absolute URL string of that location
     * with language code of the current request language. Else return the location as-is.
     */
    public String locationWithLangCode(String location) {
        if (location == null || !this.isValidRequest) return location;

        boolean isRequestDefaultLang = this.requestLang.isEmpty() || this.requestLang == settings.defaultLang;
        if (isRequestDefaultLang) return location;

        URL url = this.urlContext.resolve(location);

        boolean shouldAddLanguageCode = url != null
                                        && this.urlContext.isSameHost(url)
                                        && this.urlLanguagePatternHandler.getLang(url.toString()).isEmpty()
                                        && this.urlLanguagePatternHandler.canInterceptUrl(url.toString());

        if (!shouldAddLanguageCode) return location;

        return this.urlLanguagePatternHandler.insertLang(url.toString(), this.requestLang);
    }

    String removeLang(String uri, String lang) {
        if (lang == null || lang.length() == 0) {
            lang = this.requestLang;
        }
        return this.urlLanguagePatternHandler.removeLang(uri, lang);
    }

    public String getRequestLang() {
        return this.requestLang;
    }

    public String getClientRequestUrlInDefaultLanguage() {
        return this.clientRequestUrlInDefaultLanguage;
    }

    public String getClientRequestPathInDefaultLanguage() {
        return UrlPath.getPath(this.clientRequestUrlInDefaultLanguage);
    }

    public String getCurrentContextPathInDefaultLanguage() {
        return this.urlContext.getURL().getPath();
    }

    public boolean getShouldRedirectToDefaultLang() {
        return this.shouldRedirectToDefaultLang;
    }

    public boolean getShouldForwardToPathInDefaultLanguage() {
        return this.shouldForwardToPathInDefaultLanguage;
    }

    public boolean getIsValidRequest() {
        return this.isValidRequest;
    }

    public HashMap<String, String> getHreflangUrlMap() {
        HashMap<String, String> hreflangs = new HashMap<String, String>();
        for (String supportedLang : this.settings.supportedLangs) {
            String hreflangCode = Lang.get(supportedLang).codeISO639_1;
            String url = this.urlLanguagePatternHandler.insertLang(this.clientRequestUrlInDefaultLanguage, supportedLang);
            hreflangs.put(hreflangCode, url);
        }
        String hreflangCodeDefaultLang = Lang.get(this.settings.defaultLang).codeISO639_1;
        String urlDefaultLang = this.clientRequestUrlInDefaultLanguage;
        hreflangs.put(hreflangCodeDefaultLang, urlDefaultLang);
        return hreflangs;
    }
}
