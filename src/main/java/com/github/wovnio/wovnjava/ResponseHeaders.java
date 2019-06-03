package com.github.wovnio.wovnjava;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import javax.servlet.http.HttpServletResponse;

import javax.xml.bind.DatatypeConverter;

import net.arnx.jsonic.JSON;

class ResponseHeaders {
    private static final String apiHeaderName = "X-Wovn-Api-Status";
    private static final String apiStatusHeaderName = "X-Wovn-Api-StatusCode";
    private static final Map<String, String> fastlyHeaders;
    static {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Cache", "X-Wovn-Cache");
        headers.put("X-Cache-Hits", "X-Wovn-Cache-Hits");
        headers.put("X-Wovn-Surrogate-Key", "X-Wovn-Surrogate-Key");
        fastlyHeaders = Collections.unmodifiableMap(headers);
    }

    private final HttpServletResponse response;

    ResponseHeaders(HttpServletResponse response) {
        this.response = response;
    }

    public void setApi(String value) {
        this.response.setHeader(ResponseHeaders.apiHeaderName, value);
    }

    public void setApiStatus(String value) {
        this.response.setHeader(ResponseHeaders.apiStatusHeaderName, value);
    }

    public void forwardFastlyHeaders(HttpURLConnection con) {
        String apiHeaderName, responseHeaderName, value;
        for (Map.Entry<String, String> entry : ResponseHeaders.fastlyHeaders.entrySet()) {
            apiHeaderName = entry.getKey();
            responseHeaderName = entry.getValue();
            value = con.getHeaderField(apiHeaderName);
            if (value != null) {
                this.response.setHeader(responseHeaderName, value);
            }
        }
    }
}
