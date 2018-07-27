package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class WovnHttpServletRequest extends HttpServletRequestWrapper {
    private Headers headers;
    private final Map<String, String> customHeaders;

    public WovnHttpServletRequest(HttpServletRequest r, Headers h) {
        super(r);
        headers = h;

        this.customHeaders = new HashMap<String, String>();
        this.addHeader("X-Wovn-Lang", headers.langCode());
    }

    public void addHeader(String name, String value){
        this.customHeaders.put(name, value);
    }

    public String getHeader(String name) {
        // return custom header if exists
        String headerValue = customHeaders.get(name);

        if (headerValue != null) {
            return headerValue;
        }

        // otherwise, return original headers
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    public Enumeration<String> getHeaderNames() {
        Set<String> set = new HashSet<String>(customHeaders.keySet());

        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            String n = e.nextElement();
            set.add(n);
        }

        return Collections.enumeration(set);
    }

    public String getRemoteHost() {
        String host = super.getRemoteHost();
        if (headers.settings.urlPattern.equals("subdomain")) {
            host = headers.removeLang(host, null);
        }
        return host;
    }

    public String getServerName() {
        String serverName = super.getServerName();
        if (headers.settings.urlPattern.equals("subdomain")) {
            serverName = headers.removeLang(serverName, null);
        }
        return serverName;
    }

    public String getRequestURI() {
        String uri = super.getRequestURI();
        if (!headers.settings.urlPattern.equals("subdomain")) {
            if (uri != null && uri.length() > 0) {
                uri = headers.removeLang(uri, null);
            }
        }
        return uri;
    }

    public StringBuffer getRequestURL() {
        String url = super.getRequestURL().toString();
        url = this.headers.removeLang(url, null);
        return new StringBuffer(url);
    }

    public String getServletPath() {
        String path = super.getServletPath();
        if (this.headers.settings.urlPattern.equals("path")) {
            path = this.headers.removeLang(path, null);
        }
        return path;
    }
}
