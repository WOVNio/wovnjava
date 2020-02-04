package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class LanguageAliasesTest extends TestCase {
    private Lang english;
    private Lang italian;
    private Lang russian;

    private ArrayList<Lang> supportedLangs;
    private LanguageAliases sut;

    protected void setUp() throws Exception {
        this.english = Lang.get("en");
        this.italian = Lang.get("it");
        this.russian = Lang.get("ru");

        this.supportedLangs = new ArrayList<Lang>();
        this.supportedLangs.add(this.english);
        this.supportedLangs.add(this.italian);
        this.supportedLangs.add(this.russian);
    }

    public void testNoLangCodeAliases__IdentifyLanguageByLangCode() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        this.sut = new LanguageAliases(this.supportedLangs, langCodeAliases, this.english);

        assertEquals(this.english, sut.getLanguageFromAlias("en"));
        assertEquals(this.italian, sut.getLanguageFromAlias("it"));
        assertEquals(this.russian, sut.getLanguageFromAlias("ru"));

        assertEquals("en", sut.getAliasFromLanguage(this.english));
        assertEquals("it", sut.getAliasFromLanguage(this.italian));
        assertEquals("ru", sut.getAliasFromLanguage(this.russian));

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

        assertEquals("us", sut.getAliasFromLanguage(this.english));
        assertEquals("italy", sut.getAliasFromLanguage(this.italian));
        assertEquals("ru", sut.getAliasFromLanguage(this.russian));

        assertEquals(true, sut.hasAliasForDefaultLang);
    }

    public void testNullInput__ReturnNull() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        this.sut = new LanguageAliases(this.supportedLangs, langCodeAliases, this.english);

        assertEquals(null, sut.getLanguageFromAlias(null));
        assertEquals(null, sut.getAliasFromLanguage(null));
    }
}
