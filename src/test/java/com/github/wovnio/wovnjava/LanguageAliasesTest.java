package com.github.wovnio.wovnjava;

import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class LanguageAliasesTest extends TestCase {
    private Lang english;
    private Lang italian;
    private Lang russian;
    private Lang chinese;

    private ArrayList<Lang> supportedLangs;
    private LanguageAliases sut;

    protected void setUp() throws Exception {
        this.english = Lang.get("en");
        this.italian = Lang.get("it");
        this.russian = Lang.get("ru");
        this.chinese = Lang.get("zh-cht");

        this.supportedLangs = new ArrayList<Lang>();
        this.supportedLangs.add(this.english);
        this.supportedLangs.add(this.italian);
        this.supportedLangs.add(this.russian);
        this.supportedLangs.add(this.chinese);
    }

    public void testNoLangCodeAliases__IdentifyLanguageByLangCode() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        this.sut = new LanguageAliases(this.supportedLangs, langCodeAliases, this.english);

        assertEquals(this.english, sut.getLanguageFromAlias("en"));
        assertEquals(this.italian, sut.getLanguageFromAlias("it"));
        assertEquals(this.russian, sut.getLanguageFromAlias("ru"));
        assertEquals(this.chinese, sut.getLanguageFromAlias("zh-CHT"));

        assertEquals("en", sut.getAliasFromLanguage(this.english));
        assertEquals("it", sut.getAliasFromLanguage(this.italian));
        assertEquals("ru", sut.getAliasFromLanguage(this.russian));
        assertEquals("zh-CHT", sut.getAliasFromLanguage(this.chinese));

        assertEquals(false, sut.hasAliasForDefaultLang);
    }

    public void testHasLangCodeAliases__IdentifyLanguageByAlias() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        langCodeAliases.put(this.english, "us");
        langCodeAliases.put(this.italian, "italy");
        this.sut = new LanguageAliases(this.supportedLangs, langCodeAliases, this.english);

        assertEquals(null, sut.getLanguageFromAlias("en"));
        assertEquals(null, sut.getLanguageFromAlias("it"));

        assertEquals(this.english, sut.getLanguageFromAlias("us"));
        assertEquals(this.italian, sut.getLanguageFromAlias("italy"));
        assertEquals(this.russian, sut.getLanguageFromAlias("ru"));
        assertEquals(this.chinese, sut.getLanguageFromAlias("zh-CHT"));

        assertEquals("us", sut.getAliasFromLanguage(this.english));
        assertEquals("italy", sut.getAliasFromLanguage(this.italian));
        assertEquals("ru", sut.getAliasFromLanguage(this.russian));
        assertEquals("zh-CHT", sut.getAliasFromLanguage(this.chinese));

        assertEquals(true, sut.hasAliasForDefaultLang);
    }

    public void testNoLangCodeAliases__IdentifyLanguageByLangCode__IgnoreCasesensitive() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        this.sut = new LanguageAliases(this.supportedLangs, langCodeAliases, this.english);

        assertEquals(this.english, sut.getLanguageFromAlias("En", true));
        assertEquals(this.italian, sut.getLanguageFromAlias("iT", true));
        assertEquals(this.russian, sut.getLanguageFromAlias("RU", true));
        assertEquals(this.chinese, sut.getLanguageFromAlias("zh-cht", true));
        assertEquals(this.chinese, sut.getLanguageFromAlias("zh-CHT", true));
        assertNotEquals(this.chinese, sut.getLanguageFromAlias("zh-cht", false));

        assertEquals("en", sut.getAliasFromLanguage(this.english));
        assertEquals("it", sut.getAliasFromLanguage(this.italian));
        assertEquals("ru", sut.getAliasFromLanguage(this.russian));
        assertEquals("zh-CHT", sut.getAliasFromLanguage(this.chinese));

        assertEquals(false, sut.hasAliasForDefaultLang);
    }

    public void testNullInput__ReturnNull() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        this.sut = new LanguageAliases(this.supportedLangs, langCodeAliases, this.english);

        assertEquals(null, sut.getLanguageFromAlias(null));
        assertEquals(null, sut.getAliasFromLanguage(null));
    }
}
