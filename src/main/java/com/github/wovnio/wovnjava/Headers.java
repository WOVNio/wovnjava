package com.github.wovnio.wovnjava;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class Headers {
    Settings settings;
    String host;
    String pathName;
    String pathNameKeepTrailingSlash;
    String protocol;
    String pageUrl;
    String query;
    String url;

    private HttpServletRequest request;
    private UrlLanguagePatternHandler urlLanguagePatternHandler;
    private UrlContext urlContext;

    private final String requestLang;
    private final String clientRequestUrlWithoutLangCode;
    private final boolean shouldRedirectToDefaultLang;
    private final boolean isValidPath;

    Headers(HttpServletRequest request, Settings settings, UrlLanguagePatternHandler urlLanguagePatternHandler) {
        this.settings = settings;
        this.request = request;
        this.urlLanguagePatternHandler = urlLanguagePatternHandler;

        String clientRequestUrl = UrlResolver.computeClientRequestUrl(request, settings);

        this.requestLang = this.urlLanguagePatternHandler.getLang(clientRequestUrl);
        this.clientRequestUrlWithoutLangCode = this.urlLanguagePatternHandler.removeLang(clientRequestUrl, this.requestLang);

        try {
            this.urlContext = new UrlContext(new URL(this.clientRequestUrlWithoutLangCode));
        } catch (MalformedURLException e) {
            this.urlContext = null;
        }

        this.shouldRedirectToDefaultLang = settings.urlPattern.equals("path") && this.requestLang.equals(settings.defaultLang);
        this.isValidPath = this.urlLanguagePatternHandler.isMatchingSitePrefixPath(clientRequestUrl);

        this.protocol = this.request.getScheme();

        String requestUri = null;
        if (!this.settings.originalUrlHeader.isEmpty()) {
            requestUri = this.request.getHeader(this.settings.originalUrlHeader);
        }
        if (requestUri == null || requestUri.isEmpty()) {
            requestUri = this.request.getRequestURI();
            if (requestUri == null || requestUri.length() == 0) {
                if (Pattern.compile("^[^/]").matcher(this.request.getPathInfo()).find()) {
                    requestUri = "/";
                } else {
                    requestUri = "";
                }
                requestUri += this.request.getPathInfo();
            }
        }
        // Both getRequestURI() and getPathInfo() do not have query parameters.
        if (this.settings.originalQueryStringHeader.isEmpty()) {
            if (request.getQueryString() != null && !this.request.getQueryString().isEmpty()) {
                requestUri += "?" + this.request.getQueryString();
            }
        } else {
            String query = this.request.getHeader(this.settings.originalQueryStringHeader);
            if (query != null && !query.isEmpty()) {
                requestUri += "?" + query;
            }
        }
        if (Pattern.compile("://").matcher(requestUri).find()) {
            requestUri = Pattern.compile("^.*://[^/]+").matcher(requestUri).replaceFirst("");
        }

        if (this.settings.useProxy && this.request.getHeader("X-Forwarded-Host") != null) {
            this.host = this.request.getHeader("X-Forwarded-Host");
        } else {
            this.host = this.request.getServerName();
        }
        if (this.settings.urlPattern.equals("subdomain")) {
            this.host = this.removeLang(this.host, this.langCode());
        }
        String[] split = requestUri.split("\\?");
        this.pathName = split[0];
        if (split.length == 2) {
            this.query = split[1];
        }
        if (this.settings.urlPattern.equals("path")) {
            this.pathName = this.removeLang(this.pathName, this.langCode());
        }
        if (this.query == null) {
            this.query = "";
        }

        int port;
        if (this.settings.useProxy) {
            if (this.request.getHeader("X-Forwarded-Port") == null || this.request.getHeader("X-Forwarded-Port").isEmpty()) {
                port = 80;
            } else {
                port = Integer.parseInt(request.getHeader("X-Forwarded-Port"));
            }
        } else {
            port = this.request.getServerPort();
        }
        if (port != 80 && port != 443) {
            this.host += ":" + port;
        }

        this.url = this.host + this.pathName;
        if (this.query != null && this.query.length() > 0) {
            this.url += "?";
        }
        this.url += this.removeLang(this.query, this.langCode());
        this.url = this.url.length() == 0 ? "/" : this.url;
        if (!this.query.isEmpty() && !this.query.startsWith("?")) {
            this.query = "?" + this.query;
        }
        this.query = this.removeLang(this.query, this.langCode());
        this.pathNameKeepTrailingSlash = this.pathName;
        this.pathName = Pattern.compile("/$").matcher(this.pathName).replaceAll("");
        this.pageUrl = this.host + this.pathName + this.query;
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
     *
     * Do not modify `location` if
     *  - current request language is default language
     *  - `location` is malformed and cannot be parsed by java.net.URL
     *  - `location` is absolute URL with an external host
     *  - `location` already includes a Wovn language code
     *  - `location` does not match sitePrefixPath
     */
    public String locationWithLangCode(String location) {
        if (location == null || this.urlContext == null) return location;

        boolean isRequestDefaultLang = this.requestLang.isEmpty() || this.requestLang == settings.defaultLang;
        if (isRequestDefaultLang) return location;

        URL url = this.urlContext.resolve(location);

        boolean shouldAddLanguageCode = url != null
                                        && this.urlContext.isSameHost(url)
                                        && this.urlLanguagePatternHandler.getLang(url.toString()).isEmpty()
                                        && this.urlLanguagePatternHandler.isMatchingSitePrefixPath(url.toString());

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

    public String getClientRequestUrlWithoutLangCode() {
        return this.clientRequestUrlWithoutLangCode;
    }

    public String getClientRequestPathWithoutLangCode() {
        return UrlPath.getPath(this.clientRequestUrlWithoutLangCode);
    }

    public boolean getShouldRedirectToDefaultLang() {
        return this.shouldRedirectToDefaultLang;
    }

    public boolean getIsValidPath() {
        return this.isValidPath;
    }

    public HashMap<String, String> getHreflangUrlMap() {
        HashMap<String, String> hreflangs = new HashMap<String, String>();
        for (String supportedLang : this.settings.supportedLangs) {
            String hreflangCode = Lang.get(supportedLang).codeISO639_1;
            String url = this.urlLanguagePatternHandler.insertLang(this.clientRequestUrlWithoutLangCode, supportedLang);
            hreflangs.put(hreflangCode, url);
        }
        String hreflangCodeDefaultLang = Lang.get(this.settings.defaultLang).codeISO639_1;
        String urlDefaultLang = this.clientRequestUrlWithoutLangCode;
        hreflangs.put(hreflangCodeDefaultLang, urlDefaultLang);
        return hreflangs;
    }
}
