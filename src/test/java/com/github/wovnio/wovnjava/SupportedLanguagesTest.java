package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class SupportedLanguagesTest extends TestCase {
    private ArrayList<Lang> supportedLangs;
    private SupportedLanguages sut;

    protected void setUp() throws Exception {
        this.supportedLangs = new ArrayList<Lang>();
        this.supportedLangs.add(Lang.get("en"));
        this.supportedLangs.add(Lang.get("ja"));
        this.supportedLangs.add(Lang.get("fr"));
    }

    public void testGet__NoLangCodeAliases__IdentifyLanguageByLangCode() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        this.sut = new SupportedLanguages(this.supportedLangs, langCodeAliases, Lang.get("en"));

        SupportedLanguage eng = sut.get("en");
        assertEquals(Lang.get("en"), eng.lang);
        assertEquals("en", eng.identifier);
        assertEquals(true, eng.isDefaultLanguage);

        SupportedLanguage jap = sut.get("ja");
        assertEquals(Lang.get("ja"), jap.lang);
        assertEquals("ja", jap.identifier);
        assertEquals(false, jap.isDefaultLanguage);

        SupportedLanguage fre = sut.get("fr");
        assertEquals(Lang.get("fr"), fre.lang);
        assertEquals("fr", fre.identifier);
        assertEquals(false, fre.isDefaultLanguage);
    }

    public void testGet__HasLangCodeAliases__IdentifyLanguageByAlias() {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        langCodeAliases.put(Lang.get("en"), "english");
        langCodeAliases.put(Lang.get("ja"), "japan");
        this.sut = new SupportedLanguages(this.supportedLangs, langCodeAliases, Lang.get("en"));

        assertEquals(null, sut.get("en"));

        SupportedLanguage eng = sut.get("english");
        assertEquals(Lang.get("en"), eng.lang);
        assertEquals("english", eng.identifier);
        assertEquals(true, eng.isDefaultLanguage);

        assertEquals(null, sut.get("ja"));

        SupportedLanguage jap = sut.get("japan");
        assertEquals(Lang.get("ja"), jap.lang);
        assertEquals("japan", jap.identifier);
        assertEquals(false, jap.isDefaultLanguage);

        SupportedLanguage fre = sut.get("fr");
        assertEquals(Lang.get("fr"), fre.lang);
        assertEquals("fr", fre.identifier);
        assertEquals(false, fre.isDefaultLanguage);
    }
}
