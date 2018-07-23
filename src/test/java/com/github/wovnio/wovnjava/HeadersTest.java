package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.HashMap;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

public class HeadersTest extends TestCase {

    private static FilterConfig mockConfigPath() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("secretKey", "secret");
        }};
        return TestUtil.makeConfig(parameters);
    }
    private static FilterConfig mockConfigSubdomain() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("secretKey", "secret");
            put("urlPattern", "subdomain");
        }};
        return TestUtil.makeConfig(parameters);
    }
    private static FilterConfig mockConfigQuery() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("secretKey", "secret");
            put("urlPattern", "query");
        }};
        return TestUtil.makeConfig(parameters);
    }

    private static FilterConfig mockConfigQueryParameter() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("secretKey", "secret");
            put("urlPattern", "query");
            put("query", "abc");
        }};
        return TestUtil.makeConfig(parameters);
    }

    private static FilterConfig mockConfigQueryParameterAAA() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("secretKey", "secret");
            put("urlPattern", "query");
            put("query", "AAA");
        }};
        return TestUtil.makeConfig(parameters);
    }

    private static FilterConfig mockConfigOriginalHeaders() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("secretKey", "secret");
            put("query", "baz");
            put("originalUrlHeader", "REDIRECT_URL");
            put("originalQueryStringHeader", "REDIRECT_QUERY_STRING");
        }};
        return TestUtil.makeConfig(parameters);
    }

    private static HttpServletRequest mockRequestPath() {
        return mockRequestPath("/ja/test");
    }
    private static HttpServletRequest mockRequestPath(String path) {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https");
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn(path).atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.replay(mock);

        return mock;
    }
    private static HttpServletRequest mockRequestSubdomain() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https");
        EasyMock.expect(mock.getRemoteHost()).andReturn("ja.example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("ja.example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.replay(mock);

        return mock;
    }
    private static HttpServletRequest mockRequestQuery() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https");
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("wovn=ja").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.replay(mock);

        return mock;
    }

    private static HttpServletRequest mockRequestQueryParameter() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https");
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("def=456&wovn=ja&abc=123").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.replay(mock);

        return mock;
    }

    private static HttpServletRequest mockRequestServerPort() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https");
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/ja/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(8080).atLeastOnce();
        EasyMock.replay(mock);

        return mock;
    }

    private static HttpServletRequest mockRequestOriginalHeaders() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https");
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/ja/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getHeader("REDIRECT_URL")).andReturn("/foo/bar").atLeastOnce();
        EasyMock.expect(mock.getHeader("REDIRECT_QUERY_STRING")).andReturn("baz=123").atLeastOnce();
        EasyMock.replay(mock);

        return mock;

    }

    public void testHeaders() {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertNotNull(h);
    }

    public void testHeadersWithoutQueryParameter() {
        HttpServletRequest mockRequest = mockRequestQueryParameter();
        FilterConfig mockConfig = mockConfigQuery();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertNotNull(h);
        assertEquals("", h.query);
    }

    public void testHeadersWithQueryParameter() {
        HttpServletRequest mockRequest = mockRequestQueryParameter();
        FilterConfig mockConfig = mockConfigQueryParameter();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertNotNull(h);
        assertEquals("?abc=123", h.query);
    }

    public void testGetPathLangPath() {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertEquals("ja", h.getPathLang());
    }

    public void testGetPathLangSubdomain() {
        HttpServletRequest mockRequest = mockRequestSubdomain();
        FilterConfig mockConfig = mockConfigSubdomain();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertEquals("ja", h.getPathLang());
    }

    public void testGetPathLangQuery() {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertEquals("ja", h.getPathLang());
    }

    public void testRedirectLocationPathTop() {
        Headers h = new Headers(TestUtil.mockRequestPath("/"), TestUtil.makeSettings());
        assertEquals("https://example.com/", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/", h.redirectLocation("ja"));
    }

    public void testRedirectLocationPathDirectory() {
        Headers h = new Headers(TestUtil.mockRequestPath("/test/"), TestUtil.makeSettings());
        assertEquals("https://example.com/test/", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/test/", h.redirectLocation("ja"));
    }

    public void testRedirectLocationPathFile() {
        Headers h = new Headers(TestUtil.mockRequestPath("/foo.html"), TestUtil.makeSettings());
        assertEquals("https://example.com/foo.html", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/foo.html", h.redirectLocation("ja"));
    }

    public void testRedirectLocationPathDirectoryAndFile() {
        Headers h = new Headers(TestUtil.mockRequestPath("/dir/foo.html"), TestUtil.makeSettings());
        assertEquals("https://example.com/dir/foo.html", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/dir/foo.html", h.redirectLocation("ja"));
    }

    public void testRedirectLocationPathNestedDirectory() {
        Headers h = new Headers(TestUtil.mockRequestPath("/dir1/dir2/"), TestUtil.makeSettings());
        assertEquals("https://example.com/dir1/dir2/", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/dir1/dir2/", h.redirectLocation("ja"));
    }

    public void testRedirectLocationSubdomain() {

    }
    public void testRedirectLocationQuery() {

    }

    public void testRemoveLangPath() {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertEquals("example.com/test", h.removeLang("example.com/ja/test", null));
    }
    public void testRemoveLangSubdomain() {
        HttpServletRequest mockRequest = mockRequestSubdomain();
        FilterConfig mockConfig = mockConfigSubdomain();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertEquals("example.com/test", h.removeLang("ja.example.com/test", null));
    }
    public void testRemoveLangQuery() {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertEquals("example.com/test", h.removeLang("example.com/test?wovn=ja", null));
    }

    public void testNotMatchQuery() {
        HttpServletRequest mockRequest = mockRequestQueryParameter();
        FilterConfig mockConfig = mockConfigQueryParameterAAA();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertEquals("", h.query);
    }

    public void testServerPort() {
        HttpServletRequest mockRequest = mockRequestServerPort();
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertEquals("example.com:8080/test", h.pageUrl);
    }

    public void testSitePrefixPath() {
        Headers h = makeHeader("/global/en/foo", "/global/");
        assertEquals("/global/", h.removeLang("/global/en/", null));
        assertEquals("/en/global/", h.removeLang("/en/global/", null));
    }

    private Headers makeHeader(String requestPath, String sitePrefix) {
        HttpServletRequest mockRequest = mockRequestPath(requestPath);
        HashMap<String, String> option = new HashMap<String, String>(){ { put("sitePrefixPath", "/global/"); } };
        Settings s = TestUtil.makeSettings(option);
        return new Headers(mockRequest, s);
    }

    public void testOriginalHeaders() {
        HttpServletRequest mockRequest = mockRequestOriginalHeaders();
        FilterConfig mockConfig = mockConfigOriginalHeaders();

        Settings s = new Settings(mockConfig);
        Headers h = new Headers(mockRequest, s);

        assertEquals("/foo/bar", h.pathName);
        assertEquals("?baz=123", h.query);
        assertEquals("example.com/foo/bar?baz=123", h.pageUrl);
    }
}
