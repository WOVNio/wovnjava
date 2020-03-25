package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;

public class WovnServletFilterTest extends TestCase {
    public void testHtml() throws ServletException, IOException, ConfigurationError {
        FilterChainMock mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/", pathOption);
        assertEquals("text/html; charset=utf-8", mock.res.getContentType());
        assertEquals("/", mock.req.getRequestURI());
    }

    public void testHtmlWithLang() throws ServletException, IOException, ConfigurationError {
        FilterChainMock mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/ja/", "/", pathOption);
        assertEquals("text/html; charset=utf-8", mock.res.getContentType());
        assertEquals("https://example.com/", mock.req.getRequestURL().toString());
    }

    public void testHtmlWithQueryLang() throws ServletException, IOException, ConfigurationError {
        FilterChainMock mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/?wovn=ja", queryOption);
        assertEquals("text/html; charset=utf-8", mock.res.getContentType());
        assertEquals("/", mock.req.getRequestURI());
    }

    public void testHtmlWithSubdomain() throws ServletException, IOException, ConfigurationError {
        FilterChainMock mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/", subdomainOption("ja.wovn.io"));
        assertEquals("text/html; charset=utf-8", mock.res.getContentType());
        assertEquals("/", mock.req.getRequestURI());
    }

    public void testCss() throws ServletException, IOException, ConfigurationError {
        FilterChainMock mock = TestUtil.doServletFilter("text/css", "/dir/style.css", pathOption);
        assertEquals("text/css", mock.res.getContentType());
        assertEquals("/dir/style.css", mock.req.getRequestURI());
    }

    public void testCssWithLang() throws ServletException, IOException, ConfigurationError {
        FilterChainMock mock = TestUtil.doServletFilter("text/css", "/ja/style.css", "/style.css", pathOption);
        assertEquals("text/css", mock.res.getContentType());
        assertEquals("https://example.com/style.css", mock.req.getRequestURL().toString());
    }

    public void testImage() throws ServletException, IOException, ConfigurationError {
        FilterChainMock mock = TestUtil.doServletFilter("image/png", "/image.png", pathOption);
        assertEquals("image/png", mock.res.getContentType());
        assertEquals("/image.png", mock.req.getRequestURI());
    }

    public void testImageWithLang() throws ServletException, IOException, ConfigurationError {
        FilterChainMock mock = TestUtil.doServletFilter("image/png", "/ja/image.png", "/image.png", pathOption);
        assertEquals("image/png", mock.res.getContentType());
        assertEquals("https://example.com/image.png", mock.req.getRequestURL().toString());
    }

    public void testProcessRequestOnce__RequestNotProcessed__ProcessRequest() throws ServletException, IOException,
            ConfigurationError {
        HashMap<String, String> config = new HashMap<String, String>() {{
            put("urlPattern", "path");
            put("defaultLang", "ja");
            put("location", "https://example.com/ja/search/");
        }};
        boolean requestIsAlreadyProcessed = false;
        FilterChainMock mock = TestUtil.doServletFilter("text/html", "/search/", "/search/", TestUtil.emptyOption, requestIsAlreadyProcessed);

        ServletResponse responseObjectPassedToFilterChain = mock.res;
        // If wovnjava is intercepting the request, the response object should be wrapped in a WovnHttpServletResponse
        assertEquals(true, responseObjectPassedToFilterChain instanceof HttpServletResponse);
        assertEquals(true, responseObjectPassedToFilterChain instanceof WovnHttpServletResponse);
    }

    public void testProcessRequestOnce__RequestAlreadyProcessed__DoNotProcessRequestAgain() throws ServletException, IOException,
            ConfigurationError {
        HashMap<String, String> config = new HashMap<String, String>() {{
            put("urlPattern", "path");
            put("defaultLang", "ja");
            put("location", "https://example.com/ja/search/");
        }};
        boolean requestIsAlreadyProcessed = true;
        FilterChainMock mock = TestUtil.doServletFilter("text/html", "/search/", "/search/", TestUtil.emptyOption, requestIsAlreadyProcessed);

        ServletResponse responseObjectPassedToFilterChain = mock.res;
        // If wovnjava is ignoring the request, the response object should NOT be wrapped in a WovnHttpServletResponse
        assertEquals(true, responseObjectPassedToFilterChain instanceof HttpServletResponse);
        assertEquals(false, responseObjectPassedToFilterChain instanceof WovnHttpServletResponse);
    }

    public void testConfigShowVersion() throws ServletException, IOException, ConfigurationError {
        FilterChainMock mock = TestUtil.doServletFilter("text/html", "/", showVersionOption);
    }

    private final HashMap<String, String> pathOption = new HashMap<String, String>() {{
        put("urlPattern", "path");
    }};

    private final HashMap<String, String> queryOption = new HashMap<String, String>() {{
        put("urlPattern", "query");
    }};

    private HashMap<String, String> subdomainOption(final String host) {
        return new HashMap<String, String>() {{
            put("urlPattern", "subdomain");
            put("host", host);
        }};
    }

    private final HashMap<String, String> showVersionOption = new HashMap<String, String>() {{
        put("showVersion", "false");
    }};
}
