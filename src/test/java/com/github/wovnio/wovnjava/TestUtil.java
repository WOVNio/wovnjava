package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
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

    public static FilterConfig makeConfigWithValidDefaults() {
        return makeConfigWithValidDefaults(emptyOption);
    }

    public static FilterConfig makeConfigWithValidDefaults(HashMap<String, String> options) {
        HashMap<String, String> settings = new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
            put("widgetUrl", "//j.wovn.io/1");
        }};
        settings.putAll(options);
        return TestUtil.makeConfig(settings);
    }

    public static Settings makeSettings() throws ConfigurationError {
        return makeSettings(emptyOption);
    }

    public static Settings makeSettings(HashMap<String, String> options) throws ConfigurationError {
        return new Settings(makeConfigWithValidDefaults(options));
    }

    public static HttpServletResponse mockResponse(String contentType, String encoding, int statusCode) throws IOException {
        return mockResponse(contentType, encoding, false, statusCode);
    }

    public static HttpServletResponse mockResponse(String contentType, String encoding, boolean isPreviouslyProcessed, int statusCode) throws IOException {
        HttpServletResponse mock = EasyMock.createMock(HttpServletResponse.class);
        mock.setCharacterEncoding("UTF-8");
        EasyMock.expectLastCall();
        EasyMock.expect(mock.getWriter()).andReturn(new PrintWriter(new StringWriter()));
        EasyMock.expect(mock.getContentType()).andReturn(contentType).atLeastOnce();
        EasyMock.expect(mock.getCharacterEncoding()).andReturn(encoding);
        EasyMock.expect(mock.getStatus()).andReturn(statusCode);
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
        return doServletFilter(contentType, path, path, emptyOption, 200);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, HashMap<String, String> option) throws ServletException, IOException {
        return doServletFilter(contentType, path, path, option, 200);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath) throws ServletException, IOException {
        return doServletFilter(contentType, path, forwardPath, emptyOption, 200);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option, int statusCode) throws ServletException, IOException {
        return doServletFilter(contentType, path, forwardPath, option, false, statusCode);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option, boolean isPreviouslyProcessed, int statusCode) throws ServletException, IOException {
        RequestDispatcherMock dispatcher = new RequestDispatcherMock();
        String requestUrl = "https://example.com" + path;
        HttpServletRequest req = MockHttpServletRequest.createWithForwardingDispatcher(requestUrl, forwardPath, dispatcher);
        HttpServletResponse res = mockResponse(contentType, "", isPreviouslyProcessed, statusCode);
        FilterConfig filterConfig = makeConfigWithValidDefaults(option);
        FilterChainMock filterChain = new FilterChainMock();
        WovnServletFilter filter = new WovnServletFilter();
        filter.init(filterConfig);
        filter.doFilter(req, res, filterChain);
        filterChain.req = filterChain.req == null ? dispatcher.req : filterChain.req;
        filterChain.res = filterChain.res == null ? dispatcher.res : filterChain.res;
        return filterChain;
    }
}
