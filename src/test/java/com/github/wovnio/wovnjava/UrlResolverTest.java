package com.github.wovnio.wovnjava;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;

public class UrlResolverTest extends TestCase {
    public void testComputeClientRequestUrl__defaultSettings__requestNotForwarded__returnUrl() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("http").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(443).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/page/index.html").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn("user=Elvis").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings();

        assertEquals("http://site.com/page/index.html?user=Elvis", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__defaultSettings__requestNotForwarded__nonDefaultPort__NoQuery__returnUrl() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(8080).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn(null).times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings();

        assertEquals("https://site.com:8080/", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__defaultSettings__requestForwardedInternally__returnOriginalUrl() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/internal/find.html").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn("user=Elvis").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("/search").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("user=Elvis&q=korean+food").times(0,1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings();

        assertEquals("https://site.com/search?user=Elvis&q=korean+food", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__useProxy__proxyHeadersNotSet__returnUrlAsNormal() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/en/tokyo").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn(null).times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.expect(r.getHeader("X-Forwarded-Host")).andReturn(null).times(0,1);
        EasyMock.expect(r.getHeader("X-Forwarded-Port")).andReturn(null).times(0,1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("useProxy", "true");
        }});

        assertEquals("https://site.com/en/tokyo", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__useProxy__proxyHeadersPresent__returnUrlByHttpHeaders() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/en/tokyo").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn(null).times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn(null).times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn(null).times(0,1);
        EasyMock.expect(r.getHeader("X-Forwarded-Host")).andReturn("global.co.jp").times(0,1);
        EasyMock.expect(r.getHeader("X-Forwarded-Port")).andReturn("777").times(0,1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("useProxy", "true");
        }});

        assertEquals("https://global.co.jp:777/en/tokyo", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__customHeaderPathAndQuery__customHeadersNotSet__returnUrlAsNormal() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/internal/forward/index.html").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn("q=internal").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("/en/global/").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("q=original").times(0,1);
        EasyMock.expect(r.getHeader("X-My-Url")).andReturn(null).times(0,1);
        EasyMock.expect(r.getHeader("X-My-Query")).andReturn(null).times(0,1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("originalUrlHeader", "X-My-Url");
            put("originalQueryStringHeader", "X-My-Query");
        }});

        assertEquals("https://site.com/en/global/?q=original", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__customHeaderPathAndQuery__customHeadersSet__returnPathAndQueryFromHeaders() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/internal/forward/index.html").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn("q=internal").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("/en/tokyo").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("q=original").times(0,1);
        EasyMock.expect(r.getHeader("X-My-Url")).andReturn("/global/").times(0,1);
        EasyMock.expect(r.getHeader("X-My-Query")).andReturn("wovn=vi").times(0,1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("originalUrlHeader", "X-My-Url");
            put("originalQueryStringHeader", "X-My-Query");
        }});

        assertEquals("https://site.com/global/?wovn=vi", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testClientRequestHostAndPort__override_port_and_host() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/en/tokyo").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn("q=original").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("/en/tokyo").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("q=original").times(0,1);
        EasyMock.replay(r);

        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("fixedHost", "example.com");
            put("fixedPort", "1234");
        }});

        assertEquals("https://example.com:1234/en/tokyo?q=original", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__override_port_and_host__standard_http__no_port_number_inserted() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/en/tokyo").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn("q=original").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("/en/tokyo").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("q=original").times(0,1);
        EasyMock.replay(r);
        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("fixedHost", "example.com");
            put("fixedPort", "80");
            put("fixedScheme", "http");
        }});

        assertEquals("http://example.com/en/tokyo?q=original", UrlResolver.computeClientRequestUrl(r, s));
    }

    public void testComputeClientRequestUrl__override_scheme() throws ConfigurationError {
        HttpServletRequest r = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(r.getScheme()).andReturn("https").times(0,1);
        EasyMock.expect(r.getServerName()).andReturn("site.com").times(0,1);
        EasyMock.expect(r.getServerPort()).andReturn(80).times(0,1);
        EasyMock.expect(r.getRequestURI()).andReturn("/en/tokyo").times(0,1);
        EasyMock.expect(r.getQueryString()).andReturn("q=original").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.request_uri")).andReturn("/en/tokyo").times(0,1);
        EasyMock.expect(r.getAttribute("javax.servlet.forward.query_string")).andReturn("q=original").times(0,1);
        EasyMock.replay(r);
        Settings s = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("fixedScheme", "http");
        }});

        assertEquals("http://site.com/en/tokyo?q=original", UrlResolver.computeClientRequestUrl(r, s));
    }
}
