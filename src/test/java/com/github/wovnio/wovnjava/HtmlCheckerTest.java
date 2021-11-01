package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import junit.framework.TestCase;

public class HtmlCheckerTest extends TestCase {
    private final HtmlChecker htmlChecker = new HtmlChecker();

    public void testCanTranslateContentType() {
        assertEquals(true, htmlChecker.canTranslate(getMockResponse(200, null), "<html"));
        assertEquals(true, htmlChecker.canTranslate(getMockResponse(200, "html"), "<html"));
        assertEquals(true, htmlChecker.canTranslate(getMockResponse(200, "text/html"), "<html"));
        assertEquals(true, htmlChecker.canTranslate(getMockResponse(200, "text/xhtml"), "<html"));
        assertEquals(false, htmlChecker.canTranslate(getMockResponse(200, "text/plain"), "<html"));
    }

    public void testIsTextFileContentType() {
        assertEquals(true, htmlChecker.isTextFileContentType(null));
        assertEquals(true, htmlChecker.isTextFileContentType("html"));
        assertEquals(true, htmlChecker.isTextFileContentType("text/html"));
        assertEquals(true, htmlChecker.isTextFileContentType("text/xhtml"));
        assertEquals(true, htmlChecker.isTextFileContentType("text/plain"));
        assertEquals(true, htmlChecker.isTextFileContentType("text/css"));
        assertEquals(true, htmlChecker.isTextFileContentType("text/javascript"));
    }

    public void testCanTranslate() {
        assertEquals(false, htmlChecker.canTranslate(getMockResponse(200, "text/html"), null));
        assertEquals(false, htmlChecker.canTranslate(getMockResponse(200, "text/html"), ""));
        assertEquals(false, htmlChecker.canTranslate(getMockResponse(200, "text/html"), "hello world"));

        assertCanTranslate(false, "<!doctype html><html ⚡>");
        assertCanTranslate(false, "<!doctype html><html amp>");
        assertCanTranslate(false, "<!doctype html><html\namp>");
        assertCanTranslate(false, "<!doctype html><html\ramp>");
        assertCanTranslate(false, "<!doctype html><html\tamp>");
        assertCanTranslate(false, "<!doctype html><html\r\t\namp>");
        assertCanTranslate(false, "<!doctype html><html amp=amp>");
        assertCanTranslate(false, "<!doctype html><html amp=1>");
        assertCanTranslate(false, "<!doctype html><html amp=\"\">");
        assertCanTranslate(false, "<!doctype html><html amp=''>");
        assertCanTranslate(false, "<!doctype html><html ⚡=amp>");
        assertCanTranslate(false, "<!doctype html><html ⚡=1>");
        assertCanTranslate(false, "<!doctype html><html ⚡=\"\">");
        assertCanTranslate(false, "<!doctype html><html ⚡=''>");
        assertCanTranslate(false, "<!doctype html><html lang=\"en\" amp>");
        assertCanTranslate(false, "<!doctype html><html lang='en' amp>");
        assertCanTranslate(false, "<!doctype html><html lang=en amp>");
        assertCanTranslate(false, "<!doctype html><html amp lang=\"en\">");
        assertCanTranslate(false, "<!doctype html><html onload=\"console.log(1 > 2 && 3 < 4)\" amp lang=en>");
        assertCanTranslate(true, "<!doctype html><html ampute>");
        assertCanTranslate(true, "<!doctype html><html lamp>");
        assertCanTranslate(true, "<!doctype html><html lang=amp>");
        assertCanTranslate(true, "<!doctype html><html lang = amp>");
        assertCanTranslate(true, "<!doctype html>");
        assertCanTranslate(true, "<!DOCTYPE html>");
        assertCanTranslate(true, "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
        assertCanTranslate(true, "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        assertCanTranslate(true, "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">");
        assertCanTranslate(true, "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        assertCanTranslate(true, "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        assertCanTranslate(true, "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        assertCanTranslate(true, "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">");
    }

    public void testCanTranslateStatusCode() {
        assertEquals(false, htmlChecker.canTranslate(getMockResponse(102, "html"), "<html"));
        assertEquals(false, htmlChecker.canTranslate(getMockResponse(199, "html"), "<html"));
        assertEquals(false, htmlChecker.canTranslate(getMockResponse(300, "html"), "<html"));
        assertEquals(false, htmlChecker.canTranslate(getMockResponse(399, "html"), "<html"));

        assertEquals(true, htmlChecker.canTranslate(getMockResponse(200, "html"), "<html"));
        assertEquals(true, htmlChecker.canTranslate(getMockResponse(299, "html"), "<html"));
        assertEquals(true, htmlChecker.canTranslate(getMockResponse(400, "html"), "<html"));
        assertEquals(true, htmlChecker.canTranslate(getMockResponse(404, "html"), "<html"));
    }

    private void assertCanTranslate(boolean expect, String prefix) {
        String template = "<head> <meta charset=\"utf-8\"></head><body>hello</body></html>";
        assertEquals(expect, htmlChecker.canTranslate(getMockResponse(200, "text/html"), prefix + template));
        assertEquals(expect, htmlChecker.canTranslate(getMockResponse(200, "text/html"), "  " + prefix + template));
        assertEquals(expect, htmlChecker.canTranslate(getMockResponse(200, "text/html"), "\n" + prefix + template));
        assertEquals(expect, htmlChecker.canTranslate(getMockResponse(200, "text/html"), "<!-- comment -->" + prefix + template));
        assertEquals(expect, htmlChecker.canTranslate(getMockResponse(200, "text/html"), "<!-- comment -->\n " + prefix + template));
    }

    private HttpServletResponse getMockResponse(int statusCode, String contentType) {
        HttpServletResponse mock = EasyMock.mock(HttpServletResponse.class);
        EasyMock.expect(mock.getStatus()).andReturn(statusCode);
        EasyMock.expect(mock.getContentType()).andReturn(contentType);
        EasyMock.replay(mock);
        return mock;
    }
}
