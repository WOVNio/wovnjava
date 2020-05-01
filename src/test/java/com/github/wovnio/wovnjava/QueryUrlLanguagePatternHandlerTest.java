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
    private Lang chinese;

    private Lang defaultLang;
    private ArrayList<Lang> supportedLangs;

    protected void setUp() throws Exception {
        this.english = Lang.get("en");
        this.japanese = Lang.get("ja");
        this.french = Lang.get("fr");
        this.chinese = Lang.get("zh-cht");

        this.supportedLangs = new ArrayList<Lang>();
        this.supportedLangs.add(this.english);
        this.supportedLangs.add(this.japanese);
        this.supportedLangs.add(this.french);
        this.supportedLangs.add(this.chinese);
    }

    private QueryUrlLanguagePatternHandler create(Lang defaultLanguage) {
        this.defaultLang = defaultLanguage;

        Map<Lang, String> langCodeAliasSetting = new LinkedHashMap<Lang, String>();
        LanguageAliases languageAliasesEmpty = new LanguageAliases(this.supportedLangs, langCodeAliasSetting, this.defaultLang);

        return new QueryUrlLanguagePatternHandler(this.defaultLang, languageAliasesEmpty);
    }

    private QueryUrlLanguagePatternHandler createWithAliases(Lang defaultLanguage) {
        this.defaultLang = defaultLanguage;

        Map<Lang, String> langCodeAliasSetting = new LinkedHashMap<Lang, String>();
        langCodeAliasSetting.put(this.english, "us");
        langCodeAliasSetting.put(this.japanese, "japan");
        LanguageAliases languageAliasesConfigured = new LanguageAliases(this.supportedLangs, langCodeAliasSetting, this.defaultLang);

        return new QueryUrlLanguagePatternHandler(this.defaultLang, languageAliasesConfigured);
    }

    public void testGetLang__NonMatchingQuery__ReturnDefaultLang() {
        QueryUrlLanguagePatternHandler sut = create(this.english);
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
        QueryUrlLanguagePatternHandler sut = create(this.english);
        assertEquals(this.french, sut.getLang("?wovn=fr"));
        assertEquals(this.french, sut.getLang("/?wovn=fr"));
        assertEquals(this.french, sut.getLang("/en/?wovn=fr"));
        assertEquals(this.french, sut.getLang("/en/?lang=es&wovn=fr&country=vi"));
        assertEquals(this.french, sut.getLang("site.com?wovn=fr"));
        assertEquals(this.french, sut.getLang("site.com/?lang=en&wovn=fr"));
        assertEquals(this.french, sut.getLang("http://site.com/?wovn=fr"));
        assertEquals(this.french, sut.getLang("en.site.com/es/page/index.html?wovn=fr"));
    }

    public void testGetLang__MatchingQuery__ValidSupportedRegionalLang__ReturnTargetLangObject() {
        QueryUrlLanguagePatternHandler sut = create(this.english);
        assertEquals(this.chinese, sut.getLang("?wovn=zh-CHT"));
        assertEquals(this.chinese, sut.getLang("/?wovn=zh-CHT"));
        assertEquals(this.chinese, sut.getLang("/en/?wovn=zh-CHT"));
        assertEquals(this.chinese, sut.getLang("/en/?lang=es&wovn=zh-CHT&country=vi"));
        assertEquals(this.chinese, sut.getLang("site.com?wovn=zh-CHT"));
        assertEquals(this.chinese, sut.getLang("site.com/?lang=en&wovn=zh-CHT"));
        assertEquals(this.chinese, sut.getLang("http://site.com/?wovn=zh-CHT"));
        assertEquals(this.chinese, sut.getLang("en.site.com/es/page/index.html?wovn=zh-CHT"));
    }

    public void testGetLang__MatchingQuery__NotSupportedLang__ReturnDefaultLang() {
        QueryUrlLanguagePatternHandler sut = create(this.english);
        assertEquals(this.defaultLang, sut.getLang("?wovn=th"));
        assertEquals(this.defaultLang, sut.getLang("/?wovn=vi"));
        assertEquals(this.defaultLang, sut.getLang("/en/?wovn=sv"));
        assertEquals(this.defaultLang, sut.getLang("/en/?lang=es&wovn=pl&country=vi"));
        assertEquals(this.defaultLang, sut.getLang("site.com?wovn=no"));
        assertEquals(this.defaultLang, sut.getLang("site.com/?lang=es&wovn=ar"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com/?wovn=it"));
        assertEquals(this.defaultLang, sut.getLang("en.site.com/es/page/index.html?wovn=ar"));
    }

    public void testGetLang__HasLanguageAliases__NonMatchingQuery__ReturnDefaultLang() {
        QueryUrlLanguagePatternHandler sut = createWithAliases(this.english);
        assertEquals(this.defaultLang, sut.getLang("?wovn=th"));
        assertEquals(this.defaultLang, sut.getLang("/?wovn=football"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com/?wovn=en"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com/?wovn=ja"));
    }

    public void testGetLang__HasLanguageAliases__MatchingQuery__ReturnTargetLang() {
        QueryUrlLanguagePatternHandler sut = createWithAliases(this.english);
        assertEquals(this.english, sut.getLang("http://site.com/?wovn=us"));
        assertEquals(this.japanese, sut.getLang("http://site.com/?wovn=japan"));
        assertEquals(this.french, sut.getLang("http://site.com/?wovn=fr"));
        assertEquals(this.chinese, sut.getLang("http://site.com/?wovn=zh-CHT"));
    }

    public void testConvertToDefaultLanguage__NonMatchingQuery__DoNotModify() {
        QueryUrlLanguagePatternHandler sut = create(this.english);
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
        QueryUrlLanguagePatternHandler sut = createWithAliases(this.english);
        assertEquals("", sut.convertToDefaultLanguage("?wovn=ja"));
        assertEquals("", sut.convertToDefaultLanguage("?wovn=zh-CHT"));
        assertEquals("/?search=pizza&lang=ja", sut.convertToDefaultLanguage("/?search=pizza&wovn=japan&lang=ja"));
        assertEquals("site.com/page/index.html?wovn", sut.convertToDefaultLanguage("site.com/page/index.html?wovn&wovn=us"));
        assertEquals("https://ja.site.com/ja/", sut.convertToDefaultLanguage("https://ja.site.com/ja/?wovn=en"));
        assertEquals("https://ja.site.com/page", sut.convertToDefaultLanguage("https://ja.site.com/page"));
    }

    public void testConvertToTargetLanguage() {
        QueryUrlLanguagePatternHandler sut = create(this.english);
        assertEquals("/?wovn=ja", sut.convertToTargetLanguage("/", this.japanese));
        assertEquals("/?wovn=zh-CHT", sut.convertToTargetLanguage("/", this.chinese));
        assertEquals("/path/index.html?wovn=ja", sut.convertToTargetLanguage("/path/index.html", this.japanese));
        assertEquals("site.com/home?q=123&wovn=ja", sut.convertToTargetLanguage("site.com/home?q=123", this.japanese));
        assertEquals("http://site.com?wovn=ja", sut.convertToTargetLanguage("http://site.com", this.japanese));
        assertEquals("http://site.com?wovn=ja", sut.convertToTargetLanguage("http://site.com?wovn=fr", this.japanese));
        assertEquals("http://site.com?wovn=ja", sut.convertToTargetLanguage("http://site.com?wovn=ru", this.japanese));

        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com", this.english));
        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com?wovn=ru", this.english));
        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com?wovn=japan", this.english));
        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com?wovn=zh-CHT", this.english));
    }

    public void testConvertToTargetLanguage__HasLanguageAliases() {
        QueryUrlLanguagePatternHandler sut = createWithAliases(this.english);
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
