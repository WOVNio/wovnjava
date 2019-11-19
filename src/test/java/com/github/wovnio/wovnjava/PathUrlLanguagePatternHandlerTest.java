package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class PathUrlLanguagePatternHandlerTest extends TestCase {
    private Lang defaultLang;
    private ArrayList<Lang> supportedLangs;

    protected void setUp() throws Exception {
        this.defaultLang = Lang.get("en");
        this.supportedLangs = new ArrayList<Lang>();
        this.supportedLangs.add(Lang.get("en"));
        this.supportedLangs.add(Lang.get("ja"));
    }

    private PathUrlLanguagePatternHandler createWithParams(String sitePrefixPath) {
        return new PathUrlLanguagePatternHandler(this.defaultLang, this.supportedLangs, sitePrefixPath);
    }

    public void testGetLang__NonMatchingPath__ReturnEmptyLang() {
        PathUrlLanguagePatternHandler sut = createWithParams("");
        assertEquals("", sut.getLang(""));
        assertEquals("", sut.getLang("/"));
        assertEquals("", sut.getLang("?query"));
        assertEquals("", sut.getLang("/page"));
        assertEquals("", sut.getLang("site.com/page/index.html"));
        assertEquals("", sut.getLang("en.site.com/pre/fix/index.html"));
        assertEquals("", sut.getLang("/page?wovn=en"));
        assertEquals("", sut.getLang("site.com/French/"));
        assertEquals("", sut.getLang("http://site.com/Suomi/page/index.html"));
    }

    public void testGetLang__MatchingPath__ReturnLangCode() {
        PathUrlLanguagePatternHandler sut = createWithParams("");
        assertEquals("fr", sut.getLang("/fr"));
        assertEquals("fr", sut.getLang("/fr/"));
        assertEquals("fr", sut.getLang("/fr?wovn=en"));
        assertEquals("fr", sut.getLang("/fr/?wovn=en"));
        assertEquals("fr", sut.getLang("http://site.com/fr/page"));
        assertEquals("fr", sut.getLang("https://site.com/fr/page/index.html"));
        assertEquals("fr", sut.getLang("en.site.com/fr/page/index.html?wovn=es"));
    }

    public void testGetLang__SitePrefixPath__NonMatchingPath__ReturnEmptyLang() {
        PathUrlLanguagePatternHandler sut = createWithParams("/pre/fix");
        assertEquals("", sut.getLang("site.com/fr"));
        assertEquals("", sut.getLang("en.site.com/en/?wovn=en"));
        assertEquals("", sut.getLang("/es/pre/fix/page/index.html"));
        assertEquals("", sut.getLang("/pre/fr/fix/page/index.html"));
        assertEquals("", sut.getLang("/pre/en/fix/page/index.html"));
        assertEquals("", sut.getLang("/pre/fix/page/en/index.html"));
        assertEquals("", sut.getLang("/pre/fix/french/page/index.html"));
        assertEquals("", sut.getLang("https://en.site.com/en/page/"));
    }

    public void testGetLang__SitePrefixPath__MatchingPath__ReturnLangCode() {
        PathUrlLanguagePatternHandler sut = createWithParams("/pre/fix");
        assertEquals("fr", sut.getLang("site.com/pre/fix/fr"));
        assertEquals("fr", sut.getLang("site.com/pre/fix/fr/"));
        assertEquals("fr", sut.getLang("site.com/pre/fix/fr?query"));
        assertEquals("fr", sut.getLang("site.com/pre/fix/fr/?query"));
        assertEquals("fr", sut.getLang("en.site.com/pre/fix/fr/index.html?wovn=es"));
        assertEquals("fr", sut.getLang("/pre/fix/fr/index.html"));
        assertEquals("fr", sut.getLang("/pre/fix/fr/page/index.html"));
        assertEquals("fr", sut.getLang("https://en.site.com/pre/fix/fr/page/"));
    }

    public void testRemoveLang__NonMatchingPath__DoNotModify() {
        PathUrlLanguagePatternHandler sut = createWithParams("");
        assertEquals("", sut.removeLang("", "ja"));
        assertEquals("?query", sut.removeLang("?query", "ja"));
        assertEquals("/", sut.removeLang("/", "ja"));
        assertEquals("/?query", sut.removeLang("/?query", "ja"));
        assertEquals("site.com", sut.removeLang("site.com", "ja"));
        assertEquals("site.com?query", sut.removeLang("site.com?query", "ja"));
        assertEquals("site.com/", sut.removeLang("site.com/", "ja"));
        assertEquals("site.com/page/", sut.removeLang("site.com/page/", "ja"));
        assertEquals("/global/en/page/", sut.removeLang("/global/en/page/", "ja"));
        assertEquals("site.com/en/page/", sut.removeLang("site.com/en/page/", "ja"));
        assertEquals("site.com/english/page/", sut.removeLang("site.com/english/page/", "en"));
        assertEquals("site.com/en/ja/page/", sut.removeLang("site.com/en/ja/page/", "ja"));
        assertEquals("/global/page/ja/index.html", sut.removeLang("/global/page/ja/index.html", "ja"));
        assertEquals("http://www.site.com/global/ja", sut.removeLang("http://www.site.com/global/ja", "ja"));
        assertEquals("https://test.com/en/path/", sut.removeLang("https://test.com/en/path/", "ja"));
    }

    public void testRemoveLang__MatchingSupportedLang__RemoveLangCode() {
        PathUrlLanguagePatternHandler sut = createWithParams("");
        assertEquals("", sut.removeLang("/ja", "ja"));
        assertEquals("/", sut.removeLang("/ja/", "ja"));
        assertEquals("?query", sut.removeLang("/ja?query", "ja"));
        assertEquals("/?query", sut.removeLang("/ja/?query", "ja"));
        assertEquals("site.com", sut.removeLang("site.com/en", "en"));
        assertEquals("site.com/", sut.removeLang("site.com/ja/", "ja"));
        assertEquals("site.com/?query", sut.removeLang("site.com/ja/?query", "ja"));
        assertEquals("site.com/index.html", sut.removeLang("site.com/no/index.html", "no"));
        assertEquals("site.com/page/index.html", sut.removeLang("site.com/en/page/index.html", "en"));
        assertEquals("/page/index.html", sut.removeLang("/en/page/index.html", "en"));
        assertEquals("/page/index.html?query", sut.removeLang("/en/page/index.html?query", "en"));
        assertEquals("site.com/en/page/", sut.removeLang("site.com/ja/en/page/", "ja"));
        assertEquals("site.com/ja/page/", sut.removeLang("site.com/ja/ja/page/", "ja"));
        assertEquals("http://www.site.com", sut.removeLang("http://www.site.com/ja", "ja"));
        assertEquals("https://test.com/path/index.html", sut.removeLang("https://test.com/en/path/index.html", "en"));
    }

    public void testRemoveLang__SitePrefixPath__NonMatchingPath__DoNotModify() {
        PathUrlLanguagePatternHandler sut = createWithParams("/pre/fix");
        assertEquals("/", sut.removeLang("/", "ja"));
        assertEquals("site.com", sut.removeLang("site.com", "ja"));
        assertEquals("site.com?query", sut.removeLang("site.com?query", "ja"));
        assertEquals("site.com/pre/fix/", sut.removeLang("site.com/pre/fix/", "ja"));
        assertEquals("site.com/no/index.html", sut.removeLang("site.com/no/index.html", "no"));
        assertEquals("site.com/no/index.html?query", sut.removeLang("site.com/no/index.html?query", "no"));
        assertEquals("site.com/fr/pre/fix/", sut.removeLang("site.com/fr/pre/fix/", "fr"));
        assertEquals("site.com/pre/ja/fix/", sut.removeLang("site.com/pre/ja/fix/", "ja"));
        assertEquals("site.com/prefix/no", sut.removeLang("site.com/prefix/no", "no"));
        assertEquals("/pre/fix/page/en/index.html", sut.removeLang("/pre/fix/page/en/index.html", "en"));
        assertEquals("/pre/fix/ja/page/index.html", sut.removeLang("/pre/fix/ja/page/index.html", "en"));
        assertEquals("http://www.site.com/ja", sut.removeLang("http://www.site.com/ja", "ja"));
    }

    public void testRemoveLang__SitePrefixPath__MatchingSupportedLang__RemoveLangCode() {
        PathUrlLanguagePatternHandler sut = createWithParams("/pre/fix");
        assertEquals("/pre/fix", sut.removeLang("/pre/fix/ja", "ja"));
        assertEquals("/pre/fix?query", sut.removeLang("/pre/fix/ja?query", "ja"));
        assertEquals("/pre/fix/", sut.removeLang("/pre/fix/ja/", "ja"));
        assertEquals("/pre/fix/?query", sut.removeLang("/pre/fix/ja/?query", "ja"));
        assertEquals("http://site.com/pre/fix/", sut.removeLang("http://site.com/pre/fix/en/", "en"));
        assertEquals("site.com/pre/fix/page/index.html", sut.removeLang("site.com/pre/fix/no/page/index.html", "no"));
        assertEquals("site.com/pre/fix/page/index.html?query", sut.removeLang("site.com/pre/fix/no/page/index.html?query", "no"));
        assertEquals("http://www.site.com/pre/fix", sut.removeLang("http://www.site.com/pre/fix/ja", "ja"));
    }

    public void testRemoveLang__EmptyLanguage__DoNotModify() {
        PathUrlLanguagePatternHandler sut = createWithParams("");
        assertEquals("/", sut.removeLang("/", ""));
        assertEquals("site.com?wovn=en", sut.removeLang("site.com?wovn=en", ""));
        assertEquals("site.com/no/index.html", sut.removeLang("site.com/no/index.html", ""));
        assertEquals("http://fr.site.com/ja", sut.removeLang("http://fr.site.com/ja", ""));
    }

    public void testIsMatchSitePrefixPath__DefaultSettings() {
        PathUrlLanguagePatternHandler sut = createWithParams("");
        assertEquals(true, sut.canInterceptUrl(""));
        assertEquals(true, sut.canInterceptUrl("?query"));
        assertEquals(true, sut.canInterceptUrl("/pre/fix/ja"));
        assertEquals(true, sut.canInterceptUrl("/pre/fix/ja?query"));
        assertEquals(true, sut.canInterceptUrl("http://www.site.com"));
        assertEquals(true, sut.canInterceptUrl("https://site.com/pre/fix/en/"));
        assertEquals(true, sut.canInterceptUrl("site.com/no/page/index.html"));
        assertEquals(true, sut.canInterceptUrl("site.com/no/page/index.html?query"));
    }

    public void testIsMatchSitePrefixPath__UsingSitePrefixPath() {
        PathUrlLanguagePatternHandler sut = createWithParams("/pre/fix");
        assertEquals(false, sut.canInterceptUrl(""));
        assertEquals(false, sut.canInterceptUrl("site.com"));
        assertEquals(false, sut.canInterceptUrl("site.com?query"));
        assertEquals(false, sut.canInterceptUrl("www.site.com/pre"));
        assertEquals(false, sut.canInterceptUrl("http://www.site.com/en/pre/fix"));
        assertEquals(true, sut.canInterceptUrl("/pre/fix"));
        assertEquals(true, sut.canInterceptUrl("/pre/fix/"));
        assertEquals(true, sut.canInterceptUrl("/pre/fix?query"));
        assertEquals(true, sut.canInterceptUrl("/pre/fix/?query"));
        assertEquals(true, sut.canInterceptUrl("/pre/fix/ja"));
        assertEquals(true, sut.canInterceptUrl("https://site.com/pre/fix/en/"));
        assertEquals(true, sut.canInterceptUrl("site.com/pre/fix/page/index.html"));
        assertEquals(true, sut.canInterceptUrl("site.com/pre/fix/page/index.html?query"));
    }

    public void testInsertLang__DefaultSettings() {
        PathUrlLanguagePatternHandler sut = createWithParams("");
        assertEquals("/ja", sut.insertLang("", "ja"));
        assertEquals("/ja/", sut.insertLang("/", "ja"));
        assertEquals("/ja/path/index.html", sut.insertLang("/path/index.html", "ja"));
        assertEquals("site.com/ja/", sut.insertLang("site.com/", "ja"));
        assertEquals("http://site.com/ja/home", sut.insertLang("http://site.com/home", "ja"));
        assertEquals("https://fr.site.co.uk/ja?query", sut.insertLang("https://fr.site.co.uk?query", "ja"));
    }

    public void testInsertLang__UsingSitePrefixPath__MatchesSitePrefixPath() {
        PathUrlLanguagePatternHandler sut = createWithParams("/pre/fix");
        assertEquals("/pre/fix/ja", sut.insertLang("/pre/fix", "ja"));
        assertEquals("/pre/fix/ja/", sut.insertLang("/pre/fix/", "ja"));
        assertEquals("/pre/fix/ja/path/index.html", sut.insertLang("/pre/fix/path/index.html", "ja"));
        assertEquals("site.com/pre/fix/ja/", sut.insertLang("site.com/pre/fix/", "ja"));
        assertEquals("http://site.com/pre/fix/ja?query", sut.insertLang("http://site.com/pre/fix?query", "ja"));
    }

    public void testInsertLang__UsingSitePrefixPath__SitePrefixPathNotMatched() {
        PathUrlLanguagePatternHandler sut = createWithParams("/pre/fix");
        assertEquals("", sut.insertLang("", "ja"));
        assertEquals("/", sut.insertLang("/", "ja"));
        assertEquals("/path/index.html", sut.insertLang("/path/index.html", "ja"));
        assertEquals("site.com/", sut.insertLang("site.com/", "ja"));
        assertEquals("http://site.com/home", sut.insertLang("http://site.com/home", "ja"));
        assertEquals("https://fr.site.co.uk?query", sut.insertLang("https://fr.site.co.uk?query", "ja"));
    }
}
