package com.github.wovnio.wovnjava;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;

public class RequestOptionsTest extends TestCase {
    public void testDisableModeNoQueryParameter() throws ConfigurationError {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn(null);
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(defaultSettings(), request);
        assertEquals(false, sut.getDisableMode());
    }

    public void testDisableModeNonmatchingQueryParameter() throws ConfigurationError {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("pen&pineapple&apple&pen");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(defaultSettings(), request);
        assertEquals(false, sut.getDisableMode());
    }

    public void testDisableModeMatchingQueryParameter() throws ConfigurationError {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("pen&wovnDisable&pen");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(defaultSettings(), request);
        assertEquals(true, sut.getDisableMode());
    }

    public void testNoQueryParameter() throws ConfigurationError {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn(null);
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(debugModeSettings(), request);
        assertEquals(false, sut.getCacheDisableMode());
        assertEquals(false, sut.getDebugMode());
    }

    public void testCacheDisable() throws ConfigurationError {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("user=Ceb&wovnCacheDisable");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(debugModeSettings(), request);
        assertEquals(true, sut.getCacheDisableMode());
        assertEquals(false, sut.getDebugMode());
    }

    public void testDebugMode() throws ConfigurationError {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("wovnDebugMode&user=Ceb");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(debugModeSettings(), request);
        assertEquals(false, sut.getCacheDisableMode());
        assertEquals(true, sut.getDebugMode());
    }

    public void testQueryParameteresWithoutDebugModeSettings() throws ConfigurationError {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("wovnDebugMode&wovnDisableCache");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(defaultSettings(), request);
        assertEquals(false, sut.getCacheDisableMode());
        assertEquals(false, sut.getDebugMode());
    }

    private Settings debugModeSettings() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("debugMode", "true");
        }});
        return settings;
    }

    private Settings defaultSettings() throws ConfigurationError {
        return TestUtil.makeSettings();
    }
}
