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
        assertEquals("", sut.getLang("http://site.com"));
    }

    public void testGetLang__MatchingSubdomain__ReturnLangCode() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
        assertEquals("en", sut.getLang("en.site.com"));
        assertEquals("es", sut.getLang("es.site.com/"));
        assertEquals("fr", sut.getLang("fr.site.com/en/page/index.html?lang=it&wovn=en"));
        assertEquals("en", sut.getLang("http://en.site.com/"));
        assertEquals("en", sut.getLang("https://en.site.com?wovn=fr"));
    }

    public void testRemoveLang__NonMatchingSubdomain__DoNotModify() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
        assertEquals("/", sut.removeLang("/", "en"));
        assertEquals("/en/path/index.php", sut.removeLang("/en/path/index.php", "en"));
        assertEquals("?lang=english", sut.removeLang("?lang=english", "en"));
        assertEquals("site.com", sut.removeLang("site.com", "en"));
        assertEquals("ja.site.com", sut.removeLang("ja.site.com", "fr"));
        assertEquals("https://ja.fr.site.com", sut.removeLang("https://ja.fr.site.com", "fr"));
        assertEquals("site.com/fr/index.html?wovn=fr", sut.removeLang("site.com/fr/index.html?wovn=fr", "fr"));
    }

    public void testRemoveLang__MatchingSubdomain__RemoveLangCode() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
        assertEquals("site.com", sut.removeLang("en.site.com", "en"));
        assertEquals("site.com/", sut.removeLang("es.site.com/", "es"));
        assertEquals("http://site.com/", sut.removeLang("http://es.site.com/", "es"));
        assertEquals("site.com/fr/index.html?lang=fr&wovn=fr", sut.removeLang("fr.site.com/fr/index.html?lang=fr&wovn=fr", "fr"));
    }

    public void testRemoveLang__EmptyLanguage__DoNotModify() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
        assertEquals("/", sut.removeLang("/", ""));
        assertEquals("site.com?wovn=en", sut.removeLang("site.com?wovn=en", ""));
        assertEquals("site.com/no/index.html", sut.removeLang("site.com/no/index.html", ""));
        assertEquals("http://fr.site.com/ja", sut.removeLang("http://fr.site.com/ja", ""));
    }

    public void testInsertLang() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler();
        assertEquals("/", sut.insertLang("/", "ja"));
        assertEquals("/path/index.html", sut.insertLang("/path/index.html", "ja"));
        assertEquals("ja.site.com?q=none", sut.insertLang("site.com?q=none", "ja"));
        assertEquals("http://ja.site.com?q=none", sut.insertLang("http://site.com?q=none", "ja"));
        assertEquals("https://ja.user13.sub.site.co.jp/home", sut.insertLang("https://user13.sub.site.co.jp/home", "ja"));
        // note that insertLang assumes URL without language code
        assertEquals("ja.ja.site.com", sut.insertLang("ja.site.com", "ja"));
    }
}
