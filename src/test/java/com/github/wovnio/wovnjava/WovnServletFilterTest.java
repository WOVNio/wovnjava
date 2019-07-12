package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;


public class WovnServletFilterTest extends TestCase {
    public void testHtml() throws ServletException, IOException {
        FilterChainMock mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/");
        assertEquals("text/html; charset=utf-8", mock.res.getContentType());
        assertEquals("/", mock.req.getRequestURI());
    }

    public void testHtmlWithLang() throws ServletException, IOException {
        FilterChainMock mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/ja/", "/");
        assertEquals("text/html; charset=utf-8", mock.res.getContentType());
        assertEquals("/", mock.req.getRequestURI());
    }

    public void testHtmlWithQueryLang() throws ServletException, IOException {
        FilterChainMock mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/?wovn=ja", queryOption);
        assertEquals("text/html; charset=utf-8", mock.res.getContentType());
        assertEquals("/", mock.req.getRequestURI());
    }

    public void testHtmlWithSubdomain() throws ServletException, IOException {
        FilterChainMock mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/", subdomainOption("ja.wovn.io"));
        assertEquals("text/html; charset=utf-8", mock.res.getContentType());
        assertEquals("/", mock.req.getRequestURI());
    }

    public void testCss() throws ServletException, IOException {
        FilterChainMock mock = TestUtil.doServletFilter("text/css", "/dir/style.css");
        assertEquals("text/css", mock.res.getContentType());
        assertEquals("/dir/style.css", mock.req.getRequestURI());
    }

    public void testCssWithLang() throws ServletException, IOException {
        FilterChainMock mock = TestUtil.doServletFilter("text/css", "/ja/style.css", "/style.css");
        assertEquals("text/css", mock.res.getContentType());
        assertEquals("/style.css", mock.req.getRequestURI());
    }

    public void testImage() throws ServletException, IOException {
        FilterChainMock mock = TestUtil.doServletFilter("image/png", "/image.png");
        assertEquals("image/png", mock.res.getContentType());
        assertEquals("/image.png", mock.req.getRequestURI());
    }

    public void testImageWithLang() throws ServletException, IOException {
        FilterChainMock mock = TestUtil.doServletFilter("image/png", "/ja/image.png", "image.png");
        assertEquals("image/png", mock.res.getContentType());
        assertEquals("/image.png", mock.req.getRequestURI());
    }

    public void testProcessRequestOnce__RequestNotProcessed() throws ServletException, IOException {
        HashMap<String, String> config = new HashMap<String, String>() {{
            put("urlPattern", "path");
            put("defaultLang", "ja");
            put("location", "https://example.com/ja/search/");
        }};
        boolean requestIsAlreadyProcessed = false;
        FilterChainMock mock = TestUtil.doServletFilter("text/html", "/search/", "/search/", TestUtil.emptyOption, requestIsAlreadyProcessed);
        HttpServletResponse res = (HttpServletResponse)mock.res;
        // WovnServletFilter will always set API status HTTP header when processing the request
        assertEquals(true, res.getHeader("X-Wovn-Api-Status") != null); // Cannot check this because `getHeader` is mocked
    }
    /*
     */

    /*
    //public static FilterChainMock doServletFilter(String contentType, String path, String forwardPath, HashMap<String, String> option) throws ServletException, IOException {
    public void testProcessRequestOnce__RequestNotProcessed() {
        RequestDispatcherMock dispatcher = new RequestDispatcherMock();
        HttpServletRequest req = TestUtil.mockRequestPath("/", "/", dispatcher);
        HttpServletResponse res = TestUtil.mockResponse("text/html; charset=utf-8", "", "");
        FilterConfig filterConfig = makeConfig(option);
        FilterChainMock filterChain = new FilterChainMock();
        WovnServletFilter filter = new WovnServletFilter();
        filter.init(filterConfig);
        filter.doFilter(req, res, filterChain);
        filterChain.req = filterChain.req == null ? dispatcher.req : filterChain.req;
        filterChain.res = filterChain.res == null ? dispatcher.res : filterChain.res;
        return filterChain;

    }
    public void blrblrblr() throws ServletException, IOException {
        FilterChainMock mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/ja/", "/");
        assertEquals("text/html; charset=utf-8", mock.res.getContentType());
        assertEquals("/", mock.req.getRequestURI());
    }
    */

    private final HashMap<String, String> queryOption = new HashMap<String, String>() {{
        put("urlPattern", "query");
    }};

    private HashMap<String, String> subdomainOption(String host) {
        return new HashMap<String, String>() {{
            put("urlPattern", "subdomain");
            put("host", host);
        }};
    }

}
