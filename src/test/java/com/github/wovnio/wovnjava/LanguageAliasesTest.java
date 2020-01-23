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

        assertEquals(this.english, sut.getLang("en"));
        assertEquals(this.italian, sut.getLang("it"));
        assertEquals(this.russian, sut.getLang("ru"));

        assertEquals("en", sut.getAlias(this.english));
        assertEquals("it", sut.getAlias(this.italian));
        assertEquals("ru", sut.getAlias(this.russian));

        assertEquals(false, sut.hasAliasForDefaultLang);
    }

    public void testHasLangCodeAliases__IdentifyLanguageByAlias() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        langCodeAliases.put(this.english, "us");
        langCodeAliases.put(this.italian, "italy");
        this.sut = new LanguageAliases(this.supportedLangs, langCodeAliases, this.english);

        assertEquals(null, sut.getLang("en"));
        assertEquals(null, sut.getLang("it"));

        assertEquals(this.english, sut.getLang("us"));
        assertEquals(this.italian, sut.getLang("italy"));
        assertEquals(this.russian, sut.getLang("ru"));

        assertEquals("us", sut.getAlias(this.english));
        assertEquals("italy", sut.getAlias(this.italian));
        assertEquals("ru", sut.getAlias(this.russian));

        assertEquals(true, sut.hasAliasForDefaultLang);
    }

    public void testNullInput__ReturnNull() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        this.sut = new LanguageAliases(this.supportedLangs, langCodeAliases, this.english);

        assertEquals(null, sut.getLang(null));
        assertEquals(null, sut.getAlias(null));
    }
}
