package com.github.wovnio.wovnjava;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

public class TestUtil {
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
        return makeConfigWithValidDefaults(new HashMap<>());
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
        return makeSettings(new HashMap<>());
    }

    public static Settings makeSettings(HashMap<String, String> options) throws ConfigurationError {
        return new Settings(makeConfigWithValidDefaults(options));
    }

    public static HttpServletResponse mockResponse(String contentType, String encoding, boolean isPreviouslyProcessed, int statusCode, int expectedContentLength, StringWriter responseBuffer) throws IOException {
        HttpServletResponse mock = EasyMock.createMock(HttpServletResponse.class);
        if (expectedContentLength != -1) {
            mock.setContentLength(expectedContentLength);
            EasyMock.expectLastCall();
        }
        mock.setCharacterEncoding("UTF-8");
        EasyMock.expectLastCall();
        EasyMock.expect(mock.getWriter()).andReturn(new PrintWriter(responseBuffer)).anyTimes();
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

    public static TestFilterResult doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option, String originalResponseBody) throws ServletException, IOException {
        return doServletFilter(contentType, path, forwardPath, option, false, 200, originalResponseBody);
    }

    public static TestFilterResult doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option, boolean isPreviouslyProcessed, int statusCode, String originalResponseBody) throws ServletException, IOException {
        return doServletFilter(contentType, path, forwardPath, option, isPreviouslyProcessed, statusCode, originalResponseBody, -1);
    }

    public static TestFilterResult doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option, boolean isPreviouslyProcessed, int statusCode, String originalResponseBody, int expectedContentLength) throws ServletException, IOException {
        RequestDispatcherMock customerApplication = new RequestDispatcherMock(originalResponseBody);
        String requestUrl = "https://example.com" + path;
        HttpServletRequest req = MockHttpServletRequest.createWithForwardingDispatcher(requestUrl, forwardPath, customerApplication);

        StringWriter responseBuffer = new StringWriter();
        HttpServletResponse res = mockResponse(contentType, "UTF-8", isPreviouslyProcessed, statusCode, expectedContentLength, responseBuffer);

        FilterConfig filterConfig = makeConfigWithValidDefaults(option);
        FilterChainMock filterChain = new FilterChainMock(originalResponseBody);
        WovnServletFilter filter = new WovnServletFilter();
        filter.init(filterConfig);

        filter.doFilter(req, res, filterChain);

        filterChain.req = filterChain.req == null ? customerApplication.req : filterChain.req;
        filterChain.res = filterChain.res == null ? res : filterChain.res;

        return new TestUtil.TestFilterResult(responseBuffer, filterChain.req, filterChain.res);
    }

    public static class TestFilterResult {
        public StringWriter responseBuffer;
        public HttpServletRequest request;
        public ServletResponse response;

        public TestFilterResult(StringWriter responseBuffer, HttpServletRequest request, ServletResponse response) {
            this.responseBuffer = responseBuffer;
            this.request = request;
            this.response = response;
        }
    }
}
