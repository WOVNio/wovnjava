package com.github.wovnio.wovnjava;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class SubdomainUrlLanguagePatternHandlerTest extends TestCase {
    public void testGetLang__NonMatchingSubdomain__ReturnEmptyLang() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
        assertEquals("", sut.getLang("/"));
        assertEquals("", sut.getLang("/en"));
        assertEquals("", sut.getLang("/en/page"));
        assertEquals("", sut.getLang("site.com/page/index.html"));
        assertEquals("", sut.getLang("site.com/en/pre/fix/index.html"));
        assertEquals("", sut.getLang("/page?language=en&wovn=fr"));
        assertEquals("", sut.getLang("deutsch.site.com/page"));
    }

    public void testGetLang__MatchingSubdomain__ReturnLangCode() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
		assertEquals("en", sut.getLang("en.site.com"));
		assertEquals("es", sut.getLang("es.site.com/"));
		assertEquals("fr", sut.getLang("fr.site.com/en/page/index.html?lang=it&wovn=en"));
	}

    public void testRemoveLang__NonMatchingSubdomain__DoNotModify() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
		assertEquals("/", sut.removeLang("/", "en"));
        assertEquals("site.com", sut.removeLang("site.com", "en"));
		assertEquals("ja.site.com", sut.removeLang("ja.site.com", "fr"));
		assertEquals("ja.fr.site.com", sut.removeLang("ja.fr.site.com", "fr"));
		assertEquals("site.com/fr/index.html?wovn=fr", sut.removeLang("site.com/fr/index.html?wovn=fr", "fr"));
    }

    public void testRemoveLang__MatchingSubdomain__RemoveLangCode() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
		assertEquals("site.com", sut.removeLang("en.site.com", "en"));
		assertEquals("site.com/", sut.removeLang("es.site.com/", "es"));
		assertEquals("site.com/fr/index.html?lang=fr&wovn=fr", sut.removeLang("fr.site.com/fr/index.html?lang=fr&wovn=fr", "fr"));
	}
}
