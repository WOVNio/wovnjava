package com.github.wovnio.wovnjava;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class RequestDispatcherMock implements RequestDispatcher {
    public HttpServletRequest req;
    public ServletResponse res;
    private String originalResponseBody;

    public RequestDispatcherMock(String originalResponseBody) {
        this.originalResponseBody = originalResponseBody;
    }

    public void forward(ServletRequest req, ServletResponse res) throws IOException {
        this.req = (HttpServletRequest)req;
        this.res = (ServletResponse)res;

        // This is simulating the customer's application returning the HTML
        this.res.getWriter().write(this.originalResponseBody);
    }

    public void include(ServletRequest req, ServletResponse res) {
        this.req = (HttpServletRequest)req;
        this.res = (ServletResponse)res;
    }
}
