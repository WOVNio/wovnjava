package com.github.wovnio.wovnjava;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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
