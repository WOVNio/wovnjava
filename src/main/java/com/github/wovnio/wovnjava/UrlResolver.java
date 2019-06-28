package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletRequest;

final class UrlResolver {
    private UrlResolver() {}

    static String calculateCurrentRequestPath(HttpServletRequest request) {
        if (this.settings.useProxy && this.request.getHeader("X-Forwarded-Host") != null) {
            path = this.request.getHeader("X-Forwarded-Host") + this.request.getRequestURI();
        } else {
            path = this.request.getServerName() + this.request.getRequestURI();
        }
        if (this.request.getQueryString() != null && this.request.getQueryString().length() > 0) {
            path += "?" + this.request.getQueryString();
        }
    }

    static String calculateClientRequestUrl(
            HttpServletRequest request,
            boolean useProxy,
            String originalUrlHeaderSetting,
            String originalQueryStringHeaderSetting)
    {
        String scheme = request.getScheme();
        String host = clientRequestHostAndPort(request, useProxy);
        String path = clientRequestPath(request, originalUrlHeaderSetting);
        String query = clientRequestQuery(request, originalQueryStringHeaderSetting);
        return scheme + "://" + host + path + query;
    }

    private static String clientRequestedHostAndPort(HttpServletRequest request, boolean useProxy) {
        String host;
        if (useProxy && request.getHeader("X-Forwarded-Host") != null) {
            host = request.getHeader("X-Forwarded-Host");
        } else {
            host = request.getServerName();
        }
        int port;
        if (useProxy) {
            if (request.getHeader("X-Forwarded-Port") == null || request.getHeader("X-Forwarded-Port").isEmpty()) {
                port = 80;
            } else {
                port = Integer.parseInt(request.getHeader("X-Forwarded-Port"));
            }
        } else {
            port = request.getServerPort();
        }
        if (port != 80 && port != 443) {
            return host + ":" + port;
        } else {
            return host;
        }
    }

    private static String clientRequestedPath(HttpServletRequest request, String originalUrlHeaderSetting) {
        String path = null;
        if (!originalUrlHeaderSetting.isEmpty()) {
            path = request.getHeader(originalUrlHeaderSetting);
        }
        if (path == null || path.isEmpty()) {
            path = (String) request.getAttribute("javax.servlet.forward.request_uri");
        }
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
        }
        return path;
    }

    private static String clientRequestedQuery(HttpServletRequest request, String originalQueryStringHeaderSetting) {
        String query;
        if (originalQueryStringHeaderSetting.isEmpty()) {
            query = request.getAttribute("javax.servlet.forward.query_string");
            if (query == null || query.isEmpty()) {
                query = request.getQueryString();
            }
        } else {
            query = request.getHeader(originalQueryStringHeaderSetting);
        }

        if (query == null || query.isEmpty()) {
            return "";
        } else {
            return "?" + query;
        }
    }
}
