package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;

public class WovnHttpServletRequest extends HttpServletRequestWrapper {
    private Headers headers;
    private final Map<String, String> customHeaders;

    public WovnHttpServletRequest(HttpServletRequest r, Headers h) {
        super(r);
        headers = h;

        this.customHeaders = new HashMap<String, String>() {{
            put("X-Wovn-Lang", headers.langCode());
        }};
    }

    public void addHeader(String name, String value){
        this.customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        // return custom header if exists
        String headerValue = customHeaders.get(name);

        if (headerValue != null) {
            return headerValue;
        }

        // otherwise, return original headers
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        Set<String> set = new HashSet<String>(customHeaders.keySet());
        names.addAll(set);

        return Collections.enumeration(names);
    }

    @Override
    public String getRemoteHost() {
        String host = super.getRemoteHost();
        if (headers.settings.urlPattern.equals("subdomain")) {
            host = headers.removeLang(host, null);
        }
        return host;
    }

    @Override
    public String getServerName() {
        String serverName = super.getServerName();
        if (headers.settings.urlPattern.equals("subdomain")) {
            serverName = headers.removeLang(serverName, null);
        }
        return serverName;
    }

    @Override
    public String getRequestURI() {
        String uri = super.getRequestURI();
        if (!headers.settings.urlPattern.equals("subdomain")) {
            if (uri != null && uri.length() > 0) {
                uri = headers.removeLang(uri, null);
            }
        }
        return uri;
    }

    @Override
    public StringBuffer getRequestURL() {
        String url = super.getRequestURL().toString();
        url = this.headers.removeLang(url, null);
        return new StringBuffer(url);
    }

    @Override
    public String getServletPath() {
        String path = super.getServletPath();
        if (this.headers.settings.urlPattern.equals("path")) {
            path = this.headers.removeLang(path, null);
        }
        return path;
    }
}
