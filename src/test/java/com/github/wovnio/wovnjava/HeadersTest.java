package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.HashMap;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

public class HeadersTest extends TestCase {

    private static FilterConfig mockConfigPath() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("urlPattern", "path");
        }};
        return TestUtil.makeConfigWithValidDefaults(parameters);
    }
    private static FilterConfig mockConfigSubdomain() {
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

    private static FilterConfig mockConfigOriginalHeaders() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("originalUrlHeader", "REDIRECT_URL");
            put("originalQueryStringHeader", "REDIRECT_QUERY_STRING");
        }};
        return TestUtil.makeConfigWithValidDefaults(parameters);
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

    public void testGetIsValidRequest() throws ConfigurationError {
        Headers h;
        h = makeHeaderWithSitePrefixPath("/", "global");
        assertEquals(false, h.getIsValidRequest());

        h = makeHeaderWithSitePrefixPath("/global", "global");
        assertEquals(true, h.getIsValidRequest());

        h = makeHeaderWithSitePrefixPath("/global/ja/foo", "global");
        assertEquals(true, h.getIsValidRequest());

        h = makeHeaderWithSitePrefixPath("/ja/global/foo", "global");
        assertEquals(false, h.getIsValidRequest());
    }

    private Headers makeHeaderWithSitePrefixPath(String requestPath, String sitePrefixPath) throws ConfigurationError {
        HttpServletRequest mockRequest = mockRequestPath(requestPath);
        HashMap<String, String> option = new HashMap<String, String>() {{
            put("urlPattern", "path");
            put("sitePrefixPath", sitePrefixPath);
        }};
        Settings s = TestUtil.makeSettings(option);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        return new Headers(mockRequest, s, ulph);
    }

    public void testGetHreflangUrlMap__PathPattern() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
            put("urlPattern", "path");
            put("sitePrefixPath", "/home");
        }});
        UrlLanguagePatternHandler patternHandler = UrlLanguagePatternHandlerFactory.create(settings);
		HttpServletRequest request = TestUtil.mockRequestPath("/home?user=123");
        Headers sut = new Headers(request, settings, patternHandler);

		HashMap<String, String> hreflangs = sut.getHreflangUrlMap();

		assertEquals(3, hreflangs.size());
		assertEquals("https://example.com/home?user=123", hreflangs.get("en"));
		assertEquals("https://example.com/home/ja?user=123", hreflangs.get("ja"));
		assertEquals("https://example.com/home/fr?user=123", hreflangs.get("fr"));
    }

    public void testGetHreflangUrlMap__QueryPattern() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ko");
            put("urlPattern", "query");
        }});
        UrlLanguagePatternHandler patternHandler = UrlLanguagePatternHandlerFactory.create(settings);
		HttpServletRequest request = TestUtil.mockRequestPath("/home?user=123");
        Headers sut = new Headers(request, settings, patternHandler);

		HashMap<String, String> hreflangs = sut.getHreflangUrlMap();

		assertEquals(2, hreflangs.size());
		assertEquals("https://example.com/home?user=123", hreflangs.get("ja"));
		assertEquals("https://example.com/home?user=123&wovn=ko", hreflangs.get("ko"));
    }

    public void testGetHreflangUrlMap__SubdomainPattern__WithChineseSupportedLangs() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ko,th, zh-CHT, zh-CHS");
            put("urlPattern", "subdomain");
        }});
        UrlLanguagePatternHandler patternHandler = UrlLanguagePatternHandlerFactory.create(settings);
		HttpServletRequest request = TestUtil.mockRequestPath("/home?user=123");
        Headers sut = new Headers(request, settings, patternHandler);

		HashMap<String, String> hreflangs = sut.getHreflangUrlMap();

		assertEquals(5, hreflangs.size());
		assertEquals("https://example.com/home?user=123", hreflangs.get("ja"));
		assertEquals("https://ko.example.com/home?user=123", hreflangs.get("ko"));
		assertEquals("https://th.example.com/home?user=123", hreflangs.get("th"));
		assertEquals("https://zh-CHT.example.com/home?user=123", hreflangs.get("zh-Hant"));
		assertEquals("https://zh-CHS.example.com/home?user=123", hreflangs.get("zh-Hans"));
    }
}
