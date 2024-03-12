package com.github.wovnio.wovnjava;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;

public class WovnHttpServletRequest extends HttpServletRequestWrapper {
    private Headers headers;
    private final Map<String, String> customHeaders;

    public WovnHttpServletRequest(HttpServletRequest r, Headers h) {
        super(r);
        headers = h;

        this.customHeaders = new HashMap<String, String>() {{
            put("X-Wovn-Lang", headers.getRequestLang().code);
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
        URL url;
        try {
            url = headers.convertToDefaultLanguage(new URL("http://" + host));
            return url.getHost();
        } catch (MalformedURLException e) {
            return host;
        }
    }

    @Override
    public String getServerName() {
        // `currentContextUrlInDefaultLanguage` is computed directly from `request.getRequestURL()`
        // This implementation assumes that `getServerName()` will always give the hostname of `request.getRequestURL()`
        return headers.getCurrentContextUrlInDefaultLanguage().getHost();
    }

    @Override
    public StringBuffer getRequestURL() {
        // `currentContextUrlInDefaultLanguage` is computed directly from `request.getRequestURL()`
        return new StringBuffer(headers.getCurrentContextUrlInDefaultLanguage().toString());
    }
}
