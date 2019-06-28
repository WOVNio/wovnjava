package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletRequest;

final class UrlResolver {
    private UrlResolver() {}

    static String computeClientRequestUrl(HttpServletRequest request, Settings settings) {
        String scheme = request.getScheme();
        String host = clientRequestHostAndPort(request, settings.useProxy);
        String path = clientRequestPath(request, settings.originalUrlHeader);
        String query = clientRequestQuery(request, settings.originalQueryStringHeader);
        return scheme + "://" + host + path + query;
    }

    private static String clientRequestHostAndPort(HttpServletRequest request, boolean useProxy) {
        String host;
        int port;
        if (useProxy) {
            /* request.getHeader returns String or null */
            String forwardedHost = request.getHeader("X-Forwarded-Host");
            if (forwardedHost != null) {
                host = forwardedHost;
            } else {
                /* X-Fowarded-Host is required for `useProxy`. Set empty string to avoid crashing. */
                host = "";
            }
            /* request.getHeader returns String or null */
            String forwardedPort = request.getHeader("X-Forwarded-Port");
            if (forwardedPort == null || forwardedPort.isEmpty()) {
                port = 80;
            } else {
                port = Integer.parseInt(forwardedPort);
            }
        } else {
            /* request.getServerName returns String */
            host = request.getServerName();
            /* request.getServerPort returns int */
            port = request.getServerPort();
        }

        if (port != 80 && port != 443) {
            return host + ":" + port;
        } else {
            return host;
        }
    }

    private static String clientRequestPath(HttpServletRequest request, String originalUrlHeaderSetting) {
        String path;
        if (!originalUrlHeaderSetting.isEmpty()) {
            /* request.getHeader returns String or null */
            path = request.getHeader(originalUrlHeaderSetting);
        } else {
            /* request.getAttribute returns Object */
            path = (String) request.getAttribute("javax.servlet.forward.request_uri").toString();
            if (path == null || path.isEmpty()) {
                /* request.getRequestURI returns String */
                path = request.getRequestURI();
            }
        }

        if (path == null) {
            return "";
        } else {
            return path;
        }
    }

    private static String clientRequestQuery(HttpServletRequest request, String originalQueryStringHeaderSetting) {
        String query;
        if (!originalQueryStringHeaderSetting.isEmpty()) {
            query = request.getHeader(originalQueryStringHeaderSetting);
        } else {
            /* request.getAttribute returns Object */
            query = request.getAttribute("javax.servlet.forward.query_string").toString();
            if (query == null || query.isEmpty()) {
                /* request.getQueryString returns String or null */
                query = request.getQueryString();
            }
        }

        if (query == null || query.isEmpty()) {
            return "";
        } else {
            return "?" + query;
        }
    }
}
