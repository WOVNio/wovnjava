package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String requestLang;
    private UrlLanguagePatternHandler urlLanguagePatternHandler;

    Headers(HttpServletRequest r, Settings s, UrlLanguagePatternHandler urlLanguagePatternHandler) {
        this.settings = s;
        this.request = r;
        this.urlLanguagePatternHandler = urlLanguagePatternHandler;

        this.requestLang = this.computeRequestLang();

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
            if (r.getQueryString() != null && !this.request.getQueryString().isEmpty()) {
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
        if (this.settings.query.size() > 0) {
            ArrayList<String> queryVals = new ArrayList<String>();
            for (String q : this.settings.query) {
                Pattern p = Pattern.compile("(^|&)(" + q + "[^&]+)(&|$)");
                Matcher m = p.matcher(this.query);
                if (m.find() && m.group(2) != null && m.group(2).length() > 0) {
                    queryVals.add(m.group(2));
                }
            }
            if (queryVals.isEmpty()) {
                // ignore all query parameters.
                this.query = "";
            } else {
                this.query = "?";
                Collections.sort(queryVals);
                for (String q : queryVals) {
                    this.query += q + "&";
                }
                // remove last ampersand.
                this.query = Pattern.compile("&$").matcher(this.query).replaceFirst("");
            }
        } else {
            this.query = "";
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

    String getRequestLang() {
        return this.requestLang;
    }

    /**
     * @return String Returns request URL with new language code added
     */
    String redirectLocation(String lang) {
        if (lang.equals(this.settings.defaultLang)) {
            return this.protocol + "://" + this.url;
        } else {
            String location = this.url;
            if (this.settings.urlPattern.equals("query")) {
                if (!Pattern.compile("\\?").matcher(location).find()) {
                    location = location + "?wovn=" + lang;
                } else if (!Pattern.compile("(\\?|&)wovn=").matcher(this.request.getRequestURI()).find()) {
                    location = location + "&wovn=" + lang;
                }
            } else if (this.settings.urlPattern.equals("subdomain")) {
                location = lang.toLowerCase() + "." + location;
            } else {
                // path
                if (settings.hasSitePrefixPath) {
                    String sitePrefixPath = this.settings.sitePrefixPath;
                    if (this.pathName.startsWith(sitePrefixPath)) {
                        location = location.replaceFirst(sitePrefixPath, sitePrefixPath + "/" + lang);
                        if (!location.endsWith("/")) {
                          location += "/";
                        }
                    }
                } else {
                    if (location.contains("/")) {
                        location = location.replaceFirst("/", "/" + lang + "/");
                    } else {
                        location += "/" + lang + "/";
                    }
                }
            }
            return protocol + "://" + location;
        }
    }

    public String locationWithLangCode(String location) {
        // check if needed
        if (location == null) {
            return null;
        }
        if (langCode().equals(settings.defaultLang)) {
            return location;
        }

        // catprue protocl and path
        String locationProtocol = protocol;
        String path;
        if (location.contains("://")) {
            if (!location.contains("://" + host)) {
                return location;
            }
            String[] protocolAndRemaining = location.split("://", 2);
            locationProtocol = protocolAndRemaining[0];
            path = "/" + protocolAndRemaining[1].split("/", 2)[1];
        } else {
            if (location.startsWith("/")) {
                path = location;
            } else {
                path = UrlPath.join("/", UrlPath.join(UrlPath.removeFile(pathNameKeepTrailingSlash), location));
            }
        }
        path = UrlPath.normalize(path);

        if (!path.startsWith(this.settings.sitePrefixPath)) {
            return location;
        }

        // check location already have language code
        if (settings.urlPattern.equals("query") && location.contains("wovn=")) {
            return location;
        } else if (settings.urlPattern.equals("path")) {
            String pathLang = this.urlLanguagePatternHandler.getLang(path);
            if (pathLang != null && pathLang.length() > 0) {
                return location;
            }
        }

        // build new location
        String lang = langCode();
        String queryLangCode = "";
        String subdomainLangCode = "";
        String pathLangCode = "";
        String sitePrefixPath = "";
        if (settings.urlPattern.equals("query")) {
            if (location.contains("?")) {
                queryLangCode = "&wovn=" + lang;
            } else {
                queryLangCode = "?wovn=" + lang;
            }
        } else if (settings.urlPattern.equals("subdomain")) {
            subdomainLangCode = lang + ".";
        } else {
            pathLangCode = "/" + lang;
            sitePrefixPath = this.settings.sitePrefixPath;
            path = path.replaceFirst(sitePrefixPath, "");
        }
        return locationProtocol + "://" + subdomainLangCode + host + sitePrefixPath + pathLangCode + path + queryLangCode;
    }
    /**
     * @return String Returns request URL without any language code
     */
    String getUrlWithoutLanguageCode() {
        return this.protocol + "://" + this.url;
    }

    String removeLang(String uri, String lang) {
        if (lang == null || lang.length() == 0) {
            lang = this.requestLang;
        }
        return this.urlLanguagePatternHandler.removeLang(uri, lang);
    }

    boolean isValidPath() {
        return this.pathName.startsWith(this.settings.sitePrefixPath);
    }

    private String computeRequestLang() {
        String path;
        if (this.settings.useProxy && this.request.getHeader("X-Forwarded-Host") != null) {
            path = this.request.getHeader("X-Forwarded-Host") + this.request.getRequestURI();
        } else {
            path = this.request.getServerName() + this.request.getRequestURI();
        }
        if (this.request.getQueryString() != null && this.request.getQueryString().length() > 0) {
            path += "?" + this.request.getQueryString();
        }
        return this.urlLanguagePatternHandler.getLang(path);
    }
}
