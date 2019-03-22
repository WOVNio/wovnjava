package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

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
    private String browserLang;
    private String pathLang;
    private String unmaskedHost;
    private String unmaskedPathName;
    private String unmaskedUrl;

    Headers(HttpServletRequest r, Settings s) {
        this.settings = s;
        this.request = r;

        this.protocol = this.request.getScheme();
        if (this.settings.useProxy && this.request.getHeader("X-Forwarded-Host") != null) {
            this.unmaskedHost = this.request.getHeader("X-Forwarded-Host");
        } else {
            this.unmaskedHost = this.request.getRemoteHost();
        }

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

        String[] split = requestUri.split("\\?");
        this.unmaskedPathName = split[0];
        if ( !Pattern.compile("/$").matcher(this.unmaskedPathName).find()
                && !Pattern.compile("/[^/.]+\\.[^/.]+$").matcher(this.unmaskedPathName).find()
                ) {
            this.unmaskedPathName += "/";
        }
        this.unmaskedUrl = this.protocol + "://" + this.unmaskedHost + this.unmaskedPathName;
        if (this.settings.useProxy && this.request.getHeader("X-Forwarded-Host") != null) {
            this.host = this.request.getHeader("X-Forwarded-Host");
        } else {
            this.host = this.request.getServerName();
        }
        if (this.settings.urlPattern.equals("subdomain")) {
            this.host = this.removeLang(this.host, this.langCode());
        }
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
        String pl = getPathLang();
        if (pl != null && pl.length() > 0) {
            return pl;
        } else {
            return settings.defaultLang;
        }
    }

    String getPathLang() {
        if (this.pathLang == null || this.pathLang.length() == 0) {
            Pattern p = Pattern.compile(settings.urlPatternReg);
            String path;
            if (this.settings.useProxy && this.request.getHeader("X-Forwarded-Host") != null) {
                path = this.request.getHeader("X-Forwarded-Host") + this.request.getRequestURI();
            } else {
                path = this.request.getServerName() + this.request.getRequestURI();
            }
            if (this.request.getQueryString() != null && this.request.getQueryString().length() > 0) {
                path += "?" + this.request.getQueryString();
            }
            Matcher m = p.matcher(path);
            if (m.find()) {
                String l = m.group(1);
                if (l != null && l.length() > 0 && Lang.getLang(l) != null) {
                    String lc = Lang.getCode(l);
                    if (lc != null && lc.length() > 0) {
                        this.pathLang = lc;
                        return this.pathLang;
                    }
                }
            }
            this.pathLang = "";
        }
        return this.pathLang;
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
                    String sitePrefixPath = this.settings.sitePrefixPathWithoutSlash;
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
                path = pathJoin("/", pathJoin(removeFilePart(pathNameKeepTrailingSlash), location));
            }
        }
        path = pathNormalize(path);

        if (!path.startsWith(this.settings.sitePrefixPathWithoutSlash)) {
            return location;
        }

        // check location already have language code
        if (settings.urlPattern.equals("query") && location.contains("wovn=")) {
            return location;
        } else if (settings.urlPattern.equals("path")) {
            Pattern p = Pattern.compile(settings.urlPatternReg);
            Matcher m = p.matcher(path);
            if (m.find()) {
                String l = m.group(1);
                if (l != null && l.length() > 0 && Lang.getLang(l) != null) {
                    return location;
                }
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
            sitePrefixPath = this.settings.sitePrefixPathWithoutSlash;
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
            lang = this.getPathLang();
        }
        if (this.settings.urlPattern.equals("query")) {
            return uri.replaceFirst("(^|\\?|&)wovn=" + lang + "(&|$)", "$1")
                    .replaceAll("(\\?|&)$", "");
        } else if (this.settings.urlPattern.equals("subdomain")) {
            return Pattern.compile("(^|(//))" + lang + "\\.", Pattern.CASE_INSENSITIVE)
                    .matcher(uri).replaceFirst("$1");
        } else {
            String prefix = this.settings.sitePrefixPathWithSlash;
            return uri.replaceFirst(prefix + lang + "(/|$)", prefix);
        }
    }

    boolean isValidPath() {
        return this.pathName.startsWith(this.settings.sitePrefixPathWithoutSlash);
    }

    private String removeFilePart(String path) {
        if (path.endsWith("/")) {
            return path;
        } else {
            int index = path.lastIndexOf("/");
            if (index > 0) {
                return path.substring(0, index + 1);
            } else {
                return "/";
            }
        }
    }

    private String pathJoin(String left, String right) {
        boolean l = left.endsWith("/");
        boolean r = right.startsWith("/");
        if (l && r) {
            return left + right.substring(1);
        } else if (l || r) {
            return left + right;
        } else {
            return left + "/" + right;
        }
    }

    private String pathNormalize(String path) {
        path = replaceRepeat(path, "/./", "/");            // remove redundant relative path
        path = replaceRepeat(path, "/[^/]+/\\.\\./", "/"); // combine relative path.         eg. '/dir/../file' to '/file'
        path = path.replace("/../", "/");                  // remove nonsense relative path. eg. '/../dir/file' to '/dir/file'
        path = path.replaceFirst("\\.\\./", "");           // remove nonsense relative path. eg.'../dir/file' to '/dir/file'
        return path.length() == 0 ? "/" : path;
    }

    private String replaceRepeat(String path, String pattern, String replacement) {
        while(true) {
            String newPath = path.replaceAll(pattern, replacement);
            if (path.length() == newPath.length()) {
                return path;
            } else {
                path = newPath;
            }
        }
    }
}
