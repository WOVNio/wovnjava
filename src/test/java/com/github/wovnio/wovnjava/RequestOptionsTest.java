package com.github.wovnio.wovnjava;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;

public class RequestOptionsTest extends TestCase {
    public void testDisableModeNoQueryParameter() {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn(null);
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(defaultSettings(), request);
        assertEquals(false, sut.getDisableMode());
    }

    public void testDisableModeNonmatchingQueryParameter() {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("pen&pineapple&apple&pen");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(defaultSettings(), request);
        assertEquals(false, sut.getDisableMode());
    }

    public void testDisableModeMatchingQueryParameter() {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("pen&wovnDisable&pen");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(defaultSettings(), request);
        assertEquals(true, sut.getDisableMode());
    }

    public void testNoQueryParameter() {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn(null);
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(debugModeSettings(), request);
        assertEquals(false, sut.getCacheDisableMode());
        assertEquals(false, sut.getDebugMode());
    }

    public void testCacheDisable() {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("user=Ceb&wovnCacheDisable");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(debugModeSettings(), request);
        assertEquals(true, sut.getCacheDisableMode());
        assertEquals(false, sut.getDebugMode());
    }

    public void testDebugMode() {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("wovnDebugMode&user=Ceb");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(debugModeSettings(), request);
        assertEquals(false, sut.getCacheDisableMode());
        assertEquals(true, sut.getDebugMode());
    }

    public void testQueryParameteresWithoutDebugModeSettings() {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getQueryString()).andReturn("wovnDebugMode&wovnDisableCache");
        EasyMock.replay(request);

        RequestOptions sut = new RequestOptions(defaultSettings(), request);
        assertEquals(false, sut.getCacheDisableMode());
        assertEquals(false, sut.getDebugMode());
    }

    private Settings debugModeSettings() {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("debugMode", "true");
        }});
        return settings;
    }

    private Settings defaultSettings() {
        return TestUtil.makeSettings();
    }
}
