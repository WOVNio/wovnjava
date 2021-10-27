package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

public class HtmlCheckerTest extends TestCase {
    private final HtmlChecker htmlChecker = new HtmlChecker();

    public void testCanTranslateContentType() {
        assertEquals(true, htmlChecker.canTranslateContentType(null));
        assertEquals(true, htmlChecker.canTranslateContentType("html"));
        assertEquals(true, htmlChecker.canTranslateContentType("text/html"));
        assertEquals(true, htmlChecker.canTranslateContentType("text/xhtml"));
        assertEquals(false, htmlChecker.canTranslateContentType("text/plain"));
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
        assertEquals(false, htmlChecker.canTranslateContent(null));
        assertEquals(false, htmlChecker.canTranslateContent(""));
        assertEquals(false, htmlChecker.canTranslateContent("hello world"));
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
        assertEquals(false, htmlChecker.canTranslateStatusCode(102));
        assertEquals(false, htmlChecker.canTranslateStatusCode(199));
        assertEquals(false, htmlChecker.canTranslateStatusCode(300));
        assertEquals(false, htmlChecker.canTranslateStatusCode(399));
        assertEquals(true, htmlChecker.canTranslateStatusCode(200));
        assertEquals(true, htmlChecker.canTranslateStatusCode(299));
        assertEquals(true, htmlChecker.canTranslateStatusCode(400));
        assertEquals(true, htmlChecker.canTranslateStatusCode(404));
    }

    private void assertCanTranslate(boolean expect, String prefix) {
        String template = "<head> <meta charset=\"utf-8\"></head><body>hello</body></html>";
        assertEquals(expect, htmlChecker.canTranslateContent(prefix + template));
        assertEquals(expect, htmlChecker.canTranslateContent("  " + prefix + template));
        assertEquals(expect, htmlChecker.canTranslateContent("\n" + prefix + template));
        assertEquals(expect, htmlChecker.canTranslateContent("<!-- comment -->" + prefix + template));
        assertEquals(expect, htmlChecker.canTranslateContent("<!-- comment -->\n " + prefix + template));
    }
}
