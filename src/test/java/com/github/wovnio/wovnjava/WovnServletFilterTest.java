package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

public class WovnServletFilterTest extends TestCase {
    public void testHtml() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("path");
        String originalResponseBody = "<html>Original</html>";
        String expectedResponseBody = String.format("<html lang=\"en\"><head><script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=en&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script><link rel=\"alternate\" hreflang=\"en\" href=\"https://example.com/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://example.com/ja/\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>Original</body></html>", Settings.VERSION);

        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/", "/", settings, originalResponseBody);;

        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
        assertEquals("text/html; charset=utf-8", mock.response.getContentType());
        assertEquals("/", mock.request.getRequestURI());
    }

    public void testHtml__OverrideContentLengthTurnedOn__SetsContentLength() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("path", true);
        String originalResponseBody = "<html>Original</html>";
        String expectedResponseBody = String.format("<html lang=\"en\"><head><script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=en&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script><link rel=\"alternate\" hreflang=\"en\" href=\"https://example.com/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://example.com/ja/\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>Original</body></html>", Settings.VERSION);
        int expectedContentLength = expectedResponseBody.getBytes().length;

        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/", "/", settings, false, 200, originalResponseBody, expectedContentLength);

        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
        assertEquals("text/html; charset=utf-8", mock.response.getContentType());
        assertEquals("/", mock.request.getRequestURI());
    }

    public void testHtmlWithLang() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("path");
        String originalResponseBody = "<html>Original</html>";
        String expectedResponseBody = String.format("<html lang=\"en\"><head><script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=ja&amp;defaultLang=en&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script><link rel=\"alternate\" hreflang=\"en\" href=\"https://example.com/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://example.com/ja/\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>Original</body></html>", Settings.VERSION);

        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/ja/", "/", settings, originalResponseBody);;
        
        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
        assertEquals("text/html; charset=utf-8", mock.response.getContentType());
        assertEquals("https://example.com/", mock.request.getRequestURL().toString());
    }

    public void testHtmlWithQueryLang() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("query");
        String originalResponseBody = "<html>Original</html>";
        String expectedResponseBody = String.format("<html lang=\"en\"><head><script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=ja&amp;defaultLang=en&amp;urlPattern=query&amp;version=%s\" data-wovnio-type=\"fallback\" async></script><link rel=\"alternate\" hreflang=\"en\" href=\"https://example.com/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://example.com/?wovn=ja\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>Original</body></html>", Settings.VERSION);

        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/?wovn=ja", "/", settings, originalResponseBody);;
        
        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
        assertEquals("text/html; charset=utf-8", mock.response.getContentType());
        assertEquals("/", mock.request.getRequestURI());
    }

    public void testHtmlWithSubdomain() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("subdomain");
        String originalResponseBody = "<html>Original</html>";
        String expectedResponseBody = String.format("<html lang=\"en\"><head><script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=en&amp;urlPattern=subdomain&amp;version=%s\" data-wovnio-type=\"fallback\" async></script><link rel=\"alternate\" hreflang=\"en\" href=\"https://example.com/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://ja.example.com/\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>Original</body></html>", Settings.VERSION);

        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("text/html; charset=utf-8", "/", "/", settings, originalResponseBody);;
        
        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
        assertEquals("text/html; charset=utf-8", mock.response.getContentType());
        assertEquals("/", mock.request.getRequestURI());
    }

    public void testCss() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("path");
        String originalResponseBody = "body { color: red; }";
        String expectedResponseBody = originalResponseBody;

        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("text/css", "/dir/style.css", "/dir/style.css", settings, originalResponseBody);;
        
        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
        assertEquals("text/css", mock.response.getContentType());
        assertEquals("/dir/style.css", mock.request.getRequestURI());
    }

    public void testCssWithLang() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("path");
        String originalResponseBody = "body { color: red; }";
        String expectedResponseBody = originalResponseBody;
        
        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("text/css", "/ja/style.css", "/style.css", settings, originalResponseBody);;
        
        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
        assertEquals("text/css", mock.response.getContentType());
        assertEquals("https://example.com/style.css", mock.request.getRequestURL().toString());
    }

    public void testImage() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("path");
        String originalResponseBody = "image data";
        String expectedResponseBody = originalResponseBody;

        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("image/png", "/image.png", "/image.png", settings, originalResponseBody);;
        
        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
        assertEquals("image/png", mock.response.getContentType());
        assertEquals("/image.png", mock.request.getRequestURI());
    }

    public void testImageWithLang() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("path");
        String originalResponseBody = "image data";
        String expectedResponseBody = originalResponseBody;

        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("image/png", "/ja/image.png", "/image.png", settings, originalResponseBody);;
        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
        assertEquals("image/png", mock.response.getContentType());
        assertEquals("https://example.com/image.png", mock.request.getRequestURL().toString());
    }

    public void testProcessRequestOnce__RequestNotProcessed__ProcessRequest() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("path");
        String originalResponseBody = "<html>Original</html>";
        String expectedResponseBody = String.format("<html lang=\"en\"><head><script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=en&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script><link rel=\"alternate\" hreflang=\"en\" href=\"https://example.com/search/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://example.com/ja/search/\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>Original</body></html>", Settings.VERSION);

        boolean requestIsAlreadyProcessed = false;
        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("text/html", "/search/", "/search/", settings, requestIsAlreadyProcessed, 200, originalResponseBody);

        ServletResponse responseObjectPassedToFilterChain = mock.response;
        // If wovnjava is intercepting the request, the response object should be wrapped in a WovnHttpServletResponse
        assertEquals(true, responseObjectPassedToFilterChain instanceof HttpServletResponse);
        assertEquals(true, responseObjectPassedToFilterChain instanceof WovnHttpServletResponse);
        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
    }

    public void testProcessRequestOnce__RequestAlreadyProcessed__DoNotProcessRequestAgain() throws ServletException, IOException {
        HashMap<String, String> settings = createSettings("path");
        String originalResponseBody = "<html>Original</html>";
        String expectedResponseBody = originalResponseBody;
        
        boolean requestIsAlreadyProcessed = true;
        TestUtil.TestFilterResult mock = TestUtil.doServletFilter("text/html", "/search/", "/search/", settings, requestIsAlreadyProcessed, 200, originalResponseBody);

        ServletResponse responseObjectPassedToFilterChain = mock.response;
        // If wovnjava is ignoring the request, the response object should NOT be wrapped in a WovnHttpServletResponse
        assertEquals(true, responseObjectPassedToFilterChain instanceof HttpServletResponse);
        assertEquals(false, responseObjectPassedToFilterChain instanceof WovnHttpServletResponse);
        assertEquals(expectedResponseBody, mock.responseBuffer.toString());
    }

    private HashMap<String, String> createSettings(String urlPattern) {
        return createSettings(urlPattern, false);
    }

    private HashMap<String, String> createSettings(String urlPattern, boolean overrideContentLength) {
        return new HashMap<String, String>() {{
            put("urlPattern", urlPattern);
            put("overrideContentLength", Boolean.toString(overrideContentLength));
        }};
    }

}
