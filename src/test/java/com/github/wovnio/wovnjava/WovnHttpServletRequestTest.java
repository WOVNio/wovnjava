package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Enumeration;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;

public class WovnHttpServletRequestTest extends TestCase {

    private static HttpServletRequest mockRequestPath() {
        return MockHttpServletRequest.create("https://example.com/en/test");
    }

    private static HttpServletRequest mockRequestSubDomain() {
        return MockHttpServletRequest.create("https://en.example.com/test");
    }

    private static HttpServletRequest mockRequestQuery() {
        return MockHttpServletRequest.create("https://example.com/test?wovn=en");
    }

    private static FilterConfig mockConfigPath() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("urlPattern", "path");
        }};
        return TestUtil.makeConfigWithValidDefaults(parameters);
    }

    private static FilterConfig mockConfigSubDomain() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("urlPattern", "subdomain");
        }};
        return TestUtil.makeConfigWithValidDefaults(parameters);
    }

    private static FilterConfig mockConfigQuery() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("urlPattern", "query");
        }};
        return TestUtil.makeConfigWithValidDefaults(parameters);
    }

    public void testWovnHttpServletRequest() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertNotNull(wovnRequest);
    }

    public void testGetRemoteHost__PathPattern__DoNotModify() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.createWithRemoteHost("https://site.com/en/", "proxy.com");
        FilterConfig mockConfig = mockConfigPath();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("proxy.com", wovnRequest.getRemoteHost());
    }

    public void testGetRemoteHost__SubdomainPattern__RemoveLanguageCodeFromRemoteHost() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.createWithRemoteHost("https://en.site.com/", "en.proxy.com");
        FilterConfig mockConfig = mockConfigSubDomain();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("proxy.com", wovnRequest.getRemoteHost());
    }

    public void testGetRemoteHost__QueryPattern__DoNotModify() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.createWithRemoteHost("https://site.com/?wovn=en", "proxy.com");
        FilterConfig mockConfig = mockConfigQuery();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("proxy.com", wovnRequest.getRemoteHost());
    }

    public void testGetServerNameWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("example.com", wovnRequest.getServerName());
    }

    public void testGetServerNameWithSubDomain() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestSubDomain();
        FilterConfig mockConfig = mockConfigSubDomain();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("example.com", wovnRequest.getServerName());
    }

    public void testGetServerNameWithQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("example.com", wovnRequest.getServerName());
    }

    public void testGetRequestURLWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("https://example.com/test", wovnRequest.getRequestURL().toString());
    }

    public void testGetRequestURLWithSubDomain() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestSubDomain();
        FilterConfig mockConfig = mockConfigSubDomain();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("https://example.com/test", wovnRequest.getRequestURL().toString());
    }

    public void testGetRequestURLWithQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("https://example.com/test", wovnRequest.getRequestURL().toString());
    }

    public void testWovnLangHeaderWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("en", wovnRequest.getHeader("X-Wovn-Lang"));
        assertEquals("x-header-test-value", wovnRequest.getHeader("X-Header-Test"));

        Enumeration<String> reqHeaders = wovnRequest.getHeaderNames();
        assertEquals(true, reqHeaders.hasMoreElements());
        assertEquals("X-Header-Test", reqHeaders.nextElement());
        assertEquals(true, reqHeaders.hasMoreElements());
        assertEquals("X-Wovn-Lang", reqHeaders.nextElement());
    }

    public void testWovnLangHeaderWithQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("en", wovnRequest.getHeader("X-Wovn-Lang"));
        assertEquals("x-header-test-value", wovnRequest.getHeader("X-Header-Test"));

        Enumeration<String> reqHeaders = wovnRequest.getHeaderNames();
        assertEquals(true, reqHeaders.hasMoreElements());
        assertEquals("X-Header-Test", reqHeaders.nextElement());
        assertEquals(true, reqHeaders.hasMoreElements());
        assertEquals("X-Wovn-Lang", reqHeaders.nextElement());
    }
}
