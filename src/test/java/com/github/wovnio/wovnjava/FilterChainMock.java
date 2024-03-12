package com.github.wovnio.wovnjava;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class FilterChainMock implements FilterChain {
    public HttpServletRequest req;
    public ServletResponse res;
    public String originalResponseBody;

    public FilterChainMock(String originalResponseBody) {
        this.originalResponseBody = originalResponseBody;
    }

    public void doFilter(ServletRequest req, ServletResponse res) throws IOException {
        this.req = (HttpServletRequest)req;
        this.res = res;

        // This is simulating the customer's application returning the HTML
        this.res.getWriter().write(this.originalResponseBody);
    }
}
