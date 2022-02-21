package com.github.wovnio.wovnjava;

import java.util.HashMap;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;

import java.net.URL;
import java.net.MalformedURLException;

import junit.framework.TestCase;

public class HeadersTest extends TestCase {
    private Lang japanese;

    protected void setUp() throws Exception {
        this.japanese = Lang.get("ja");
    }

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
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,zh-CHS");
        }};
        return TestUtil.makeConfigWithValidDefaults(parameters);
    }

    public void testHeaders() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/ja/test");
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertNotNull(h);
    }

    public void testGetRequestLangPath() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/ja/test");
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals(this.japanese, h.getRequestLang());
    }

    public void testGetRequestLangSubdomain() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://ja.example.com/test");
        FilterConfig mockConfig = mockConfigSubdomain();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals(this.japanese, h.getRequestLang());
    }

    public void testGetRequestLangQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/test?wovn=ja");
        FilterConfig mockConfig = mockConfigQuery();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        assertEquals(this.japanese, h.getRequestLang());
    }

    public void testConvertToDefaultLanguage__PathPattern() throws ConfigurationError, MalformedURLException {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/ja/test");
        FilterConfig mockConfig = mockConfigPath();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        URL url = new URL("http://example.com/ja/test");
        assertEquals("http://example.com/test", h.convertToDefaultLanguage(url).toString());
    }
    public void testConvertToDefaultLanguage__SubdomainPattern() throws ConfigurationError, MalformedURLException {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://ja.example.com/test");
        FilterConfig mockConfig = mockConfigSubdomain();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        URL url = new URL("http://ja.example.com/test");
        assertEquals("http://example.com/test", h.convertToDefaultLanguage(url).toString());
    }
    public void testConvertToDefaultLanguage__QueryPattern() throws ConfigurationError, MalformedURLException {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/test?wovn=ja");
        FilterConfig mockConfig = mockConfigQuery();

        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);

        URL url = new URL("http://example.com/test?wovn=ja");
        assertEquals("http://example.com/test", h.convertToDefaultLanguage(url).toString());
    }

    public void testConvertToDefaultLanguage__PathPatternWithSitePrefixPath() throws ConfigurationError, MalformedURLException {
        Headers h = createHeaders("/global/en/foo", "/global/", "");
        URL url;

        url = new URL("http://site.com/global/en/");
        assertEquals("http://site.com/global/", h.convertToDefaultLanguage(url).toString());

        url = new URL("http://site.com/en/global/");
        assertEquals("http://site.com/en/global/", h.convertToDefaultLanguage(url).toString());
    }

    public void testLocationWithDefaultLangCode() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/signin");
        FilterConfig mockConfig = mockConfigPath();
        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);
        assertEquals("http://example.com/", h.locationWithLangCode("http://example.com/"));
        assertEquals("https://example.com/", h.locationWithLangCode("https://example.com/"));
        assertEquals("https://example.com/dir/file", h.locationWithLangCode("https://example.com/dir/file"));
    }

    public void testLocationWithPath() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/ja/dir/signin");
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
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/ja/dir/signin/");
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
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/location.jsp?wovn=ja");
        FilterConfig mockConfig = mockConfigQuery();
        Settings s = new Settings(mockConfig);
        UrlLanguagePatternHandler ulph = UrlLanguagePatternHandlerFactory.create(s);
        Headers h = new Headers(mockRequest, s, ulph);
        assertEquals("https://example.com/index.jsp?wovn=ja", h.locationWithLangCode("./index.jsp"));
    }

    public void testLocationWithQuery() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com/dir/signin?wovn=ja");
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
        assertEquals("https://example.com/file?q=hello&wovn=ja", h.locationWithLangCode("../../file?q=hello&wovn=zh-CHS"));
        assertEquals("https://example.com/file?wovn=ja", h.locationWithLangCode("../../file?wovn=zh-CHS"));
    }

    public void testLocationWithSubdomain() throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://ja.example.com/dir/signin");
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
        Headers h = createHeaders("/global/ja/foo", "/global/", "");
        assertEquals("http://example.com/", h.locationWithLangCode("http://example.com/"));
        assertEquals("http://example.com/global/ja/", h.locationWithLangCode("http://example.com/global/"));
        assertEquals("https://example.com/global/ja/", h.locationWithLangCode("https://example.com/global/"));
        assertEquals("https://example.com/global/ja/", h.locationWithLangCode("https://example.com/global/ja/"));
        assertEquals("https://example.com/global/ja/th/", h.locationWithLangCode("https://example.com/global/th/")); // `th` not in supportedLangs
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
        h = createHeaders("/", "global", "");
        assertEquals(false, h.getIsValidRequest());

        h = createHeaders("/global", "global", "");
        assertEquals(true, h.getIsValidRequest());

        h = createHeaders("/global/ja/foo", "global", "");
        assertEquals(true, h.getIsValidRequest());

        h = createHeaders("/ja/global/foo", "global", "");
        assertEquals(false, h.getIsValidRequest());
    }

    public void testGetIsValidRequest__withIgnoredPaths() throws ConfigurationError {
        Headers h;

        h = createHeaders("/", "", "/admin,/wp-admin");
        assertEquals(true, h.getIsValidRequest());

        h = createHeaders("/user/admin", "", "/admin,/wp-admin");
        assertEquals(true, h.getIsValidRequest());

        h = createHeaders("/adminpage", "", "/admin,/wp-admin");
        assertEquals(true, h.getIsValidRequest());

        h = createHeaders("/admin", "", "/admin,/wp-admin");
        assertEquals(false, h.getIsValidRequest());

        h = createHeaders("/wp-admin/", "", "/admin,/wp-admin");
        assertEquals(false, h.getIsValidRequest());

        h = createHeaders("/wp-admin/page", "", "/admin,/wp-admin");
        assertEquals(false, h.getIsValidRequest());

        h = createHeaders("/ja/admin", "", "/admin,/wp-admin");
        assertEquals(false, h.getIsValidRequest());

        h = createHeaders("/ja/wp-admin/", "", "/admin,/wp-admin");
        assertEquals(false, h.getIsValidRequest());

        h = createHeaders("/en/admin", "", "/admin,/wp-admin");
        assertEquals(false, h.getIsValidRequest());

        h = createHeaders("/en/wp-admin/", "", "/admin,/wp-admin");
        assertEquals(false, h.getIsValidRequest());


        h = createHeaders("/city/wp-admin", "city", "/admin,/wp-admin");
        assertEquals(true, h.getIsValidRequest());

        h = createHeaders("/city/wp-admin", "city", "/city/admin,/city/wp-admin");
        assertEquals(false, h.getIsValidRequest());
    }

    private Headers createHeaders(String requestPath, String sitePrefixPath, String ignorePaths) throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create("https://example.com" + requestPath);
        HashMap<String, String> option = new HashMap<String, String>() {{
            put("urlPattern", "path");
            put("sitePrefixPath", sitePrefixPath);
            put("ignorePaths", ignorePaths);
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
        HttpServletRequest request = MockHttpServletRequest.create("https://example.com/home?user=123");
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
        HttpServletRequest request = MockHttpServletRequest.create("https://example.com/home?user=123");
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
        HttpServletRequest request = MockHttpServletRequest.create("https://example.com/home?user=123");
        Headers sut = new Headers(request, settings, patternHandler);

		HashMap<String, String> hreflangs = sut.getHreflangUrlMap();

		assertEquals(5, hreflangs.size());
		assertEquals("https://example.com/home?user=123", hreflangs.get("ja"));
		assertEquals("https://ko.example.com/home?user=123", hreflangs.get("ko"));
		assertEquals("https://th.example.com/home?user=123", hreflangs.get("th"));
		assertEquals("https://zh-CHT.example.com/home?user=123", hreflangs.get("zh-Hant"));
		assertEquals("https://zh-CHS.example.com/home?user=123", hreflangs.get("zh-Hans"));
    }

    public void testIsSearchEngineBot_NoUserAgent_False() throws ConfigurationError {
        String userAgent = null;

        Settings settings = TestUtil.makeSettings();
        UrlLanguagePatternHandler patternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        HttpServletRequest request = MockHttpServletRequest.create("https://example.com/home?user=123", userAgent);

        Headers sut = new Headers(request, settings, patternHandler);

        assertEquals(false, sut.isSearchEngineBot());
    }

    public void testIsSearchEngineBot_OrdinaryUserAgent_False() throws ConfigurationError {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.81 Safari/537.36";

        Settings settings = TestUtil.makeSettings();
        UrlLanguagePatternHandler patternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        HttpServletRequest request = MockHttpServletRequest.create("https://example.com/home?user=123");

        Headers sut = new Headers(request, settings, patternHandler);

        assertEquals(false, sut.isSearchEngineBot());
    }

    public void testIsSearchEngineBot_SearchEngineBotUserAgent_True() throws ConfigurationError {
        String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";

        Settings settings = TestUtil.makeSettings();
        UrlLanguagePatternHandler patternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        HttpServletRequest request = MockHttpServletRequest.create("https://example.com/home?user=123", userAgent);

        Headers sut = new Headers(request, settings, patternHandler);

        assertEquals(true, sut.isSearchEngineBot());
    }
}
