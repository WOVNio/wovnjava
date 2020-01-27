package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class QueryUrlLanguagePatternHandlerTest extends TestCase {
    private Lang english;
    private Lang japanese;
    private Lang french;

    private Lang defaultLang;
    private LanguageAliases languageAliasesEmpty;
    private LanguageAliases languageAliasesConfigured;

    protected void setUp() throws Exception {
        this.english = Lang.get("en");
        this.japanese = Lang.get("ja");
        this.french = Lang.get("fr");

        this.defaultLang = this.english;
        ArrayList<Lang> supportedLangs = new ArrayList<Lang>();
        supportedLangs.add(this.english);
        supportedLangs.add(this.japanese);
        supportedLangs.add(this.french);

        Map<Lang, String> langCodeAliasSetting = new LinkedHashMap<Lang, String>();
        this.languageAliasesEmpty = new LanguageAliases(supportedLangs, langCodeAliasSetting, this.defaultLang);

        langCodeAliasSetting.put(this.english, "us");
        langCodeAliasSetting.put(this.japanese, "japan");
        this.languageAliasesConfigured = new LanguageAliases(supportedLangs, langCodeAliasSetting, this.defaultLang);
    }

    public void testGetLang__NonMatchingQuery__ReturnDefaultLang() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.languageAliasesEmpty);
        assertEquals(this.defaultLang, sut.getLang("/"));
        assertEquals(this.defaultLang, sut.getLang("/en"));
        assertEquals(this.defaultLang, sut.getLang("/en/page?wovn&en"));
        assertEquals(this.defaultLang, sut.getLang("site.com/page/index.html"));
        assertEquals(this.defaultLang, sut.getLang("en.site.com/pre/fix/index.html"));
        assertEquals(this.defaultLang, sut.getLang("/page?language=en"));
        assertEquals(this.defaultLang, sut.getLang("/en/?wovn=Nederlands"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com?wovn="));
    }

    public void testGetLang__MatchingQuery__ValidSupportedLang__ReturnTargetLangObject() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.languageAliasesEmpty);
        assertEquals(this.french, sut.getLang("?wovn=fr"));
        assertEquals(this.french, sut.getLang("/?wovn=fr"));
        assertEquals(this.french, sut.getLang("/en/?wovn=fr"));
        assertEquals(this.french, sut.getLang("/en/?lang=es&wovn=fr&country=vi"));
        assertEquals(this.french, sut.getLang("site.com?wovn=fr"));
        assertEquals(this.french, sut.getLang("site.com/?lang=en&wovn=fr"));
        assertEquals(this.french, sut.getLang("http://site.com/?wovn=fr"));
        assertEquals(this.french, sut.getLang("en.site.com/es/page/index.html?wovn=fr"));
    }

    public void testGetLang__MatchingQuery__NotSupportedLang__ReturnDefaultLang() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.languageAliasesEmpty);
        assertEquals(this.defaultLang, sut.getLang("?wovn=th"));
        assertEquals(this.defaultLang, sut.getLang("/?wovn=vi"));
        assertEquals(this.defaultLang, sut.getLang("/en/?wovn=sv"));
        assertEquals(this.defaultLang, sut.getLang("/en/?lang=es&wovn=pl&country=vi"));
        assertEquals(this.defaultLang, sut.getLang("site.com?wovn=no"));
        assertEquals(this.defaultLang, sut.getLang("site.com/?lang=es&wovn=ar"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com/?wovn=it"));
        assertEquals(this.defaultLang, sut.getLang("en.site.com/es/page/index.html?wovn=ar"));
    }

    public void testGetLang__HasLanguageAliases() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.languageAliasesConfigured);
        assertEquals(this.defaultLang, sut.getLang("?wovn=th"));
        assertEquals(this.defaultLang, sut.getLang("/?wovn=football"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com/?wovn=en"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com/?wovn=ja"));

        assertEquals(this.english, sut.getLang("http://site.com/?wovn=us"));
        assertEquals(this.japanese, sut.getLang("http://site.com/?wovn=japan"));
        assertEquals(this.french, sut.getLang("http://site.com/?wovn=fr"));
    }

    public void testConvertToDefaultLanguage__NonMatchingQuery__DoNotModify() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.languageAliasesEmpty);
        assertEquals("/", sut.convertToDefaultLanguage("/"));
        assertEquals("", sut.convertToDefaultLanguage("?wovn=ja"));
        assertEquals("/page/", sut.convertToDefaultLanguage("/page/?wovn=ru"));
        assertEquals("/page/index.html", sut.convertToDefaultLanguage("/page/index.html"));
        assertEquals("ja.site.com/ja/?lang=ja", sut.convertToDefaultLanguage("ja.site.com/ja/?lang=ja"));
        assertEquals("http://site.com/page/", sut.convertToDefaultLanguage("http://site.com/page/?wovn=japan"));
        assertEquals("/?search=pizza&lang=ja", sut.convertToDefaultLanguage("/?search=pizza&wovn=ja&lang=ja"));
        assertEquals("site.com/page/index.html?wovn", sut.convertToDefaultLanguage("site.com/page/index.html?wovn&wovn=ja"));
        assertEquals("https://ja.site.com/ja/", sut.convertToDefaultLanguage("https://ja.site.com/ja/?wovn=ja"));
    }

    public void testConvertToDefaultLanguage__HasLanguageAliases__RemoveLangCode() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.languageAliasesConfigured);
        assertEquals("", sut.convertToDefaultLanguage("?wovn=ja"));
        assertEquals("/?search=pizza&lang=ja", sut.convertToDefaultLanguage("/?search=pizza&wovn=japan&lang=ja"));
        assertEquals("site.com/page/index.html?wovn", sut.convertToDefaultLanguage("site.com/page/index.html?wovn&wovn=us"));
        assertEquals("https://ja.site.com/ja/", sut.convertToDefaultLanguage("https://ja.site.com/ja/?wovn=en"));
        assertEquals("https://ja.site.com/page", sut.convertToDefaultLanguage("https://ja.site.com/page"));
    }

    public void testConvertToTargetLanguage() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.languageAliasesEmpty);
        assertEquals("/?wovn=ja", sut.convertToTargetLanguage("/", this.japanese));
        assertEquals("/path/index.html?wovn=ja", sut.convertToTargetLanguage("/path/index.html", this.japanese));
        assertEquals("site.com/home?q=123&wovn=ja", sut.convertToTargetLanguage("site.com/home?q=123", this.japanese));
        assertEquals("http://site.com?wovn=ja", sut.convertToTargetLanguage("http://site.com", this.japanese));
        assertEquals("http://site.com?wovn=ja", sut.convertToTargetLanguage("http://site.com?wovn=fr", this.japanese));
        assertEquals("http://site.com?wovn=ja", sut.convertToTargetLanguage("http://site.com?wovn=ru", this.japanese));

        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com", this.english));
        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com?wovn=ru", this.english));
        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com?wovn=japan", this.english));
    }

    public void testConvertToTargetLanguage__HasLanguageAliases() {
        QueryUrlLanguagePatternHandler sut = new QueryUrlLanguagePatternHandler(this.defaultLang, this.languageAliasesConfigured);
        assertEquals("/?wovn=japan", sut.convertToTargetLanguage("/", this.japanese));
        assertEquals("/path/index.html?wovn=japan", sut.convertToTargetLanguage("/path/index.html", this.japanese));
        assertEquals("site.com/home?q=123&wovn=japan", sut.convertToTargetLanguage("site.com/home?q=123", this.japanese));
        assertEquals("http://site.com?wovn=japan", sut.convertToTargetLanguage("http://site.com", this.japanese));
        assertEquals("http://site.com?wovn=japan", sut.convertToTargetLanguage("http://site.com?wovn=fr", this.japanese));
        assertEquals("http://site.com?wovn=japan", sut.convertToTargetLanguage("http://site.com?wovn=ru", this.japanese));

        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com", this.english));
        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com?wovn=ru", this.english));
        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com?wovn=japan", this.english));
    }
}
