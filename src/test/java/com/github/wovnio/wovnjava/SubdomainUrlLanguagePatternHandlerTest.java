package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class SubdomainUrlLanguagePatternHandlerTest extends TestCase {
    private Lang english;
    private Lang japanese;
    private Lang french;

    private Lang defaultLang;
    private ArrayList<Lang> supportedLangs;

    protected void setUp() throws Exception {
        this.english = Lang.get("en");
        this.japanese = Lang.get("ja");
        this.french = Lang.get("fr");

        this.defaultLang = this.english;
        this.supportedLangs = new ArrayList<Lang>();
        this.supportedLangs.add(this.english);
        this.supportedLangs.add(this.japanese);
        this.supportedLangs.add(this.french);
    }

    public void testGetLang__NonMatchingSubdomain__ReturnNull() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals(null, sut.getLang("/"));
        assertEquals(null, sut.getLang("/en"));
        assertEquals(null, sut.getLang("/en/page"));
        assertEquals(null, sut.getLang("site.com/page/index.html"));
        assertEquals(null, sut.getLang("site.com/en/pre/fix/index.html"));
        assertEquals(null, sut.getLang("/page?language=en&wovn=fr"));
        assertEquals(null, sut.getLang("deutsch.site.com/page"));
        assertEquals(null, sut.getLang("http://site.com"));
    }

    public void testGetLang__MatchingSubdomain__ValidSupportedLang__ReturnTargetLangObject() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals(this.english, sut.getLang("en.site.com"));
        assertEquals(this.japanese, sut.getLang("ja.site.com/"));
        assertEquals(this.french, sut.getLang("fr.site.com/en/page/index.html?lang=it&wovn=en"));
        assertEquals(this.french, sut.getLang("http://fr.site.com/"));
        assertEquals(this.japanese, sut.getLang("https://ja.site.com?wovn=fr"));
    }

    public void testGetLang__MatchingSubdomain__NotSupportedLang__ReturnNull() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals(null, sut.getLang("th.site.com"));
        assertEquals(null, sut.getLang("es.site.com/"));
        assertEquals(null, sut.getLang("sv.site.com/en/page/index.html?lang=it&wovn=en"));
        assertEquals(null, sut.getLang("http://it.site.com/"));
        assertEquals(null, sut.getLang("https://vi.site.com?wovn=fr"));
    }

    public void testRemoveLang__NonMatchingSubdomain__DoNotModify() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/", sut.removeLang("/", "en"));
        assertEquals("/en/path/index.php", sut.removeLang("/en/path/index.php", "en"));
        assertEquals("?lang=english", sut.removeLang("?lang=english", "en"));
        assertEquals("site.com", sut.removeLang("site.com", "en"));
        assertEquals("ja.site.com", sut.removeLang("ja.site.com", "fr"));
        assertEquals("https://ja.fr.site.com", sut.removeLang("https://ja.fr.site.com", "fr"));
        assertEquals("site.com/fr/index.html?wovn=fr", sut.removeLang("site.com/fr/index.html?wovn=fr", "fr"));
    }

    public void testRemoveLang__MatchingSubdomain__RemoveLangCode() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("site.com", sut.removeLang("en.site.com", "en"));
        assertEquals("site.com/", sut.removeLang("es.site.com/", "es"));
        assertEquals("http://site.com/", sut.removeLang("http://es.site.com/", "es"));
        assertEquals("site.com/fr/index.html?lang=fr&wovn=fr", sut.removeLang("fr.site.com/fr/index.html?lang=fr&wovn=fr", "fr"));
    }

    public void testRemoveLang__EmptyLanguage__DoNotModify() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/", sut.removeLang("/", ""));
        assertEquals("site.com?wovn=en", sut.removeLang("site.com?wovn=en", ""));
        assertEquals("site.com/no/index.html", sut.removeLang("site.com/no/index.html", ""));
        assertEquals("http://fr.site.com/ja", sut.removeLang("http://fr.site.com/ja", ""));
    }

    public void testInsertLang() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/", sut.insertLang("/", "ja"));
        assertEquals("/path/index.html", sut.insertLang("/path/index.html", "ja"));
        assertEquals("ja.site.com?q=none", sut.insertLang("site.com?q=none", "ja"));
        assertEquals("http://ja.site.com?q=none", sut.insertLang("http://site.com?q=none", "ja"));
        assertEquals("https://ja.user13.sub.site.co.jp/home", sut.insertLang("https://user13.sub.site.co.jp/home", "ja"));
        // note that insertLang assumes URL without language code
        assertEquals("ja.ja.site.com", sut.insertLang("ja.site.com", "ja"));
    }
}
