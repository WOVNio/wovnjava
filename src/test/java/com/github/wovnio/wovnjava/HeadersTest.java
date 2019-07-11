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
        return mockRequestPath(path, "example.com");
    }
    private static HttpServletRequest mockRequestPath(String path, String host) {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn(host);
        EasyMock.expect(mock.getRequestURI()).andReturn(path).atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn(host).atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn(null).atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(mock);

        return mock;
    }
    private static HttpServletRequest mockRequestSubdomain() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn("ja.example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("ja.example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn(null).atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(mock);

        return mock;
    }
    private static HttpServletRequest mockRequestQuery() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("wovn=ja").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(mock);

        return mock;
    }

    private static HttpServletRequest mockRequestQueryParameter() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn("def=456&wovn=ja&abc=123").atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(mock);

        return mock;
    }

    private static HttpServletRequest mockRequestServerPort() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/ja/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn(null).atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(8080).atLeastOnce();
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(mock);

        return mock;
    }

    private static HttpServletRequest mockRequestOriginalHeaders() {
        HttpServletRequest mock = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mock.getScheme()).andReturn("https").atLeastOnce();
        EasyMock.expect(mock.getRemoteHost()).andReturn("example.com");
        EasyMock.expect(mock.getRequestURI()).andReturn("/ja/test").atLeastOnce();
        EasyMock.expect(mock.getServerName()).andReturn("example.com").atLeastOnce();
        EasyMock.expect(mock.getQueryString()).andReturn(null).atLeastOnce();
        EasyMock.expect(mock.getServerPort()).andReturn(443).atLeastOnce();
        EasyMock.expect(mock.getHeader("REDIRECT_URL")).andReturn("/foo/bar").atLeastOnce();
        EasyMock.expect(mock.getHeader("REDIRECT_QUERY_STRING")).andReturn("baz=123").atLeastOnce();
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(mock.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(mock);

        return mock;

    }

    public void testHeaders() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertNotNull(h);
    }

    public void testHeadersWithoutQueryParameter() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQueryParameter();
        FilterConfig mockConfig = mockConfigQuery();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertNotNull(h);
        assertEquals("", h.query);
    }

    public void testHeadersWithQueryParameter() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQueryParameter();
        FilterConfig mockConfig = mockConfigQueryParameter();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertNotNull(h);
        assertEquals("?abc=123", h.query);
    }

    public void testGetRequestLangPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals("ja", h.getRequestLang());
    }

    public void testGetRequestLangSubdomain() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestSubdomain();
        FilterConfig mockConfig = mockConfigSubdomain();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals("ja", h.getRequestLang());
    }

    public void testGetRequestLangQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals("ja", h.getRequestLang());
    }

    public void testRedirectLocationPathTop() throws ConfigurationError {
        Settings s = TestUtil.makeSettings();
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(TestUtil.mockRequestPath("/"), s, ulph);
        assertEquals("https://example.com/", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/", h.redirectLocation("ja"));
    }

    public void testRedirectLocationPathDirectory() throws ConfigurationError {
        Settings s = TestUtil.makeSettings();
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(TestUtil.mockRequestPath("/test/"), s, ulph);
        assertEquals("https://example.com/test/", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/test/", h.redirectLocation("ja"));
    }

    public void testRedirectLocationPathFile() throws ConfigurationError {
        Settings s = TestUtil.makeSettings();
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(TestUtil.mockRequestPath("/foo.html"), s, ulph);
        assertEquals("https://example.com/foo.html", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/foo.html", h.redirectLocation("ja"));
    }

    public void testRedirectLocationPathDirectoryAndFile() throws ConfigurationError {
        Settings s = TestUtil.makeSettings();
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(TestUtil.mockRequestPath("/dir/foo.html"), s, ulph);
        assertEquals("https://example.com/dir/foo.html", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/dir/foo.html", h.redirectLocation("ja"));
    }

    public void testRedirectLocationPathNestedDirectory() throws ConfigurationError {
        Settings s = TestUtil.makeSettings();
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(TestUtil.mockRequestPath("/dir1/dir2/"), s, ulph);
        assertEquals("https://example.com/dir1/dir2/", h.redirectLocation("en"));
        assertEquals("https://example.com/ja/dir1/dir2/", h.redirectLocation("ja"));
    }

    public void testRedirectLocation_WithSitePrefixPathBasePathOnly_AddsLanguageToUrl() throws ConfigurationError {
        Headers h = makeHeaderWithSitePrefixPath("/global", "/global/");
        assertEquals("https://example.com/global", h.redirectLocation("en")); // `en` is defaultLang
        assertEquals("https://example.com/global/ja/", h.redirectLocation("ja"));
        assertEquals("https://example.com/global/garbage/", h.redirectLocation("garbage"));
    }

    public void testRedirectLocation_WithSitePrefixPathMatchingPath_AddsLanguageToUrl() throws ConfigurationError {
        Headers h = makeHeaderWithSitePrefixPath("/global/tokyo/", "/global/");
        assertEquals("https://example.com/global/tokyo/", h.redirectLocation("en")); // `en` is defaultLang
        assertEquals("https://example.com/global/ja/tokyo/", h.redirectLocation("ja"));
        assertEquals("https://example.com/global/garbage/tokyo/", h.redirectLocation("garbage"));
    }

    public void testRedirectLocation_WithSitePrefixPathNonmatchingPath_DoesNotModifyUrl() throws ConfigurationError {
        Headers h = makeHeaderWithSitePrefixPath("/tokyo/global/", "/global/");
        assertEquals("https://example.com/tokyo/global/", h.redirectLocation("en")); // `en` is defaultLang
        assertEquals("https://example.com/tokyo/global/", h.redirectLocation("ja"));
        assertEquals("https://example.com/tokyo/global/", h.redirectLocation("garbage"));
    }

    public void testRedirectLocationSubdomain() {

    }
    public void testRedirectLocationQuery() {

    }

    public void testRemoveLangPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath();
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals("example.com/test", h.removeLang("example.com/ja/test", null));
    }
    public void testRemoveLangSubdomain() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestSubdomain();
        FilterConfig mockConfig = mockConfigSubdomain();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals("example.com/test", h.removeLang("ja.example.com/test", null));
    }
    public void testRemoveLangQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQuery();
        FilterConfig mockConfig = mockConfigQuery();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals("example.com/test", h.removeLang("example.com/test?wovn=ja", null));
    }

    public void testNotMatchQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestQueryParameter();
        FilterConfig mockConfig = mockConfigQueryParameterAAA();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals("", h.query);
    }

    public void testServerPort() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestServerPort();
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals("example.com:8080/test", h.pageUrl);
    }

    public void testSitePrefixPath() throws ConfigurationError {
        Headers h = makeHeaderWithSitePrefixPath("/global/en/foo", "/global/");
        assertEquals("/global/", h.removeLang("/global/en/", null));
        assertEquals("/en/global/", h.removeLang("/en/global/", null));
    }

    public void testLocationWithDefaultLangCode() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath("/signin");
        FilterConfig mockConfig = mockConfigPath();
        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);
        assertEquals("http://example.com/", h.locationWithLangCode("http://example.com/"));
        assertEquals("https://example.com/", h.locationWithLangCode("https://example.com/"));
        assertEquals("https://example.com/dir/file", h.locationWithLangCode("https://example.com/dir/file"));
    }

    public void testLocationWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath("/ja/dir/signin");
        FilterConfig mockConfig = mockConfigPath();
        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);
        assertEquals("http://example.com/ja/", h.locationWithLangCode("http://example.com/"));
        assertEquals("https://example.com/ja/", h.locationWithLangCode("https://example.com/"));
        assertEquals("https://example.com/ja/dir/file", h.locationWithLangCode("https://example.com/dir/file"));
        assertEquals("https://other.com/dir/file", h.locationWithLangCode("https://other.com/dir/file"));
        assertEquals("https://example.com/ja/", h.locationWithLangCode("/"));
        assertEquals("https://example.com/ja/dir/file", h.locationWithLangCode("/dir/file"));
        assertEquals("https://example.com/ja/dir/file", h.locationWithLangCode("./file"));
        assertEquals("https://example.com/ja/file", h.locationWithLangCode("../file"));
        assertEquals("https://example.com/ja/file", h.locationWithLangCode("../../file"));
    }

    public void testLocationWithPathAndTrailingSlash() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath("/ja/dir/signin/");
        FilterConfig mockConfig = mockConfigPath();
        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);
        assertEquals("https://example.com/ja/dir/signin/file", h.locationWithLangCode("./file"));
        assertEquals("https://example.com/ja/dir/file", h.locationWithLangCode("../file"));
        assertEquals("https://example.com/ja/file", h.locationWithLangCode("../../file"));
        assertEquals("https://example.com/ja/file", h.locationWithLangCode("../../../file"));
    }

    public void testLocationWithPathAndTopLevel() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath("/location.jsp?wovn=ja");
        FilterConfig mockConfig = mockConfigQuery();
        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);
        assertEquals("https://example.com/index.jsp?wovn=ja", h.locationWithLangCode("./index.jsp"));
    }

    public void testLocationWithQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath("/dir/signin?wovn=ja");
        FilterConfig mockConfig = mockConfigQuery();
        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);
        assertEquals("http://example.com/?wovn=ja", h.locationWithLangCode("http://example.com/"));
        assertEquals("https://example.com/?wovn=ja", h.locationWithLangCode("https://example.com/"));
        assertEquals("https://example.com/dir/file?wovn=ja", h.locationWithLangCode("https://example.com/dir/file"));
        assertEquals("https://other.com/dir/file", h.locationWithLangCode("https://other.com/dir/file"));
        assertEquals("https://example.com/?wovn=ja", h.locationWithLangCode("/"));
        assertEquals("https://example.com/dir/file?wovn=ja", h.locationWithLangCode("/dir/file"));
        assertEquals("https://example.com/dir/file?wovn=ja", h.locationWithLangCode("./file"));
        assertEquals("https://example.com/file?wovn=ja", h.locationWithLangCode("../file"));
        assertEquals("https://example.com/file?wovn=ja", h.locationWithLangCode("../../file"));
        assertEquals("../../file?q=hello&wovn=zh-CHS", h.locationWithLangCode("../../file?q=hello&wovn=zh-CHS"));
        assertEquals("../../file?wovn=zh-CHS", h.locationWithLangCode("../../file?wovn=zh-CHS"));
    }

    public void testLocationWithSubdomain() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath("/dir/signin", "ja.example.com");
        FilterConfig mockConfig = mockConfigSubdomain();
        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);
        assertEquals("http://ja.example.com/", h.locationWithLangCode("http://example.com/"));
        assertEquals("https://ja.example.com/", h.locationWithLangCode("https://example.com/"));
        assertEquals("https://ja.example.com/dir/file", h.locationWithLangCode("https://example.com/dir/file"));
        assertEquals("https://other.com/dir/file", h.locationWithLangCode("https://other.com/dir/file"));
        assertEquals("https://fr.example.com/dir/file", h.locationWithLangCode("https://fr.example.com/dir/file"));
        assertEquals("https://ja.example.com/", h.locationWithLangCode("/"));
        assertEquals("https://ja.example.com/dir/file", h.locationWithLangCode("/dir/file"));
        assertEquals("https://ja.example.com/dir/file", h.locationWithLangCode("./file"));
        assertEquals("https://ja.example.com/file", h.locationWithLangCode("../file"));
        assertEquals("https://ja.example.com/file", h.locationWithLangCode("../../file"));
    }

    public void testLocationWithSitePrefixPath() throws ConfigurationError {
        Headers h = makeHeaderWithSitePrefixPath("/global/ja/foo", "/global/");
        assertEquals("http://example.com/", h.locationWithLangCode("http://example.com/"));
        assertEquals("http://example.com/global/ja/", h.locationWithLangCode("http://example.com/global/"));
        assertEquals("https://example.com/global/ja/", h.locationWithLangCode("https://example.com/global/"));
        assertEquals("https://example.com/global/ja/", h.locationWithLangCode("https://example.com/global/ja/"));
        assertEquals("https://example.com/global/th/", h.locationWithLangCode("https://example.com/global/th/"));
        assertEquals("https://example.com/global/ja/tokyo/", h.locationWithLangCode("https://example.com/global/tokyo/"));
        assertEquals("https://example.com/global/ja/file.html", h.locationWithLangCode("https://example.com/global/file.html"));
        assertEquals("https://example.com/global/ja/file.html", h.locationWithLangCode("https://example.com/pics/../global/file.html"));
        assertEquals("https://example.com/global/../../file.html", h.locationWithLangCode("https://example.com/global/../../file.html"));
        assertEquals("https://example.com/tokyo/", h.locationWithLangCode("https://example.com/tokyo/"));
        assertEquals("https://example.com/tokyo/global/", h.locationWithLangCode("https://example.com/tokyo/global/"));
        assertEquals("https://example.com/ja/global/", h.locationWithLangCode("https://example.com/ja/global/"));
        assertEquals("https://example.com/th/global/", h.locationWithLangCode("https://example.com/th/global/"));
        assertEquals("https://example.com/th/", h.locationWithLangCode("https://example.com/th/"));
    }

    public void testIsValidPath() throws ConfigurationError {
        Headers h;
        h = makeHeaderWithSitePrefixPath("/", "global");
        assertEquals(false, h.getIsValidPath());

        h = makeHeaderWithSitePrefixPath("/global", "global");
        assertEquals(true, h.getIsValidPath());

        h = makeHeaderWithSitePrefixPath("/global/ja/foo", "global");
        assertEquals(true, h.getIsValidPath());

        h = makeHeaderWithSitePrefixPath("/ja/global/foo", "global");
        assertEquals(false, h.getIsValidPath());
    }

    private Headers makeHeaderWithSitePrefixPath(String requestPath, String sitePrefixPath) throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath(requestPath);
        HashMap<String, String> option = new HashMap<String, String>() {{
            put("sitePrefixPath", sitePrefixPath);
        }};
        Settings s = TestUtil.makeSettings(option);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        return new Headers(mockRequest, s, ulph);
    }

    public void testOriginalHeaders() throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestOriginalHeaders();
        FilterConfig mockConfig = mockConfigOriginalHeaders();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals("/foo/bar", h.pathName);
        assertEquals("?baz=123", h.query);
        assertEquals("example.com/foo/bar?baz=123", h.pageUrl);
    }
}
