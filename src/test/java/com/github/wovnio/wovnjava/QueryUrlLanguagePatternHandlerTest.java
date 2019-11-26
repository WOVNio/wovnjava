package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class QueryUrlLanguagePatternHandlerTest extends TestCase {
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

    public void testGetLang__NonMatchingQuery__ReturnNull() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals(null, sut.getLang("/"));
        assertEquals(null, sut.getLang("/en"));
        assertEquals(null, sut.getLang("/en/page?wovn&en"));
        assertEquals(null, sut.getLang("site.com/page/index.html"));
        assertEquals(null, sut.getLang("en.site.com/pre/fix/index.html"));
        assertEquals(null, sut.getLang("/page?language=en"));
        assertEquals(null, sut.getLang("/en/?wovn=Nederlands"));
        assertEquals(null, sut.getLang("http://site.com?wovn="));
    }

    public void testGetLang__MatchingQuery__ValidSupportedLang__ReturnTargetLangObject() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals(this.french, sut.getLang("?wovn=fr"));
        assertEquals(this.french, sut.getLang("/?wovn=fr"));
        assertEquals(this.french, sut.getLang("/en/?wovn=fr"));
        assertEquals(this.french, sut.getLang("/en/?lang=es&wovn=fr&country=vi"));
        assertEquals(this.french, sut.getLang("site.com?wovn=fr"));
        assertEquals(this.french, sut.getLang("site.com/?lang=en&wovn=fr"));
        assertEquals(this.french, sut.getLang("http://site.com/?wovn=fr"));
        assertEquals(this.french, sut.getLang("en.site.com/es/page/index.html?wovn=fr"));
    }

    public void testGetLang__MatchingQuery__NotSupportedLang__ReturnNull() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals(null, sut.getLang("?wovn=th"));
        assertEquals(null, sut.getLang("/?wovn=vi"));
        assertEquals(null, sut.getLang("/en/?wovn=sv"));
        assertEquals(null, sut.getLang("/en/?lang=es&wovn=pl&country=vi"));
        assertEquals(null, sut.getLang("site.com?wovn=no"));
        assertEquals(null, sut.getLang("site.com/?lang=es&wovn=ar"));
        assertEquals(null, sut.getLang("http://site.com/?wovn=it"));
        assertEquals(null, sut.getLang("en.site.com/es/page/index.html?wovn=ar"));
    }

    public void testConvertToDefaultLanguage__NonMatchingQuery__DoNotModify() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/", sut.convertToDefaultLanguage("/"));
        assertEquals("/page/index.html", sut.convertToDefaultLanguage("/page/index.html"));
        assertEquals("?wovn=ru", sut.convertToDefaultLanguage("?wovn=ru"));
        assertEquals("/page/?wovn=ru", sut.convertToDefaultLanguage("/page/?wovn=ru"));
        assertEquals("ja.site.com/ja/?lang=ja", sut.convertToDefaultLanguage("ja.site.com/ja/?lang=ja"));
        assertEquals("http://site.com/page/?wovn=japan", sut.convertToDefaultLanguage("http://site.com/page/?wovn=japan"));
    }

    public void testConvertToDefaultLanguage__MatchingQuery__RemoveLangCode() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("", sut.convertToDefaultLanguage("?wovn=ja"));
        assertEquals("/?search=pizza&lang=ja", sut.convertToDefaultLanguage("/?search=pizza&wovn=ja&lang=ja"));
        assertEquals("site.com/page/index.html?wovn", sut.convertToDefaultLanguage("site.com/page/index.html?wovn&wovn=ja"));
        assertEquals("https://ja.site.com/ja/", sut.convertToDefaultLanguage("https://ja.site.com/ja/?wovn=ja"));
    }

    public void testInsertLang() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/?wovn=ja", sut.convertToTargetLanguage("/", this.japanese));
        assertEquals("/path/index.html?wovn=ja", sut.convertToTargetLanguage("/path/index.html", this.japanese));
        assertEquals("site.com/home?q=123&wovn=ja", sut.convertToTargetLanguage("site.com/home?q=123", this.japanese));
        assertEquals("http://site.com?wovn=ja", sut.convertToTargetLanguage("http://site.com", this.japanese));
        // note that convertToTargetLanguage assumes URL without language code
        assertEquals("http://site.com?wovn=fr&wovn=ja", sut.convertToTargetLanguage("http://site.com?wovn=fr", this.japanese));
    }
}
