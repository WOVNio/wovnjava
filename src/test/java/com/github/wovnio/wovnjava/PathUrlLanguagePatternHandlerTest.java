package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import junit.framework.TestCase;

public class PathUrlLanguagePatternHandlerTest extends TestCase {
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

    private PathUrlLanguagePatternHandler create(Lang defaultLanguage, String sitePrefixPath) {
        this.defaultLang = defaultLanguage;

        Map<Lang, String> langCodeAliasSetting = new LinkedHashMap<Lang, String>();
        LanguageAliases languageAliasesEmpty = new LanguageAliases(this.supportedLangs, langCodeAliasSetting, this.defaultLang);

        return new PathUrlLanguagePatternHandler(this.defaultLang, languageAliasesEmpty, sitePrefixPath);
    }

    private PathUrlLanguagePatternHandler create(String sitePrefixPath) {
        return create(this.english, sitePrefixPath);
    }

    private PathUrlLanguagePatternHandler createWithAliases(Lang defaultLanguage, String sitePrefixPath) {
        this.defaultLang = defaultLanguage;

        Map<Lang, String> langCodeAliasSetting = new LinkedHashMap<Lang, String>();
        langCodeAliasSetting.put(this.english, "us");
        langCodeAliasSetting.put(this.japanese, "japan");
        LanguageAliases languageAliasesConfigured = new LanguageAliases(this.supportedLangs, langCodeAliasSetting, this.defaultLang);

        return new PathUrlLanguagePatternHandler(this.defaultLang, languageAliasesConfigured, sitePrefixPath);
    }

    private PathUrlLanguagePatternHandler createWithAliases(String sitePrefixPath) {
        return createWithAliases(this.english, sitePrefixPath);
    }

