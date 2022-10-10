package com.github.wovnio.wovnjava;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestDispatcherMock implements RequestDispatcher {
    public HttpServletRequest req;
    public HttpServletResponse res;

    public void forward(ServletRequest req, ServletResponse res) {
        this.req = (HttpServletRequest)req;
        this.res = (HttpServletResponse)res;
    }

    public void include(ServletRequest req, ServletResponse res) {
        this.req = (HttpServletRequest)req;
        this.res = (HttpServletResponse)res;
    }
}
