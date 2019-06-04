package com.github.wovnio.wovnjava;

import java.net.HttpURLConnection;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;

public class ResponseHeadersTest extends TestCase {
    public void testSetApi() {
        HttpServletResponse mockResponse = EasyMock.createMock(HttpServletResponse.class);
        mockResponse.setHeader("X-Wovn-Api-Status", "test value");
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.replay(mockResponse);

        ResponseHeaders responseHeaders = new ResponseHeaders(mockResponse);
        responseHeaders.setApiStatus("test value");
    }

    public void testSetApiStatus() {
        HttpServletResponse mockResponse = EasyMock.createMock(HttpServletResponse.class);
        mockResponse.setHeader("X-Wovn-Api-StatusCode", "500");
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.replay(mockResponse);

        ResponseHeaders responseHeaders = new ResponseHeaders(mockResponse);
        responseHeaders.setApiStatusCode("500");
    }

    public void testForwardFastlyHeaders() {
        HttpServletResponse mockResponse = EasyMock.createMock(HttpServletResponse.class);
        mockResponse.setHeader("X-Wovn-Cache", "mock fastly x-cache");
        EasyMock.expectLastCall().atLeastOnce();
        mockResponse.setHeader("X-Wovn-Cache-Hits", "mock fastly x-cache-hits");
        EasyMock.expectLastCall().atLeastOnce();
        mockResponse.setHeader("X-Wovn-Surrogate-Key", "mock fastly x-wovn-surrogate-key");
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.replay(mockResponse);

        ResponseHeaders responseHeaders = new ResponseHeaders(mockResponse);
        HttpURLConnection con = mockConnection();
        responseHeaders.forwardFastlyHeaders(con);
    }

    private HttpURLConnection mockConnection() {
        HttpURLConnection mock = EasyMock.createMock(HttpURLConnection.class);
        EasyMock.expect(mock.getHeaderField("X-Cache")).andReturn("mock fastly x-cache");
        EasyMock.expect(mock.getHeaderField("X-Cache-Hits")).andReturn("mock fastly x-cache-hits");
        EasyMock.expect(mock.getHeaderField("X-Wovn-Surrogate-Key")).andReturn("mock fastly x-wovn-surrogate-key");
        EasyMock.replay(mock);
        return mock;
    }
}
