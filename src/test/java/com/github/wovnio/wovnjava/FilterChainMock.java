package com.github.wovnio.wovnjava;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class FilterChainMock implements FilterChain {
    public HttpServletRequest req;
    public ServletResponse res;

    public void doFilter(ServletRequest req, ServletResponse res) {
        this.req = (HttpServletRequest)req;
        this.res = res;
    }
}
