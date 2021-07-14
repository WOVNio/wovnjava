package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class WovnHttpServletResponseTest extends TestCase {
    private WovnHttpServletResponse response;
    private Headers headersMock;

    private void setup() {
        this.headersMock = EasyMock.createMock(Headers.class);
        Utf8 unicodeConverter = new Utf8("Utf-8");
        this.response = new WovnHttpServletResponse(EasyMock.createMock(HttpServletResponse.class), headersMock, unicodeConverter);
    }

    public void testSetHeader() {
        this.setup();
        EasyMock.expect(this.headersMock.locationWithLangCode("/nice.html")).andReturn("ja/nice.html").once();
        EasyMock.replay(this.headersMock);
        this.response.setHeader("Location", "/nice.html");
    }

    public void testSetHeader__should_ignore_case() {
        this.setup();
        EasyMock.expect(this.headersMock.locationWithLangCode("/nice.html")).andReturn("ja/nice.html").once();
        EasyMock.replay(this.headersMock);
        this.response.setHeader("Location", "/nice.html");
    }

    public void testSetHeader__should_not_match_non_location() {
        this.setup();
        EasyMock.replay(this.headersMock);
        this.response.setHeader("Lotion", "/nice.html");
    }
}
