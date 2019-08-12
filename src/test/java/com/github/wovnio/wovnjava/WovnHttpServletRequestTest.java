package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import java.util.*;

import org.easymock.EasyMock;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

public class WovnHttpServletRequestTest extends TestCase {

    private static HttpServletRequest mockRequestPath() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getRequestURI()).andReturn("/en/test").atLeastOnce();
        EasyMock.expect(mock.getRequestURL()).andReturn(new StringBuffer("/en/test")).atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn(null).atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getServletPath()).andReturn("/en/test").atLeastOnce();
        EasyMock.expect(mock.getHeaderNames()).andReturn(new Vector<String>().elements());
        EasyMock.expect(mock.getHeader("X-Header-Key")).andReturn("x-header-value");
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(mock);
        return mock;
    }

    private static HttpServletRequest mockRequestSubDomain() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn("en.example.com").atLeastOnce();
        EasyMock.expect(mock.getRequestURI()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getRequestURL()).andReturn(new StringBuffer("/test")).atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("en.example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn(null).atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getServletPath()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getHeaderNames()).andReturn(new Vector<String>().elements());
        EasyMock.expect(mock.getHeader("X-Header-Key")).andReturn("x-header-value");
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(mock);
        return mock;
    }

    private static HttpServletRequest mockRequestQuery() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getRequestURI()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getRequestURL()).andReturn(new StringBuffer("/test")).atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("wovn=en").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getServletPath()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getHeaderNames()).andReturn(new Vector<String>().elements());
        EasyMock.expect(mock.getHeader("X-Header-Key")).andReturn("x-header-value");
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(mock);
        return mock;
    }

    private static FilterConfig mockConfigPath() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
        }};
        return TestUtil.makeConfig(parameters);
    }

    private static FilterConfig mockConfigSubDomain() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("urlPattern", "subdomain");
        }};
        return TestUtil.makeConfig(parameters);
    }

    private static FilterConfig mockConfigQuery() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("urlPattern", "query");
        }};
        return TestUtil.makeConfig(parameters);
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

    public void testGetRemoteHostWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("example.com", wovnRequest.getRemoteHost());
    }

    public void testGetRemoteHostWithSubDomain() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestSubDomain();
        FilterConfig mockConfig = mockConfigSubDomain();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("example.com", wovnRequest.getRemoteHost());
    }

    public void testGetRemoteHostWithQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("example.com", wovnRequest.getRemoteHost());
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

    public void testGetRequestURIWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("/test", wovnRequest.getRequestURI());
    }

    public void testGetRequestURIWithSubDomain() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestSubDomain();
        FilterConfig mockConfig = mockConfigSubDomain();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("/test", wovnRequest.getRequestURI());
    }

    public void testGetRequestURIWithQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("/test", wovnRequest.getRequestURI());
    }

    public void testGetRequestURLWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("/test", wovnRequest.getRequestURL().toString());
    }

    public void testGetRequestURLWithSubDomain() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestSubDomain();
        FilterConfig mockConfig = mockConfigSubDomain();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("/test", wovnRequest.getRequestURL().toString());
    }

    public void testGetRequestURLWithQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("/test", wovnRequest.getRequestURL().toString());
    }

    public void testGetServletPathWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("/test", wovnRequest.getServletPath());
    }

    public void testGetServletPathWithSubDomain() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestSubDomain();
        FilterConfig mockConfig = mockConfigSubDomain();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("/test", wovnRequest.getServletPath());
    }

    public void testGetServletPathWithQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("/test", wovnRequest.getServletPath());
    }

    public void testWovnLangHeaderWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings settings = new Settings(mockConfig);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(mockRequest, headers);

        assertEquals("en", wovnRequest.getHeader("X-Wovn-Lang"));
        assertEquals("x-header-value", wovnRequest.getHeader("X-Header-Key"));

        Enumeration<String> reqHeaders = wovnRequest.getHeaderNames();
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
        assertEquals("x-header-value", wovnRequest.getHeader("X-Header-Key"));

        Enumeration<String> reqHeaders = wovnRequest.getHeaderNames();
        assertEquals(true, reqHeaders.hasMoreElements());
        assertEquals("X-Wovn-Lang", reqHeaders.nextElement());
    }
}
