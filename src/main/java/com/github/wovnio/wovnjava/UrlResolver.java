package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletRequest;

final class UrlResolver {
    private UrlResolver() {}

    static String computeClientRequestUrl(
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

    private static String clientRequestHostAndPort(HttpServletRequest request, boolean useProxy) {
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

    private static String clientRequestPath(HttpServletRequest request, String originalUrlHeaderSetting) {
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

    private static String clientRequestQuery(HttpServletRequest request, String originalQueryStringHeaderSetting) {
        String query;
        if (!originalQueryStringHeaderSetting.isEmpty()) {
            query = request.getHeader(originalQueryStringHeaderSetting);
        } else {
            query = request.getAttribute("javax.servlet.forward.query_string");
            if (query == null || query.isEmpty()) {
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
