package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

public class TestUtil {
    public static final HashMap<String, String> emptyOption = new HashMap<String, String>();

    public static FilterConfig makeConfig() {
        return makeConfig(emptyOption);
    }

    public static FilterConfig makeConfig(HashMap<String, String> options) {
        FilterConfig mock = EasyMock.createMock(FilterConfig.class);
        EasyMock.expect(mock.getInitParameter(EasyMock.anyString())).andAnswer(
            new IAnswer<String>() {
                @Override
                public String answer() throws Throwable {
                    String arg = (String) EasyMock.getCurrentArguments()[0];
                    return options.get(arg);
                }
            }
        ).anyTimes();
        EasyMock.replay(mock);
        return mock;
    }

    public static Settings makeSettings() throws ConfigurationError {
        return makeSettings(emptyOption);
    }

    public static Settings makeSettings(HashMap<String, String> option) throws ConfigurationError {
        return new Settings(makeConfig(option));
    }

    public static HttpServletRequest mockRequestPath(String path) {
        return mockRequestPath(path, null, new RequestDispatcherMock());
    }

    public static HttpServletRequest mockRequestPath(String path, String replacedPath, RequestDispatcherMock dispatcher) {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn(path).atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getRequestDispatcher(replacedPath == null ? EasyMock.anyString() : replacedPath)).andReturn(dispatcher);
        EasyMock.replay(mock);
        return mock;
    }

    public static HttpServletResponse mockResponse(String contentType, String encoding) throws IOException {
        return mockResponse(contentType, encoding, false);
    }

    public static HttpServletResponse mockResponse(String contentType, String encoding, boolean isPreviouslyProcessed) throws IOException {
        HttpServletResponse mock = EasyMock.createMock(HttpServletResponse.class);
        mock.setContentLength(EasyMock.anyInt());
        EasyMock.expectLastCall();
        mock.setCharacterEncoding("utf-8");
        EasyMock.expectLastCall();
        EasyMock.expect(mock.getWriter()).andReturn(new PrintWriter(new StringWriter()));
        EasyMock.expect(mock.getContentType()).andReturn(contentType).atLeastOnce();
        EasyMock.expect(mock.getCharacterEncoding()).andReturn(encoding);
        mock.setHeader(EasyMock.anyString(), EasyMock.anyString());
        EasyMock.expectLastCall().atLeastOnce();
        if (isPreviouslyProcessed) {
            EasyMock.expect(mock.containsHeader("X-Wovn-Handler")).andReturn(true).times(0,1);
        } else {
            EasyMock.expect(mock.containsHeader("X-Wovn-Handler")).andReturn(false).times(0,1);
        }
        EasyMock.replay(mock);
        return mock;
    }

    public static HttpServletResponse mockSimpleHttpServletResponse() {
        HttpServletResponse mock = EasyMock.createMock(HttpServletResponse.class);
        return mock;
    }

    public static FilterChainMock doServletFilter(String contentType, String path) throws ServletException, IOException {
        return doServletFilter(contentType, path, path, emptyOption);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, HashMap<String, String> option) throws ServletException, IOException {
        return doServletFilter(contentType, path, path, option);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath) throws ServletException, IOException {
        return doServletFilter(contentType, path, forwardPath, emptyOption);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option) throws ServletException, IOException {
        return doServletFilter(contentType, path, forwardPath, option, false);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option, boolean isPreviouslyProcessed) throws ServletException, IOException {
        RequestDispatcherMock dispatcher = new RequestDispatcherMock();
        HttpServletRequest req = mockRequestPath(path, forwardPath, dispatcher);
        HttpServletResponse res = mockResponse(contentType, "", isPreviouslyProcessed);
        FilterConfig filterConfig = makeConfig(option);
        FilterChainMock filterChain = new FilterChainMock();
        WovnServletFilter filter = new WovnServletFilter();
        filter.init(filterConfig);
        filter.doFilter(req, res, filterChain);
        filterChain.req = filterChain.req == null ? dispatcher.req : filterChain.req;
        filterChain.res = filterChain.res == null ? dispatcher.res : filterChain.res;
        return filterChain;
    }
}
