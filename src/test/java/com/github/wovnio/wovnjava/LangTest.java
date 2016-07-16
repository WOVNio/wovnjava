package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import java.util.Map;

public class LangTest extends TestCase {

    public void testLangsExist() {
        assertNotNull(Lang.LANG);
    }

    public void testLangsSize() {
        assertEquals(28, Lang.LANG.size());
    }

    public void testKeyExist() {
        for (Map.Entry<String, Lang> e : Lang.LANG.entrySet()) {
            String k = e.getKey();
            Lang l = e.getValue();
            assertFalse(l.name == null || l.name.length() == 0);
            assertFalse(l.code == null || l.code.length() == 0);
            assertFalse(l.en == null || l.en.length() == 0);
            assertEquals(k, l.code);
        }
    }

    public void testLang() {
        Lang lang = new Lang("日本語", "ja", "Japanese");
        assertNotNull(lang);
        assertEquals("日本語", lang.name);
        assertEquals("ja", lang.code);
        assertEquals("Japanese", lang.en);
    }

    public void testGetCodeWithValidCode() {
        assertEquals("ms", Lang.getCode("ms"));
    }

    public void testGetCodeWithCapitalLetters() {
        assertEquals("zh-CHT", Lang.getCode("zh-cht"));
    }

    public void testGetCodeWithValidEnglishName() {
        assertEquals("pt", Lang.getCode("Portuguese"));
    }

    public void testGetCodeWithValidNativeName() {
        assertEquals("hi", Lang.getCode("हिन्दी"));
    }

    public void testGetCodeWithInvalidName() {
        assertEquals(null, Lang.getCode("WOVN4LYFE"));
    }

    public void testGetCodeWithEmptyString() {
        assertEquals(null, Lang.getCode(""));
    }

    public void testGetCodeWithNull() {
        assertEquals(null, Lang.getCode(null));
    }

    public void testGetLangWithValidCode() {
        assertSame(Lang.LANG.get("ms"), Lang.getLang("ms"));
    }

    public void testGetLangWithCapitalLetters() {
        assertSame(Lang.LANG.get("zh-CHT"), Lang.getLang("zh-cht"));
    }

    public void testGetLangWithValidEnglishName() {
        assertSame(Lang.LANG.get("pt"), Lang.getLang("Portuguese"));
    }

    public void testGetLangWithValidNativeName() {
        assertSame(Lang.LANG.get("hi"), Lang.getLang("हिन्दी"));
    }

    public void testGetLangWithInvalidName() {
        assertSame(null, Lang.getLang("WOVN4LYFE"));
    }

    public void testGetLangWithEmptyString() {
        assertSame(null, Lang.getLang(""));
    }

    public void testGetLangWithNull() {
        assertSame(null, Lang.getLang(null));
    }

    public void testNomalizeIso639_1() {
        assertEquals("ar",       Lang.normalizeIso639_1("ar"));
        assertEquals("bg",       Lang.normalizeIso639_1("bg"));

        assertEquals("zh-Hans",  Lang.normalizeIso639_1("zh-CHS"));
        assertEquals("zh-Hant",  Lang.normalizeIso639_1("zh-CHT"));

        assertEquals("zh-Hans",  Lang.normalizeIso639_1("zh-chs"));
        assertEquals("zh-Hant",  Lang.normalizeIso639_1("zh-cht"));

        assertEquals("da",       Lang.normalizeIso639_1("da"));
        assertEquals("nl",       Lang.normalizeIso639_1("nl"));
        assertEquals("en",       Lang.normalizeIso639_1("en"));
        assertEquals("fi",       Lang.normalizeIso639_1("fi"));
        assertEquals("fr",       Lang.normalizeIso639_1("fr"));
        assertEquals("de",       Lang.normalizeIso639_1("de"));
        assertEquals("el",       Lang.normalizeIso639_1("el"));
        assertEquals("he",       Lang.normalizeIso639_1("he"));
        assertEquals("id",       Lang.normalizeIso639_1("id"));
        assertEquals("it",       Lang.normalizeIso639_1("it"));
        assertEquals("ja",       Lang.normalizeIso639_1("ja"));
        assertEquals("ko",       Lang.normalizeIso639_1("ko"));
        assertEquals("ms",       Lang.normalizeIso639_1("ms"));
        assertEquals("no",       Lang.normalizeIso639_1("no"));
        assertEquals("pl",       Lang.normalizeIso639_1("pl"));
        assertEquals("pt",       Lang.normalizeIso639_1("pt"));
        assertEquals("ru",       Lang.normalizeIso639_1("ru"));
        assertEquals("es",       Lang.normalizeIso639_1("es"));
        assertEquals("sv",       Lang.normalizeIso639_1("sv"));
        assertEquals("th",       Lang.normalizeIso639_1("th"));
        assertEquals("hi",       Lang.normalizeIso639_1("hi"));
        assertEquals("tr",       Lang.normalizeIso639_1("tr"));
        assertEquals("uk",       Lang.normalizeIso639_1("uk"));
        assertEquals("vi",       Lang.normalizeIso639_1("vi"));
    }
}