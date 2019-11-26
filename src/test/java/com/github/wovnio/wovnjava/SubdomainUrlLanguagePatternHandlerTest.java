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

    public void testConvertToDefaultLanguage__NonMatchingSubdomain__DoNotModify() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/", sut.convertToDefaultLanguage("/"));
        assertEquals("/en/path/index.php", sut.convertToDefaultLanguage("/en/path/index.php"));
        assertEquals("?lang=english", sut.convertToDefaultLanguage("?lang=english"));
        assertEquals("site.com", sut.convertToDefaultLanguage("site.com"));
        assertEquals("ru.site.com", sut.convertToDefaultLanguage("ru.site.com"));
        assertEquals("https://ru.fr.site.com", sut.convertToDefaultLanguage("https://ru.fr.site.com"));
        assertEquals("site.com/fr/index.html?wovn=fr", sut.convertToDefaultLanguage("site.com/fr/index.html?wovn=fr"));
    }

    public void testConvertToDefaultLanguage__MatchingSubdomain__RemoveLangCode() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("site.com", sut.convertToDefaultLanguage("en.site.com"));
        assertEquals("site.com/", sut.convertToDefaultLanguage("fr.site.com/"));
        assertEquals("http://site.com/", sut.convertToDefaultLanguage("http://fr.site.com/"));
        assertEquals("site.com/fr/index.html?lang=fr&wovn=fr", sut.convertToDefaultLanguage("fr.site.com/fr/index.html?lang=fr&wovn=fr"));
    }

    public void testConvertToTargetLanguage() {
        SubdomainUrlLanguagePatternHandler sut = new SubdomainUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/", sut.convertToTargetLanguage("/", this.japanese));
        assertEquals("/path/index.html", sut.convertToTargetLanguage("/path/index.html", this.japanese));
        assertEquals("ja.site.com?q=none", sut.convertToTargetLanguage("site.com?q=none", this.japanese));
        assertEquals("http://ja.site.com?q=none", sut.convertToTargetLanguage("http://site.com?q=none", this.japanese));
        assertEquals("https://ja.user13.sub.site.co.jp/home", sut.convertToTargetLanguage("https://user13.sub.site.co.jp/home", this.japanese));
        assertEquals("ja.site.com", sut.convertToTargetLanguage("ja.site.com", this.japanese));
        assertEquals("ja.site.com", sut.convertToTargetLanguage("fr.site.com", this.japanese));
        assertEquals("ja.ru.site.com", sut.convertToTargetLanguage("ru.site.com", this.japanese));
    }
}
