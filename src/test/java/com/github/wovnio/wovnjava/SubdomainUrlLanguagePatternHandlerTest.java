package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class SubdomainUrlLanguagePatternHandlerTest extends TestCase {
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

    private SubdomainUrlLanguagePatternHandler create(Lang defaultLanguage) {
        this.defaultLang = defaultLanguage;

        Map<Lang, String> langCodeAliasSetting = new LinkedHashMap<Lang, String>();
        LanguageAliases languageAliasesEmpty = new LanguageAliases(this.supportedLangs, langCodeAliasSetting,
                this.defaultLang);

        return new SubdomainUrlLanguagePatternHandler(this.defaultLang, languageAliasesEmpty);
    }

    private SubdomainUrlLanguagePatternHandler createWithAliases(Lang defaultLanguage) {
        this.defaultLang = defaultLanguage;

        Map<Lang, String> langCodeAliasSetting = new LinkedHashMap<Lang, String>();
        langCodeAliasSetting.put(this.english, "us");
        langCodeAliasSetting.put(this.japanese, "japan");
        LanguageAliases languageAliasesConfigured = new LanguageAliases(this.supportedLangs, langCodeAliasSetting,
                this.defaultLang);

        return new SubdomainUrlLanguagePatternHandler(this.defaultLang, languageAliasesConfigured);
    }

    public void testGetLang__NonMatchingSubdomain__ReturnDefaultLang() {
        SubdomainUrlLanguagePatternHandler sut = create(this.english);
        assertEquals(this.defaultLang, sut.getLang("/"));
        assertEquals(this.defaultLang, sut.getLang("/en"));
        assertEquals(this.defaultLang, sut.getLang("/en/page"));
        assertEquals(this.defaultLang, sut.getLang("site.com/page/index.html"));
        assertEquals(this.defaultLang, sut.getLang("site.com/en/pre/fix/index.html"));
        assertEquals(this.defaultLang, sut.getLang("/page?language=en&wovn=fr"));
        assertEquals(this.defaultLang, sut.getLang("deutsch.site.com/page"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com"));
    }

    public void testGetLang__NonMatchingSubdomain__ReturnDefaultLangByChinese() {
        SubdomainUrlLanguagePatternHandler sut = create(this.chinese);
        assertEquals(this.defaultLang, sut.getLang("/"));
        assertEquals(this.defaultLang, sut.getLang("/zh-CHT"));
        assertEquals(this.defaultLang, sut.getLang("/zh-CHT/page"));
        assertEquals(this.defaultLang, sut.getLang("site.com/page/index.html"));
        assertEquals(this.defaultLang, sut.getLang("site.com/zh-CHT/pre/fix/index.html"));
        assertEquals(this.defaultLang, sut.getLang("/page?language=zh-CHT&wovn=fr"));
        assertEquals(this.defaultLang, sut.getLang("deutsch.site.com/page"));
        assertEquals(this.defaultLang, sut.getLang("http://site.com"));
    }

    public void testGetLang__MatchingSubdomain__ValidSupportedLang__ReturnTargetLangObject() {
        SubdomainUrlLanguagePatternHandler sut = create(this.english);
        assertEquals(this.english, sut.getLang("en.site.com"));
        assertEquals(this.chinese, sut.getLang("zh-CHT.site.com"));
        assertEquals(this.chinese, sut.getLang("zh-cht.site.com"));
        assertEquals(this.japanese, sut.getLang("ja.site.com/"));
        assertEquals(this.french, sut.getLang("fr.site.com/en/page/index.html?lang=it&wovn=en"));
        assertEquals(this.french, sut.getLang("http://fr.site.com/"));
        assertEquals(this.japanese, sut.getLang("https://ja.site.com?wovn=fr"));
    }

    public void testGetLang__MatchingSubdomain__NotSupportedLang__ReturnDefaultLang() {
        SubdomainUrlLanguagePatternHandler sut = create(this.english);
        assertEquals(this.defaultLang, sut.getLang("th.site.com"));
        assertEquals(this.defaultLang, sut.getLang("es.site.com/"));
        assertEquals(this.defaultLang, sut.getLang("sv.site.com/en/page/index.html?lang=it&wovn=en"));
        assertEquals(this.defaultLang, sut.getLang("http://it.site.com/"));
        assertEquals(this.defaultLang, sut.getLang("https://vi.site.com?wovn=fr"));
    }

    public void testGetLang__HasAliasForDefaultLang__NonMatchingSubdomain__ReturnNull() {
        SubdomainUrlLanguagePatternHandler sut = createWithAliases(this.english);
        assertEquals(null, sut.getLang("/"));
        assertEquals(null, sut.getLang("/en"));
        assertEquals(null, sut.getLang("/en/page"));
        assertEquals(null, sut.getLang("site.com/page/index.html"));
        assertEquals(null, sut.getLang("site.com/en/pre/fix/index.html"));
        assertEquals(null, sut.getLang("/page?language=en&wovn=fr"));
        assertEquals(null, sut.getLang("deutsch.site.com/page"));
        assertEquals(null, sut.getLang("http://site.com"));
        assertEquals(null, sut.getLang("http://en.site.com"));
        assertEquals(null, sut.getLang("http://ja.site.com"));
    }

    public void testGetLang__HasLanguageAliases__MatchingSubdomain__ReturnTargetLangObject() {
        SubdomainUrlLanguagePatternHandler sut = createWithAliases(this.english);
        assertEquals(this.english, sut.getLang("us.site.com"));
        assertEquals(this.japanese, sut.getLang("japan.site.com/"));
        assertEquals(this.french, sut.getLang("fr.site.com/en/page/index.html?lang=it&wovn=en"));
        assertEquals(this.french, sut.getLang("http://fr.site.com/"));
        assertEquals(this.japanese, sut.getLang("https://japan.site.com?wovn=fr"));
        assertEquals(this.chinese, sut.getLang("https://zh-CHT.site.com?wovn=fr"));
    }

    public void testConvertToDefaultLanguage__NonMatchingSubdomain__DoNotModify() {
        SubdomainUrlLanguagePatternHandler sut = create(this.english);
        assertEquals("/", sut.convertToDefaultLanguage("/"));
        assertEquals("/en/path/index.php", sut.convertToDefaultLanguage("/en/path/index.php"));
        assertEquals("?lang=english", sut.convertToDefaultLanguage("?lang=english"));
        assertEquals("site.com", sut.convertToDefaultLanguage("site.com"));
        assertEquals("ru.site.com", sut.convertToDefaultLanguage("ru.site.com"));
        assertEquals("https://ru.fr.site.com", sut.convertToDefaultLanguage("https://ru.fr.site.com"));
        assertEquals("site.com/fr/index.html?wovn=fr", sut.convertToDefaultLanguage("site.com/fr/index.html?wovn=fr"));
    }

    public void testConvertToDefaultLanguage__MatchingSubdomain__RemoveLangCode() {
        SubdomainUrlLanguagePatternHandler sut = create(this.english);
        assertEquals("site.com", sut.convertToDefaultLanguage("en.site.com"));
        assertEquals("site.com", sut.convertToDefaultLanguage("zh-CHT.site.com"));
        assertEquals("site.com/", sut.convertToDefaultLanguage("fr.site.com/"));
        assertEquals("http://site.com/", sut.convertToDefaultLanguage("http://fr.site.com/"));
        assertEquals("site.com/fr/index.html?lang=fr&wovn=fr", sut.convertToDefaultLanguage("fr.site.com/fr/index.html?lang=fr&wovn=fr"));
    }

    public void testConvertToDefaultLanguage__HasAliasForDefaultLang__NonMatchingSubdomain__DoNotModify() {
        SubdomainUrlLanguagePatternHandler sut = createWithAliases(this.english);
        assertEquals("en.site.com", sut.convertToDefaultLanguage("en.site.com"));
        assertEquals("http://ja.site.com/", sut.convertToDefaultLanguage("http://ja.site.com/"));
        assertEquals("http://site.com/", sut.convertToDefaultLanguage("http://site.com/"));
    }

    public void testConvertToDefaultLanguage__HasAliasForDefaultLang__MatchingSubdomain__InsertDefaultLanguageAlias() {
        SubdomainUrlLanguagePatternHandler sut = createWithAliases(this.english);
        assertEquals("us.site.com/", sut.convertToDefaultLanguage("fr.site.com/"));
        assertEquals("http://us.site.com/", sut.convertToDefaultLanguage("http://fr.site.com/"));
        assertEquals("http://us.site.com/", sut.convertToDefaultLanguage("http://japan.site.com/"));
        assertEquals("http://us.site.com/", sut.convertToDefaultLanguage("http://us.site.com/"));
    }

    public void testConvertToTargetLanguage() {
        SubdomainUrlLanguagePatternHandler sut = create(this.english);
        assertEquals("/", sut.convertToTargetLanguage("/", this.japanese));
        assertEquals("/path/index.html", sut.convertToTargetLanguage("/path/index.html", this.japanese));
        assertEquals("ja.site.com?q=none", sut.convertToTargetLanguage("site.com?q=none", this.japanese));
        assertEquals("zh-CHT.site.com?q=none", sut.convertToTargetLanguage("site.com?q=none", this.chinese));
        assertEquals("http://ja.site.com?q=none", sut.convertToTargetLanguage("http://site.com?q=none", this.japanese));
        assertEquals("https://ja.user13.sub.site.co.jp/home", sut.convertToTargetLanguage("https://user13.sub.site.co.jp/home", this.japanese));
        assertEquals("ja.site.com", sut.convertToTargetLanguage("ja.site.com", this.japanese));
        assertEquals("ja.site.com", sut.convertToTargetLanguage("fr.site.com", this.japanese));
        assertEquals("ja.ru.site.com", sut.convertToTargetLanguage("ru.site.com", this.japanese));
    }

    public void testConvertToTargetLanguage__HasLanguageAliases() {
        SubdomainUrlLanguagePatternHandler sut = createWithAliases(this.english);
        assertEquals("/", sut.convertToTargetLanguage("/", this.japanese));
        assertEquals("/path/index.html", sut.convertToTargetLanguage("/path/index.html", this.japanese));
        assertEquals("site.com?q=none", sut.convertToTargetLanguage("site.com?q=none", this.japanese));
        assertEquals("japan.site.com?q=none", sut.convertToTargetLanguage("us.site.com?q=none", this.japanese));
        assertEquals("http://site.com?q=none", sut.convertToTargetLanguage("http://site.com?q=none", this.japanese));
        assertEquals("http://japan.site.com?q=none", sut.convertToTargetLanguage("http://fr.site.com?q=none", this.japanese));
        assertEquals("https://user13.sub.site.co.jp/home", sut.convertToTargetLanguage("https://user13.sub.site.co.jp/home", this.japanese));
        assertEquals("ja.site.com", sut.convertToTargetLanguage("ja.site.com", this.japanese));
        assertEquals("en.site.com", sut.convertToTargetLanguage("en.site.com", this.japanese));
        assertEquals("japan.site.com", sut.convertToTargetLanguage("fr.site.com", this.japanese));
        assertEquals("japan.site.com", sut.convertToTargetLanguage("japan.site.com", this.japanese));
        assertEquals("japan.site.com", sut.convertToTargetLanguage("us.site.com", this.japanese));
    }
}
