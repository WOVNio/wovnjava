package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class QueryUrlLanguagePatternHandlerTest extends TestCase {
    private Lang defaultLang;
    private ArrayList<Lang> supportedLangs;

    protected void setUp() throws Exception {
        this.defaultLang = Lang.get("en");
        this.supportedLangs = new ArrayList<Lang>();
        this.supportedLangs.add(Lang.get("en"));
        this.supportedLangs.add(Lang.get("ja"));
    }

    public void testGetLang__NonMatchingQuery__ReturnEmptyLang() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("", sut.getLang("/"));
        assertEquals("", sut.getLang("/en"));
        assertEquals("", sut.getLang("/en/page?wovn&en"));
        assertEquals("", sut.getLang("site.com/page/index.html"));
        assertEquals("", sut.getLang("en.site.com/pre/fix/index.html"));
        assertEquals("", sut.getLang("/page?language=en"));
        assertEquals("", sut.getLang("/en/?wovn=Nederlands"));
        assertEquals("", sut.getLang("http://site.com?wovn="));
    }

    public void testGetLang__MatchingQuery__ReturnLangCode() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("fr", sut.getLang("?wovn=fr"));
        assertEquals("fr", sut.getLang("/?wovn=fr"));
        assertEquals("fr", sut.getLang("/en/?wovn=fr"));
        assertEquals("fr", sut.getLang("/en/?lang=es&wovn=fr&country=vi"));
        assertEquals("fr", sut.getLang("site.com?wovn=fr"));
        assertEquals("fr", sut.getLang("site.com/?lang=en&wovn=fr"));
        assertEquals("fr", sut.getLang("http://site.com/?wovn=fr"));
        assertEquals("fr", sut.getLang("en.site.com/es/page/index.html?wovn=fr"));
    }

    public void testRemoveLang__NonMatchingQuery__DoNotModify() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/", sut.removeLang("/", "ja"));
        assertEquals("/page/index.html", sut.removeLang("/page/index.html", "ja"));
        assertEquals("?wovn=en", sut.removeLang("?wovn=en", "ja"));
        assertEquals("/page/?wovn=en", sut.removeLang("/page/?wovn=en", "ja"));
        assertEquals("ja.site.com/ja/?lang=ja", sut.removeLang("ja.site.com/ja/?lang=ja", "ja"));
        assertEquals("http://site.com/page/?wovn=japan", sut.removeLang("http://site.com/page/?wovn=japan", "ja"));
    }

    public void testRemoveLang__MatchingQuery__RemoveLangCode() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("", sut.removeLang("?wovn=ja", "ja"));
        assertEquals("/?search=pizza&lang=ja", sut.removeLang("/?search=pizza&wovn=ja&lang=ja", "ja"));
        assertEquals("site.com/page/index.html?wovn", sut.removeLang("site.com/page/index.html?wovn&wovn=ja", "ja"));
        assertEquals("https://ja.site.com/ja/", sut.removeLang("https://ja.site.com/ja/?wovn=ja", "ja"));
    }

    public void testRemoveLang__EmptyLanguage__DoNotModify() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/", sut.removeLang("/", ""));
        assertEquals("site.com?wovn=en", sut.removeLang("site.com?wovn=en", ""));
        assertEquals("site.com/no/index.html", sut.removeLang("site.com/no/index.html", ""));
        assertEquals("http://fr.site.com/ja", sut.removeLang("http://fr.site.com/ja", ""));
    }

    public void testInsertLang() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs);
        assertEquals("/?wovn=ja", sut.insertLang("/", "ja"));
        assertEquals("/path/index.html?wovn=ja", sut.insertLang("/path/index.html", "ja"));
        assertEquals("site.com/home?q=123&wovn=ja", sut.insertLang("site.com/home?q=123", "ja"));
        assertEquals("http://site.com?wovn=ja", sut.insertLang("http://site.com", "ja"));
        // note that insertLang assumes URL without language code
        assertEquals("http://site.com?wovn=fr&wovn=ja", sut.insertLang("http://site.com?wovn=fr", "ja"));
    }
}
