package com.github.wovnio.wovnjava;

import java.util.Vector;
import java.net.URL;
import java.net.MalformedURLException;
import java.lang.IllegalArgumentException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.RequestDispatcher;

import org.easymock.EasyMock;

public class MockHttpServletRequest {
    private static int DefaultTestPort = 443;

    public static HttpServletRequest create(String urlString) {
        return create(urlString, null);
    }

    public static HttpServletRequest create(String urlString, String userAgent) {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        stubLocation(mock, parseURL(urlString));
        stubHeaders(mock, userAgent);
        EasyMock.replay(mock);
        return mock;
    }

    public static HttpServletRequest createWithForwardingDispatcher(String urlString, String forwardingPath, RequestDispatcher dispatcher) {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        stubLocation(mock, parseURL(urlString));
        stubHeaders(mock);
        EasyMock.expect(mock.getRequestDispatcher(forwardingPath)).andReturn(dispatcher);
        EasyMock.replay(mock);
        return mock;
    }

    public static HttpServletRequest createWithRemoteHost(String urlString, String remoteHost) {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        stubLocation(mock, parseURL(urlString));
        stubHeaders(mock);
        EasyMock.expect(mock.getRemoteHost()).andReturn(remoteHost).atLeastOnce();
        EasyMock.replay(mock);
        return mock;
    }

    private static URL parseURL(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed url: " + urlString);
        }
        return url;
    }

    private static void stubLocation(HttpServletRequest mock, URL url) {
        boolean isPortDeclared = url.getPort() != -1;
        int port = isPortDeclared ? url.getPort() : MockHttpServletRequest.DefaultTestPort;

        EasyMock.expect(mock.getScheme()).andReturn(url.getProtocol()).anyTimes();
        EasyMock.expect(mock.getRequestURI()).andReturn(url.getPath()).anyTimes();
        EasyMock.expect(mock.getServletPath()).andReturn(url.getPath()).anyTimes();
        EasyMock.expect(mock.getRequestURL()).andReturn(new StringBuffer(url.toString())).anyTimes();
        EasyMock.expect(mock.getServerName()).andReturn(url.getHost()).anyTimes();
        EasyMock.expect(mock.getQueryString()).andReturn(url.getQuery()).anyTimes();
        EasyMock.expect(mock.getServerPort()).andReturn(port).anyTimes();
        EasyMock.expect(mock.getAttribute("jakarta.servlet.forward.request_uri")).andReturn(null).anyTimes();
        EasyMock.expect(mock.getAttribute("jakarta.servlet.forward.query_string")).andReturn(null).anyTimes();
    }

    private static void stubHeaders(HttpServletRequest mock) {
        stubHeaders(mock, null);
    }

    private static void stubHeaders(HttpServletRequest mock, String userAgent) {
        /* `X-Wovn-Lang` is a Header value that we mock with our HttpServletRequestWrapper (unless the Header value already exists) */
        EasyMock.expect(mock.getHeader("X-Wovn-Lang")).andReturn(null).anyTimes();

        EasyMock.expect(mock.getHeader("User-Agent")).andReturn(userAgent).anyTimes();

        /* Stub `X-Header-Test` for testing behavior of an existing Header value */
        EasyMock.expect(mock.getHeader("X-Header-Test")).andReturn("x-header-test-value").anyTimes();
        Vector<String> headersVector = new Vector<String>();
        headersVector.add("X-Header-Test");
        EasyMock.expect(mock.getHeaderNames()).andReturn(headersVector.elements()).anyTimes();
    }
}