    public void testGetLang__NonMatchingPath__ReturnDefaultLang() {
        PathUrlLanguagePatternHandler sut = create("");
        assertEquals(this.defaultLang, sut.getLang(""));
        assertEquals(this.defaultLang, sut.getLang("/"));
        assertEquals(this.defaultLang, sut.getLang("?query"));
        assertEquals(this.defaultLang, sut.getLang("/page"));
        assertEquals(this.defaultLang, sut.getLang("site.com/page/index.html"));
        assertEquals(this.defaultLang, sut.getLang("en.site.com/pre/fix/index.html"));
        assertEquals(this.defaultLang, sut.getLang("/page?wovn=en"));
        assertEquals(this.defaultLang, sut.getLang("site.com/French/"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com/Suomi/page/index.html"));
    }

    public void testGetLang__MatchingPath__ValidSupportedLang__ReturnTargetLangObject() {
        PathUrlLanguagePatternHandler sut = create("");
        assertEquals(this.french, sut.getLang("/fr"));
        assertEquals(this.french, sut.getLang("/fr/"));
        assertEquals(this.french, sut.getLang("/fr?wovn=en"));
        assertEquals(this.french, sut.getLang("/fr/?wovn=en"));
        assertEquals(this.french, sut.getLang("http://site.com/fr/page"));
        assertEquals(this.french, sut.getLang("https://site.com/fr/page/index.html"));
        assertEquals(this.french, sut.getLang("en.site.com/fr/page/index.html?wovn=es"));
    }

    public void testGetLang__MatchingPath__ValidSupportedLangRegionalLang__ReturnTargetLangObject() {
        PathUrlLanguagePatternHandler sut = create("");
        assertEquals(this.chinese, sut.getLang("/zh-CHT"));
        assertEquals(this.chinese, sut.getLang("/zh-CHT/"));
        assertEquals(this.chinese, sut.getLang("/zh-CHT?wovn=en"));
        assertEquals(this.chinese, sut.getLang("/zh-CHT/?wovn=en"));
        assertEquals(this.chinese, sut.getLang("http://site.com/zh-CHT/page"));
        assertEquals(this.chinese, sut.getLang("https://site.com/zh-CHT/page/index.html"));
        assertEquals(this.chinese, sut.getLang("en.site.com/zh-CHT/page/index.html?wovn=es"));
    }

    public void testGetLang__MatchingPath__NotSupportedLang__ReturnDefaultLang() {
        PathUrlLanguagePatternHandler sut = create("");
        assertEquals(this.defaultLang, sut.getLang("/no"));
        assertEquals(this.defaultLang, sut.getLang("/sv/"));
        assertEquals(this.defaultLang, sut.getLang("/pl?wovn=en"));
        assertEquals(this.defaultLang, sut.getLang("/th/?wovn=en"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com/vi/page"));
        assertEquals(this.defaultLang, sut.getLang("https://site.com/es/page/index.html"));
        assertEquals(this.defaultLang, sut.getLang("en.site.com/it/page/index.html?wovn=es"));
    }

    public void testGetLang__HasLanguageAliases__NonMatchingPath__ReturnNull() {
        PathUrlLanguagePatternHandler sut = createWithAliases("");
        assertEquals(null, sut.getLang(""));
        assertEquals(null, sut.getLang("/"));
        assertEquals(null, sut.getLang("?query"));
        assertEquals(null, sut.getLang("/page"));
        assertEquals(null, sut.getLang("site.com/page/index.html"));
        assertEquals(null, sut.getLang("en.site.com/pre/fix/index.html"));
        assertEquals(null, sut.getLang("/page?wovn=en"));
        assertEquals(null, sut.getLang("site.com/French/"));
        assertEquals(null, sut.getLang("http://site.com/Suomi/page/index.html"));
        assertEquals(null, sut.getLang("http://site.com/en/page/"));
        assertEquals(null, sut.getLang("http://site.com/ja/page/"));
        assertEquals(null, sut.getLang("http://site.com/th/page/"));
    }

    public void testGetLang__HasLanguageAliases__MatchingPath__ReturnTargetLangObject() {
        PathUrlLanguagePatternHandler sut = createWithAliases("");
        assertEquals(this.english, sut.getLang("http://site.com/us"));
        assertEquals(this.english, sut.getLang("http://site.com/us?u=Tom"));
        assertEquals(this.english, sut.getLang("http://site.com/us/page"));
        assertEquals(this.french, sut.getLang("http://site.com/fr/page"));
        assertEquals(this.japanese, sut.getLang("http://site.com/japan/page"));
        assertEquals(this.chinese, sut.getLang("http://site.com/zh-CHT/page"));
    }

    public void testGetLang__SitePrefixPath__NonMatchingPath__ReturnNull() {
        PathUrlLanguagePatternHandler sut = create("/pre/fix");
        assertEquals(null, sut.getLang("site.com/fr"));
        assertEquals(null, sut.getLang("site.com/fr"));
        assertEquals(null, sut.getLang("en.site.com/en/?wovn=en"));
        assertEquals(null, sut.getLang("/es/pre/fix/page/index.html"));
        assertEquals(null, sut.getLang("/pre/fr/fix/page/index.html"));
        assertEquals(null, sut.getLang("/pre/en/fix/page/index.html"));
        assertEquals(null, sut.getLang("https://en.site.com/en/page/"));
    }

    public void testGetLang__SitePrefixPath__MatchingPath__ValidSupportedLang__ReturnTargetLangObject() {
        PathUrlLanguagePatternHandler sut = create("/pre/fix");
        assertEquals(this.french, sut.getLang("site.com/pre/fix/fr"));
        assertEquals(this.french, sut.getLang("site.com/pre/fix/fr/"));
        assertEquals(this.french, sut.getLang("site.com/pre/fix/fr?query"));
        assertEquals(this.french, sut.getLang("site.com/pre/fix/fr/?query"));
        assertEquals(this.french, sut.getLang("en.site.com/pre/fix/fr/index.html?wovn=es"));
        assertEquals(this.french, sut.getLang("/pre/fix/fr/index.html"));
        assertEquals(this.french, sut.getLang("/pre/fix/fr/page/index.html"));
        assertEquals(this.french, sut.getLang("https://en.site.com/pre/fix/fr/page/"));
    }

    public void testGetLang__SitePrefixPath__MatchingPath__ValidSupportedLang__ReturnTargetLangObjectOfChinese() {
        PathUrlLanguagePatternHandler sut = create("/pre/fix");
        assertEquals(this.chinese, sut.getLang("site.com/pre/fix/zh-CHT"));
        assertEquals(this.chinese, sut.getLang("site.com/pre/fix/zh-CHT/"));
        assertEquals(this.chinese, sut.getLang("site.com/pre/fix/zh-CHT?query"));
        assertEquals(this.chinese, sut.getLang("site.com/pre/fix/zh-CHT/?query"));
        assertEquals(this.chinese, sut.getLang("en.site.com/pre/fix/zh-CHT/index.html?wovn=es"));
        assertEquals(this.chinese, sut.getLang("/pre/fix/zh-CHT/index.html"));
        assertEquals(this.chinese, sut.getLang("/pre/fix/zh-CHT/page/index.html"));
        assertEquals(this.chinese, sut.getLang("https://en.site.com/pre/fix/zh-CHT/page/"));
    }

    public void testGetLang__SitePrefixPath__MatchingPath__NotSupportedLang__ReturnDefaultLang() {
        PathUrlLanguagePatternHandler sut = create("/pre/fix");
        assertEquals(this.defaultLang, sut.getLang("site.com/pre/fix/vi"));
        assertEquals(this.defaultLang, sut.getLang("https://en.site.com/pre/fix/th/page/"));
        assertEquals(this.defaultLang, sut.getLang("/pre/fix/page/en/index.html"));
        assertEquals(this.defaultLang, sut.getLang("/pre/fix/french/page/index.html"));
    }

    public void testGetLang__SitePrefixPath__HasLanguageAliases__NonMatchingPath__ReturnNull() {
        PathUrlLanguagePatternHandler sut = createWithAliases("/pre/fix");
        assertEquals(null, sut.getLang("/"));
        assertEquals(null, sut.getLang("site.com/pre/fix/"));
        assertEquals(null, sut.getLang("site.com/pre/fix/ja"));
        assertEquals(null, sut.getLang("site.com/pre/fix/en/?query"));
        assertEquals(null, sut.getLang("site.com/us/pre/fix/"));
        assertEquals(null, sut.getLang("https://site.com/ja/pre/fix/"));
        assertEquals(null, sut.getLang("https://site.com/japan/pre/fix/"));
    }

    public void testGetLang__SitePrefixPath__HasLanguageAliases__MatchingPath__ReturnLanguageObject() {
        PathUrlLanguagePatternHandler sut = createWithAliases("/pre/fix");
        assertEquals(this.french, sut.getLang("site.com/pre/fix/fr/"));
        assertEquals(this.english, sut.getLang("site.com/pre/fix/us?query"));
        assertEquals(this.japanese, sut.getLang("en.site.com/pre/fix/japan/index.html?wovn=es"));
        assertEquals(this.chinese, sut.getLang("en.site.com/pre/fix/zh-CHT/index.html?wovn=es"));
    }

    public void testConvertToDefaultLanguage__NonMatchingPath__DoNotModify() {
        PathUrlLanguagePatternHandler sut = create("");
        assertEquals("", sut.convertToDefaultLanguage(""));
        assertEquals("?query", sut.convertToDefaultLanguage("?query"));
        assertEquals("/", sut.convertToDefaultLanguage("/"));
        assertEquals("/?query", sut.convertToDefaultLanguage("/?query"));
        assertEquals("site.com", sut.convertToDefaultLanguage("site.com"));
        assertEquals("site.com?query", sut.convertToDefaultLanguage("site.com?query"));
        assertEquals("site.com/", sut.convertToDefaultLanguage("site.com/"));
        assertEquals("site.com/page/", sut.convertToDefaultLanguage("site.com/page/"));
        assertEquals("/global/en/page/", sut.convertToDefaultLanguage("/global/en/page/"));
        assertEquals("site.com/ru/page/", sut.convertToDefaultLanguage("site.com/ru/page/"));
        assertEquals("site.com/english/page/", sut.convertToDefaultLanguage("site.com/english/page/"));
        assertEquals("site.com/ru/ja/page/", sut.convertToDefaultLanguage("site.com/ru/ja/page/"));
        assertEquals("/global/page/ja/index.html", sut.convertToDefaultLanguage("/global/page/ja/index.html"));
        assertEquals("http://www.site.com/global/ja", sut.convertToDefaultLanguage("http://www.site.com/global/ja"));
        assertEquals("https://test.com/ru/path/", sut.convertToDefaultLanguage("https://test.com/ru/path/"));
    }

    public void testConvertToDefaultLanguage__MatchingSupportedLang__RemoveLangCode() {
        PathUrlLanguagePatternHandler sut = create("");
        assertEquals("", sut.convertToDefaultLanguage("/ja"));
        assertEquals("", sut.convertToDefaultLanguage("/zh-CHT"));
        assertEquals("/", sut.convertToDefaultLanguage("/ja/"));
        assertEquals("/", sut.convertToDefaultLanguage("/zh-CHT/"));
        assertEquals("?query", sut.convertToDefaultLanguage("/ja?query"));
        assertEquals("?query", sut.convertToDefaultLanguage("/zh-CHT?query"));
        assertEquals("/?query", sut.convertToDefaultLanguage("/ja/?query"));
        assertEquals("/?query", sut.convertToDefaultLanguage("/zh-CHT/?query"));
        assertEquals("site.com", sut.convertToDefaultLanguage("site.com/en"));
        assertEquals("site.com", sut.convertToDefaultLanguage("site.com/zh-CHT"));
        assertEquals("site.com/", sut.convertToDefaultLanguage("site.com/ja/"));
        assertEquals("site.com/", sut.convertToDefaultLanguage("site.com/zh-CHT/"));
        assertEquals("site.com/?query", sut.convertToDefaultLanguage("site.com/ja/?query"));
        assertEquals("site.com/?query", sut.convertToDefaultLanguage("site.com/zh-CHT/?query"));
        assertEquals("site.com/index.html", sut.convertToDefaultLanguage("site.com/fr/index.html"));
        assertEquals("site.com/index.html", sut.convertToDefaultLanguage("site.com/zh-CHT/index.html"));
        assertEquals("site.com/page/index.html", sut.convertToDefaultLanguage("site.com/en/page/index.html"));
        assertEquals("site.com/page/index.html", sut.convertToDefaultLanguage("site.com/zh-CHT/page/index.html"));
        assertEquals("/page/index.html", sut.convertToDefaultLanguage("/en/page/index.html"));
        assertEquals("/page/index.html", sut.convertToDefaultLanguage("/zh-CHT/page/index.html"));
        assertEquals("/page/index.html?query", sut.convertToDefaultLanguage("/en/page/index.html?query"));
        assertEquals("/page/index.html?query", sut.convertToDefaultLanguage("/zh-CHT/page/index.html?query"));
        assertEquals("site.com/en/page/", sut.convertToDefaultLanguage("site.com/ja/en/page/"));
        assertEquals("site.com/ja/page/", sut.convertToDefaultLanguage("site.com/ja/ja/page/"));
        assertEquals("site.com/zh-CHT/page/", sut.convertToDefaultLanguage("site.com/zh-CHT/zh-CHT/page/"));
        assertEquals("http://www.site.com", sut.convertToDefaultLanguage("http://www.site.com/ja"));
        assertEquals("http://www.site.com", sut.convertToDefaultLanguage("http://www.site.com/zh-CHT"));
        assertEquals("https://test.com/path/index.html", sut.convertToDefaultLanguage("https://test.com/en/path/index.html"));
        assertEquals("https://test.com/path/index.html", sut.convertToDefaultLanguage("https://test.com/zh-CHT/path/index.html"));
    }

    public void testConvertToDefaultLanguage__SitePrefixPath__NonMatchingPath__DoNotModify() {
        PathUrlLanguagePatternHandler sut = create("/pre/fix");
        assertEquals("/", sut.convertToDefaultLanguage("/"));
        assertEquals("site.com", sut.convertToDefaultLanguage("site.com"));
        assertEquals("site.com?query", sut.convertToDefaultLanguage("site.com?query"));
        assertEquals("site.com/pre/fix/", sut.convertToDefaultLanguage("site.com/pre/fix/"));
        assertEquals("site.com/pre/fix/#section", sut.convertToDefaultLanguage("site.com/pre/fix/#section"));
        assertEquals("site.com/no/index.html", sut.convertToDefaultLanguage("site.com/no/index.html"));
        assertEquals("site.com/no/index.html#section", sut.convertToDefaultLanguage("site.com/no/index.html#section"));
        assertEquals("site.com/no/index.html?query", sut.convertToDefaultLanguage("site.com/no/index.html?query"));
        assertEquals("site.com/fr/pre/fix/", sut.convertToDefaultLanguage("site.com/fr/pre/fix/"));
        assertEquals("site.com/pre/ja/fix/", sut.convertToDefaultLanguage("site.com/pre/ja/fix/"));
        assertEquals("site.com/prefix/fr", sut.convertToDefaultLanguage("site.com/prefix/fr"));
        assertEquals("/pre/fix/page/en/index.html", sut.convertToDefaultLanguage("/pre/fix/page/en/index.html"));
        assertEquals("/pre/fix/ru/page/index.html", sut.convertToDefaultLanguage("/pre/fix/ru/page/index.html"));
        assertEquals("http://www.site.com/ja", sut.convertToDefaultLanguage("http://www.site.com/ja"));
    }

    public void testConvertToDefaultLanguage__SitePrefixPath__MatchingSupportedLang__RemoveLangCode() {
        PathUrlLanguagePatternHandler sut = create("/pre/fix");
        assertEquals("/pre/fix", sut.convertToDefaultLanguage("/pre/fix/ja"));
        assertEquals("/pre/fix", sut.convertToDefaultLanguage("/pre/fix/zh-CHT"));
        assertEquals("/pre/fix?query", sut.convertToDefaultLanguage("/pre/fix/ja?query"));
        assertEquals("/pre/fix?query", sut.convertToDefaultLanguage("/pre/fix/zh-CHT?query"));
        assertEquals("/pre/fix/", sut.convertToDefaultLanguage("/pre/fix/ja/"));
        assertEquals("/pre/fix/", sut.convertToDefaultLanguage("/pre/fix/zh-CHT/"));
        assertEquals("/pre/fix/?query", sut.convertToDefaultLanguage("/pre/fix/ja/?query"));
        assertEquals("/pre/fix/?query", sut.convertToDefaultLanguage("/pre/fix/zh-CHT/?query"));
        assertEquals("http://site.com/pre/fix/", sut.convertToDefaultLanguage("http://site.com/pre/fix/en/"));
        assertEquals("http://site.com/pre/fix/", sut.convertToDefaultLanguage("http://site.com/pre/fix/zh-CHT/"));
        assertEquals("site.com/pre/fix/page/index.html", sut.convertToDefaultLanguage("site.com/pre/fix/fr/page/index.html"));
        assertEquals("site.com/pre/fix/page/index.html", sut.convertToDefaultLanguage("site.com/pre/fix/zh-CHT/page/index.html"));
        assertEquals("site.com/pre/fix/page/index.html?query", sut.convertToDefaultLanguage("site.com/pre/fix/fr/page/index.html?query"));
        assertEquals("site.com/pre/fix/page/index.html?query", sut.convertToDefaultLanguage("site.com/pre/fix/zh-CHT/page/index.html?query"));
        assertEquals("http://www.site.com/pre/fix", sut.convertToDefaultLanguage("http://www.site.com/pre/fix/ja"));
        assertEquals("http://www.site.com/pre/fix", sut.convertToDefaultLanguage("http://www.site.com/pre/fix/zh-CHT"));
    }

    public void testConvertToDefaultLanguage__HasLanguageAliases__NonMatchingPath__DoNotModify() {
        PathUrlLanguagePatternHandler sut = createWithAliases("");
        assertEquals("/ja", sut.convertToDefaultLanguage("/ja"));
        assertEquals("/ja/", sut.convertToDefaultLanguage("/ja/"));
        assertEquals("http://site.com/en/page/", sut.convertToDefaultLanguage("http://site.com/en/page/"));
        assertEquals("http://site.com/th/page/", sut.convertToDefaultLanguage("http://site.com/th/page/"));
    }

    public void testConvertToDefaultLanguage__HasLanguageAliases__MatchingPath__InsertDefaultLanguageAlias() {
        PathUrlLanguagePatternHandler sut = createWithAliases("");
        assertEquals("/us?query", sut.convertToDefaultLanguage("/japan?query"));
        assertEquals("/us/?query", sut.convertToDefaultLanguage("/japan/?query"));
        assertEquals("http://site.com/us/page/", sut.convertToDefaultLanguage("http://site.com/us/page/"));
        assertEquals("http://site.com/us/page/", sut.convertToDefaultLanguage("http://site.com/japan/page/"));
        assertEquals("http://site.com/us/page/", sut.convertToDefaultLanguage("http://site.com/fr/page/"));
    }

    public void testConvertToDefaultLanguage__SitePrefixPath__HasLanguageAliases__NonMatchingPath__DoNotModify() {
        PathUrlLanguagePatternHandler sut = createWithAliases("/pre/fix");
        assertEquals("/pre/fix/ja", sut.convertToDefaultLanguage("/pre/fix/ja"));
        assertEquals("http://site.com/pre/fix/en/page/", sut.convertToDefaultLanguage("http://site.com/pre/fix/en/page/"));
        assertEquals("http://site.com/pre/fix/th/page/", sut.convertToDefaultLanguage("http://site.com/pre/fix/th/page/"));
        assertEquals("http://site.com/us/pre/fix/page/", sut.convertToDefaultLanguage("http://site.com/us/pre/fix/page/"));
        assertEquals("http://site.com/japan/pre/fix/page/", sut.convertToDefaultLanguage("http://site.com/japan/pre/fix/page/"));
    }

    public void testConvertToDefaultLanguage__SitePrefixPath__HasLanguageAliases__MatchingPath__InsertDefaultLanguageAlias() {
        PathUrlLanguagePatternHandler sut = createWithAliases("/pre/fix");
        assertEquals("/pre/fix/us", sut.convertToDefaultLanguage("/pre/fix/japan"));
        assertEquals("/pre/fix/us?query", sut.convertToDefaultLanguage("/pre/fix/fr?query"));
        assertEquals("http://site.com/pre/fix/us/page/", sut.convertToDefaultLanguage("http://site.com/pre/fix/us/page/"));
        assertEquals("http://site.com/pre/fix/us/page/", sut.convertToDefaultLanguage("http://site.com/pre/fix/japan/page/"));
        assertEquals("http://site.com/pre/fix/us/page/", sut.convertToDefaultLanguage("http://site.com/pre/fix/fr/page/"));
    }

    public void testConvertToTargetLanguage__DefaultSettings() {
        PathUrlLanguagePatternHandler sut = create("");
        assertEquals("/ja", sut.convertToTargetLanguage("", this.japanese));
        assertEquals("/zh-CHT", sut.convertToTargetLanguage("", this.chinese));
        assertEquals("/ja/", sut.convertToTargetLanguage("/", this.japanese));
        assertEquals("/ja/path/index.html", sut.convertToTargetLanguage("/path/index.html", this.japanese));
        assertEquals("site.com/ja/", sut.convertToTargetLanguage("site.com/", this.japanese));
        assertEquals("site.com/ja/", sut.convertToTargetLanguage("site.com/ja/", this.japanese));
        assertEquals("site.com/ja/", sut.convertToTargetLanguage("site.com/fr/", this.japanese));
        assertEquals("site.com/ja/ru/", sut.convertToTargetLanguage("site.com/ru/", this.japanese));
        assertEquals("http://site.com/ja/home", sut.convertToTargetLanguage("http://site.com/home", this.japanese));
        assertEquals("https://fr.site.co.uk/ja?query", sut.convertToTargetLanguage("https://fr.site.co.uk?query", this.japanese));
    }

    public void testConvertToTargetLanguage__SitePrefixPath__MatchingPath__ConvertUrl() {
        PathUrlLanguagePatternHandler sut = create("/pre/fix");
        assertEquals("/pre/fix/ja", sut.convertToTargetLanguage("/pre/fix", this.japanese));
        assertEquals("/pre/fix/zh-CHT", sut.convertToTargetLanguage("/pre/fix", this.chinese));
        assertEquals("/pre/fix/ja/", sut.convertToTargetLanguage("/pre/fix/", this.japanese));
        assertEquals("/pre/fix/ja/path/index.html", sut.convertToTargetLanguage("/pre/fix/path/index.html", this.japanese));
        assertEquals("site.com/pre/fix/ja/", sut.convertToTargetLanguage("site.com/pre/fix/", this.japanese));
        assertEquals("http://site.com/pre/fix/ja?query", sut.convertToTargetLanguage("http://site.com/pre/fix?query", this.japanese));
    }

    public void testConvertToTargetLanguage__SitePrefixPath__NonMatchingPath__DoNotModify() {
        PathUrlLanguagePatternHandler sut = create("/pre/fix");
        assertEquals("", sut.convertToTargetLanguage("", this.japanese));
        assertEquals("/", sut.convertToTargetLanguage("/", this.japanese));
        assertEquals("/path/index.html", sut.convertToTargetLanguage("/path/index.html", this.japanese));
        assertEquals("site.com/", sut.convertToTargetLanguage("site.com/", this.japanese));
        assertEquals("http://site.com/home", sut.convertToTargetLanguage("http://site.com/home", this.japanese));
        assertEquals("https://fr.site.co.uk?query", sut.convertToTargetLanguage("https://fr.site.co.uk?query", this.japanese));
    }

    public void testConvertToTargetLanguage__HasLanguageAliases__NonMatchingPath__DoNotModify() {
        PathUrlLanguagePatternHandler sut = createWithAliases("");
        assertEquals("http://site.com", sut.convertToTargetLanguage("http://site.com", this.japanese));
        assertEquals("http://site.com/", sut.convertToTargetLanguage("http://site.com/", this.japanese));
        assertEquals("http://site.com/page/index.html", sut.convertToTargetLanguage("http://site.com/page/index.html", this.japanese));
        assertEquals("http://site.com/en/page/index.html", sut.convertToTargetLanguage("http://site.com/en/page/index.html", this.japanese));
    }

    public void testConvertToTargetLanguage__HasLanguageAliases__MatchingPath__ConvertUrl() {
        PathUrlLanguagePatternHandler sut = createWithAliases("");
        assertEquals("http://site.com/japan", sut.convertToTargetLanguage("http://site.com/us", this.japanese));
        assertEquals("http://site.com/japan/", sut.convertToTargetLanguage("http://site.com/fr/", this.japanese));
        assertEquals("http://site.com/japan/page/index.html", sut.convertToTargetLanguage("http://site.com/japan/page/index.html", this.japanese));
    }

    public void testConvertToTargetLanguage__SitePrefixPath__HasLanguageAliases__NonMatchingPath__DoNotModify() {
        PathUrlLanguagePatternHandler sut = createWithAliases("/pre/fix");
        assertEquals("http://site.com/us/", sut.convertToTargetLanguage("http://site.com/us/", this.japanese));
        assertEquals("http://site.com/en/", sut.convertToTargetLanguage("http://site.com/en/", this.japanese));
        assertEquals("http://site.com/pre/fix/en/index.html", sut.convertToTargetLanguage("http://site.com/pre/fix/en/index.html", this.japanese));
        assertEquals("http://site.com/pre/fix/ja/index.html", sut.convertToTargetLanguage("http://site.com/pre/fix/ja/index.html", this.japanese));
    }

    public void testConvertToTargetLanguage__SitePrefixPath__HasLanguageAliases__MatchingPath__ConvertUrl() {
        PathUrlLanguagePatternHandler sut = createWithAliases("/pre/fix");
        assertEquals("http://site.com/pre/fix/japan", sut.convertToTargetLanguage("http://site.com/pre/fix/us", this.japanese));
        assertEquals("http://site.com/pre/fix/japan/", sut.convertToTargetLanguage("http://site.com/pre/fix/fr/", this.japanese));
        assertEquals("http://site.com/pre/fix/japan/page/index.html", sut.convertToTargetLanguage("http://site.com/pre/fix/japan/page/index.html", this.japanese));
    }

    public void testShouldRedirectExplicitDefaultLangUrl() {
        PathUrlLanguagePatternHandler sut = create("");
        assertEquals(true, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/en"));
        assertEquals(true, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/en/"));
        assertEquals(true, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/en/home"));

        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/ja"));
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/ja/home"));
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/path/en/home"));
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://en.site.com/home"));
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/home?wovn=en"));
    }

    public void testShouldRedirectExplicitDefaultLangUrl__HasLanguageAliases() {
        PathUrlLanguagePatternHandler sut = createWithAliases("");
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/en"));
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/en/"));
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/us"));
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/us/"));
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/page"));
        assertEquals(false, sut.shouldRedirectExplicitDefaultLangUrl("http://site.com/ja"));
    }
}
