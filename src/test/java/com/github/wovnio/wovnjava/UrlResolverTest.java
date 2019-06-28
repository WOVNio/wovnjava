package com.github.wovnio.wovnjava;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;

public class UrlResolverTest extends TestCase {
    public void testComputeClientRequestUrl__defaultSettings__requestNotForwarded__returnUrl() {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("http").times(1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(1);
        EasyMock.expect(r.getServerPort()).andReturn(443).times(1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("").times(1);
        EasyMock.expect(r.getRequestURI()).andReturn("/page/index.html").times(1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("").times(1);
        EasyMock.expect(r.getQueryString()).andReturn("user=Elvis").times(1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings();

        assertEquals("http://site.com/page/index.html?user=Elvis", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__defaultSettings__requestNotForwarded__nonDefaultPort__NoQuery__returnUrl() {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(1);
        EasyMock.expect(r.getServerPort()).andReturn(8080).times(1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("").times(1);
        EasyMock.expect(r.getRequestURI()).andReturn("/").times(1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("").times(1);
        EasyMock.expect(r.getQueryString()).andReturn(null).times(1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings();

        assertEquals("https://site.com:8080/", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__defaultSettings__requestForwardedInternally__returnOriginalUrl() {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("/search").times(1);
        EasyMock.expect(r.getRequestURI()).andReturn("/internal/search/korean+food/index.html").times(1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("user=Elvis&q=korean+food").times(1);
        EasyMock.expect(r.getQueryString()).andReturn("user=Elvis").times(1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings();

        assertEquals("https://site.com/search?user=Elvis&q=korean+food", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__useProxy__returnUrlByHttpHeaders() {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(1);
        EasyMock.expect(r.getHeader("X-Forwarded-Host")).andReturn("global.co.jp").times(1);
        EasyMock.expect(r.getHeader("X-Forwarded-Port")).andReturn("777").times(1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("").times(1);
        EasyMock.expect(r.getRequestURI()).andReturn("/en/tokyo").times(1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("").times(1);
        EasyMock.expect(r.getQueryString()).andReturn(null).times(1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("useProxy", "true");
        }});

        assertEquals("https://global.co.jp:777/en/tokyo", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__customHeaderPathAndQuery__customHeadersNotSet__returnEmptyPathAndQuery() {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(1);
        EasyMock.expect(r.getHeader("X-My-Url")).andReturn(null).times(1);
        EasyMock.expect(r.getHeader("X-My-Query")).andReturn(null).times(1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("originalUrlHeader", "X-My-Url");
            put("originalQueryStringHeader", "X-My-Query");
        }});

        assertEquals("https://site.com", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__customHeaderPathAndQuery__customHeadersSet__returnPathAndQueryFromHeaders() {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(1);
        EasyMock.expect(r.getHeader("X-My-Url")).andReturn("/global/").times(1);
        EasyMock.expect(r.getHeader("X-My-Query")).andReturn("wovn=vi").times(1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("originalUrlHeader", "X-My-Url");
            put("originalQueryStringHeader", "X-My-Query");
        }});

        assertEquals("https://site.com/global/?wovn=vi", UrlResolver.computeClientRequestUrl(r, s));
    }
}