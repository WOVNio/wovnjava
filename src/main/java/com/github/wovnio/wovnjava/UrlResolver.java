package com.github.wovnio.wovnjava;

import jakarta.servlet.http.HttpServletRequest;

final class UrlResolver {
    private UrlResolver() {}

    static String computeClientRequestUrl(HttpServletRequest request, Settings settings) {
        String path = clientRequestPath(request, settings.originalUrlHeader);
        String query = clientRequestQuery(request, settings.originalQueryStringHeader);
        
        if (settings.hasUrlOverride) {
            return getUrlOverride(settings, path, query);
        }

        String scheme = request.getScheme();
        String host = clientRequestHostAndPort(request, settings);
        return scheme + "://" + host + path + query;
    }

    private static String getUrlOverride(Settings settings, String path, String query) {
        String hostAndPort;
        if (settings.fixedPort == 80 || settings.fixedPort == 443) {
            hostAndPort = settings.fixedHost;
        } else {
            hostAndPort = settings.fixedHost + ":" + settings.fixedPort;
        }
        return settings.fixedScheme + "://" + hostAndPort + path + query;
    }

    private static String clientRequestHostAndPort(HttpServletRequest request, Settings settings) {
        String host = null;
        Integer port = null;
        if (settings.useProxy) {
            // request.getHeader returns String or null
            host = request.getHeader("X-Forwarded-Host");
            // request.getHeader returns String or null
            String forwardedPort = request.getHeader("X-Forwarded-Port");
            if (forwardedPort != null && !forwardedPort.isEmpty()) {
                port = Integer.parseInt(forwardedPort);
            }
        }

        if (host == null) {
            // request.getServerName returns String
            host = request.getServerName();
        }
        if (port == null) {
            // request.getServerPort returns int
            port = request.getServerPort();
        }

        if (port != 80 && port != 443) {
            return host + ":" + port;
        } else {
            return host;
        }
    }

    private static String clientRequestPath(HttpServletRequest request, String originalUrlHeaderSetting) {
        String path = null;
        if (!originalUrlHeaderSetting.isEmpty()) {
            // request.getHeader returns String or null
            path = request.getHeader(originalUrlHeaderSetting);
        }
        if (path == null) {
            // request.getAttribute returns Object or null
            Object forwardedPath = request.getAttribute("jakarta.servlet.forward.request_uri");
            if (forwardedPath != null) {
                path = forwardedPath.toString();
            }
        }
        if (path == null) {
            // request.getRequestURI returns String
            path = request.getRequestURI();
        }

        if (path == null) {
            return "";
        } else {
            return path;
        }
    }

    private static String clientRequestQuery(HttpServletRequest request, String originalQueryStringHeaderSetting) {
        String query = null;
        if (!originalQueryStringHeaderSetting.isEmpty()) {
            query = request.getHeader(originalQueryStringHeaderSetting);
        }
        if (query == null) {
            // request.getAttribute returns Object or null
            Object forwardedQuery = request.getAttribute("jakarta.servlet.forward.query_string");
            if (forwardedQuery != null) {
                query = forwardedQuery.toString();
            }
        }
        if (query == null) {
            // request.getQueryString returns String or null
            query = request.getQueryString();
        }

        if (query == null || query.isEmpty()) {
            return "";
        } else {
            return "?" + query;
        }
    }
}
