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

    public void testApiTranslate() throws NoSuchMethodException, IllegalAccessException, IOException, ServletException {
        String originalHtml = "<!doctype html><html><head><title>test</title></head><body>test</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("projectToken", "token0");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
        }});
        String html = translate("/ja/", originalHtml, settings, mockApi("replace"));
        String expect = "replaced html";
        assertEquals(expect, stripExtraSpaces(html));
    }

    public void testApiTimeout() throws NoSuchMethodException, IllegalAccessException, IOException, ServletException {
        String originalHtml = "<!doctype html><html><head><meta http-equiv=\"CONTENT-TYPE\"><title>test</title></head><body><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">test</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("projectToken", "token0");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
        }});
        String html = translate("/ja/", originalHtml, settings, mockApi("timeout"));
        String expect = "<!doctype html><html><head><title>test</title>" +
                        "<script src=\"//j.wovn.io/1\" data-wovnio=\"key=token0&amp;backend=true&amp;currentLang=ja&amp;defaultLang=en&amp;urlPattern=path&amp;langCodeAliases={}&amp;version=" + version + "\" data-wovnio-type=\"timeout\" async></script>" +
                        "<link ref=\"alternate\" hreflang=\"en\" href=\"https://example.com/\">" +
                        "<link ref=\"alternate\" hreflang=\"ja\" href=\"https://example.com/ja/\">" +
                        "<link ref=\"alternate\" hreflang=\"fr\" href=\"https://example.com/fr/\">" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                        "</head><body>test</body></html>";
        assertEquals(expect, stripExtraSpaces(html));
    }

    public void testNoApi() throws NoSuchMethodException, IllegalAccessException, IOException, ServletException {
        String originalHtml = "<!doctype html><html><head><meta http-equiv=\"CONTENT-TYPE\"><title>test</title></head><body>test</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("projectToken", "token0");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
        }});
        String html = translate("/", originalHtml, settings, null);
        String expect = "<!doctype html><html><head><title>test</title>" +
                        "<script src=\"//j.wovn.io/1\" data-wovnio=\"key=token0&amp;backend=true&amp;currentLang=en&amp;defaultLang=en&amp;urlPattern=path&amp;langCodeAliases={}&amp;version=" + version + "\" data-wovnio-type=\"backend_without_api\" async></script>" +
                        "<link ref=\"alternate\" hreflang=\"en\" href=\"https://example.com/\">" +
                        "<link ref=\"alternate\" hreflang=\"ja\" href=\"https://example.com/ja/\">" +
                        "<link ref=\"alternate\" hreflang=\"fr\" href=\"https://example.com/fr/\">" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                        "</head><body>test</body></html>";
        assertEquals(expect, stripExtraSpaces(html));
    }

    private String translate(String path, String html, Settings settings, Api api) throws NoSuchMethodException, IllegalAccessException, IOException, ServletException {
        HttpServletRequest request = mockRequestPath(path);
        Interceptor interceptor = new Interceptor(new Headers(request, settings), settings, api);
        return interceptor.translate(html);
    }

    private Api mockApi(String type) {
        Api mock = EasyMock.createMock(Api.class);
        try {
            if (type.equals("replace")) {
                EasyMock.expect(mock.translate(EasyMock.anyString(), EasyMock.anyString())).andReturn("replaced html").atLeastOnce();
            } else if (type.equals("timeout")) {
                EasyMock.expect(mock.translate(EasyMock.anyString(), EasyMock.anyString())).andThrow(ApiException.timeout).atLeastOnce();
            } else {
                throw new IllegalArgumentException("Unknown type " + type);
            }
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
}
