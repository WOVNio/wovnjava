package com.github.wovnio.wovnjava;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import jakarta.servlet.http.HttpServletResponse;


class ResponseHeaders {
    private static final String apiStatusHeaderName = "X-Wovn-Api-Status";
    private static final String apiStatusCodeHeaderName = "X-Wovn-Api-StatusCode";
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

    public void setApiStatus(String value) {
        this.response.setHeader(ResponseHeaders.apiStatusHeaderName, value);
    }

    public void setApiStatusCode(String value) {
        this.response.setHeader(ResponseHeaders.apiStatusCodeHeaderName, value);
    }

    public void forwardFastlyHeaders(HttpURLConnection con) {
        String fastlyHeaderName, responseHeaderName, value;
        for (Map.Entry<String, String> entry : ResponseHeaders.fastlyHeaders.entrySet()) {
            fastlyHeaderName = entry.getKey();
            responseHeaderName = entry.getValue();
            value = con.getHeaderField(fastlyHeaderName);
            if (value != null) {
                this.response.setHeader(responseHeaderName, value);
            }
        }
    }
}
