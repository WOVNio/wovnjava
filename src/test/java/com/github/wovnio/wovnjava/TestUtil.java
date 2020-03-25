package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

public class TestUtil {
    public static final HashMap<String, String> emptyOption = new HashMap<String, String>();

    public static FilterConfig makeConfig() {
        return makeConfig(emptyOption);
    }

    public static FilterConfig makeConfig(final HashMap<String, String> options) {
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

    public static HttpServletResponse mockResponse(String contentType, String encoding) throws IOException, ConfigurationError {
        return mockResponse(contentType, encoding, false, makeConfig());
    }

    public static HttpServletResponse mockResponse(String contentType, String encoding, boolean isPreviouslyProcessed, FilterConfig option) throws IOException, ConfigurationError {
        HttpServletResponse mock = EasyMock.createMock(HttpServletResponse.class);
        mock.setContentLength(EasyMock.anyInt());
        EasyMock.expectLastCall();
        mock.setCharacterEncoding("utf-8");
        EasyMock.expectLastCall();
        EasyMock.expect(mock.getWriter()).andReturn(new PrintWriter(new StringWriter()));
        EasyMock.expect(mock.getContentType()).andReturn(contentType).atLeastOnce();
        EasyMock.expect(mock.getCharacterEncoding()).andReturn(encoding);

        if (isPreviouslyProcessed) {
            EasyMock.expect(mock.containsHeader("X-Wovn-Handler")).andReturn(true).times(0, 1);
        } else {
            EasyMock.expect(mock.containsHeader("X-Wovn-Handler")).andReturn(false).times(0, 1);

            mock.setHeader(EasyMock.eq("X-Wovn-Api-Status"), EasyMock.anyString());
            EasyMock.expectLastCall().atLeastOnce();

            Settings settings = new Settings(option);

            if (settings.showVersion) {
                mock.setHeader(EasyMock.eq("X-Wovn-Handler"), EasyMock.eq("wovnjava_" + Settings.VERSION));
                EasyMock.expectLastCall().atLeastOnce();
            } else {
                mock.setHeader(EasyMock.eq("X-Wovn-Handler"), EasyMock.eq("wovnjava"));
                EasyMock.expectLastCall().atLeastOnce();
            }
        }
        EasyMock.replay(mock);
        return mock;
    }

    public static HttpServletResponse mockSimpleHttpServletResponse() {
        HttpServletResponse mock = EasyMock.createMock(HttpServletResponse.class);
        return mock;
    }

    public static FilterChainMock doServletFilter(String contentType, String path) throws ServletException, IOException,
            ConfigurationError {
        return doServletFilter(contentType, path, path, emptyOption);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, HashMap<String, String> option) throws ServletException, IOException,
            ConfigurationError {
        return doServletFilter(contentType, path, path, option);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath) throws ServletException, IOException,
            ConfigurationError {
        return doServletFilter(contentType, path, forwardPath, emptyOption);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option) throws ServletException, IOException, ConfigurationError {
        return doServletFilter(contentType, path, forwardPath, option, false);
    }

    public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option, boolean isPreviouslyProcessed) throws ServletException, IOException, ConfigurationError {
        RequestDispatcherMock dispatcher = new RequestDispatcherMock();
        String requestUrl = "https://example.com" + path;
        HttpServletRequest req = MockHttpServletRequest.createWithForwardingDispatcher(requestUrl, forwardPath,
                dispatcher);

        FilterConfig filterConfig = makeConfigWithValidDefaults(option);

        HttpServletResponse res = mockResponse(contentType, "", isPreviouslyProcessed, filterConfig);

        FilterChainMock filterChain = new FilterChainMock();
        WovnServletFilter filter = new WovnServletFilter();
        filter.init(filterConfig);
        filter.doFilter(req, res, filterChain);
        filterChain.req = filterChain.req == null ? dispatcher.req : filterChain.req;
        filterChain.res = filterChain.res == null ? dispatcher.res : filterChain.res;
        return filterChain;
    }
}
