package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.util.HashMap;
import java.lang.IllegalArgumentException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;


public class InterceptorTest extends TestCase {
    final String version = Settings.VERSION;

    public void testApiTranslate() throws NoSuchMethodException, IllegalAccessException, IOException, ServletException, ConfigurationError {
        String originalHtml = "<!doctype html><html><head><title>test</title></head><body>test</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("projectToken", "token0");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
        }});
        String html = translate("/ja/", originalHtml, settings, mockApiSuccess(), mockResponseHeadersSuccess());
        String expect = "replaced html";
        assertEquals(expect, stripExtraSpaces(html));
    }

    public void testApiTimeout() throws NoSuchMethodException, IllegalAccessException, IOException, ServletException, ConfigurationError {
        String originalHtml = "<!doctype html><html><head><meta http-equiv=\"CONTENT-TYPE\"><title>test</title></head><body>test</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("projectToken", "token0");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
        }});
        String html = translate("/ja/", originalHtml, settings, mockApiTimeout(), mockResponseHeadersTimeout());
        String expect = "<!doctype html><html><head><title>test</title>" +
                        "<script src=\"//j.wovn.io/1\" data-wovnio=\"key=token0&amp;backend=true&amp;currentLang=ja&amp;defaultLang=en&amp;urlPattern=path&amp;langCodeAliases={}&amp;version=" + version + "\" data-wovnio-type=\"fallback\" async></script>" +
                        "<link ref=\"alternate\" hreflang=\"en\" href=\"https://example.com/\">" +
                        "<link ref=\"alternate\" hreflang=\"ja\" href=\"https://example.com/ja/\">" +
                        "<link ref=\"alternate\" hreflang=\"fr\" href=\"https://example.com/fr/\">" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                        "</head><body>test</body></html>";
        assertEquals(expect, stripExtraSpaces(html));
    }

    public void testNoApi() throws NoSuchMethodException, IllegalAccessException, IOException, ServletException, ConfigurationError {
        String originalHtml = "<!doctype html><html><head><meta http-equiv=\"CONTENT-TYPE\"><title>test</title></head><body>test</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("projectToken", "token0");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
        }});
        String html = translate("/", originalHtml, settings, null, null);
        String expect = "<!doctype html><html><head><title>test</title>" +
                        "<script src=\"//j.wovn.io/1\" data-wovnio=\"key=token0&amp;backend=true&amp;currentLang=en&amp;defaultLang=en&amp;urlPattern=path&amp;langCodeAliases={}&amp;version=" + version + "\" data-wovnio-type=\"fallback\" async></script>" +
                        "<link ref=\"alternate\" hreflang=\"en\" href=\"https://example.com/\">" +
                        "<link ref=\"alternate\" hreflang=\"ja\" href=\"https://example.com/ja/\">" +
                        "<link ref=\"alternate\" hreflang=\"fr\" href=\"https://example.com/fr/\">" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                        "</head><body>test</body></html>";
        assertEquals(expect, stripExtraSpaces(html));
    }

    private String translate(String path, String html, Settings settings, Api api, ResponseHeaders responseHeaders) throws NoSuchMethodException, IllegalAccessException, IOException, ServletException, ConfigurationError {
        HttpServletRequest request = mockRequestPath(path);
        UrlLangPatternHandler urlLangPatternHandler = UrlLangPatternHandlerFactory.create(settings);
        Interceptor interceptor = new Interceptor(new Headers(request, settings, urlLangPatternHandler), settings, api, responseHeaders);
        return interceptor.translate(html);
    }

    private Api mockApiSuccess() {
        Api mock = EasyMock.createMock(Api.class);
        try {
            EasyMock.expect(mock.translate(EasyMock.anyString(), EasyMock.anyString())).andReturn("replaced html").atLeastOnce();
        } catch (ApiException _) {
            throw new RuntimeException("Fail create mock");
        }
        EasyMock.replay(mock);
        return mock;
    }

    private Api mockApiTimeout() {
        Api mock = EasyMock.createMock(Api.class);
        try {
            EasyMock.expect(mock.translate(EasyMock.anyString(), EasyMock.anyString())).andThrow(new ApiException("SocketTimeoutException", "")).atLeastOnce();
        } catch (ApiException _) {
            throw new RuntimeException("Fail create mock");
        }
        EasyMock.replay(mock);
        return mock;
    }

    private HttpServletRequest mockRequestPath(String path) {
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

    private String stripExtraSpaces(String html) {
        return html.replaceAll("\\s +", "").replaceAll(">\\s+<", "><");
    }

    private ResponseHeaders mockResponseHeadersSuccess() {
        ResponseHeaders mock = EasyMock.createMock(ResponseHeaders.class);
        mock.setApiStatus("Success");
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(mock);
        return mock;
    }

    private ResponseHeaders mockResponseHeadersTimeout() {
        ResponseHeaders mock = EasyMock.createMock(ResponseHeaders.class);
        mock.setApiStatus("SocketTimeoutException");
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(mock);
        return mock;
    }
}
