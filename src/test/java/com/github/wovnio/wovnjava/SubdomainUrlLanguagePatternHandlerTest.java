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
    }

    public void testGetLang__MatchingSubdomain__ReturnLangCode() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
		assertEquals("en", sut.getLang("en.site.com"));
		assertEquals("es", sut.getLang("es.site.com/"));
		assertEquals("fr", sut.getLang("fr.site.com/en/page/index.html?lang=it&wovn=en"));
        /* incorrect behavior below */
		assertEquals("de", sut.getLang("deutsch.site.com/page"));
	}

    public void testRemoveLang__NonMatchingSubdomain__DoNotModify() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
		assertEquals("/", sut.getLang("/"));
        assertEquals("site.com", sut.getLang("site.com"));
		assertEquals("ja.site.com", sut.getLang("ja.site.com", "fr"));
		assertEquals("ja.fr.site.com", sut.getLang("ja.fr.site.com", "fr"));
		assertEquals("site.com/fr/index.html?wovn=fr", sut.getLang("site.com/fr/index.html?wovn=fr", "fr"));
    }

    public void testRemoveLang__MatchingSubdomain__RemoveLangCode() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
		assertEquals("site.com", sut.getLang("en.site.com", "en"));
		assertEquals("site.com/", sut.getLang("es.site.com/", "es"));
		assertEquals("site.com/fr/index.html?lang=fr&wovn=fr", sut.getLang("fr.site.com/fr/index.html?lang=fr&wovn=fr", "fr");
	}
}
