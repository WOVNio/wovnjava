package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import java.util.Map;

public class LangTest extends TestCase {
    public void testGetLang__validCode() {
        Lang english = Lang.get("en");
        assertEquals("en", english.code);
        assertEquals("en", english.codeISO639_1);
    }

    public void testGetLang__validCode_capitalLetters() {
        Lang japanese = Lang.get("JA");
        assertEquals("ja", japanese.code);
        assertEquals("ja", japanese.codeISO639_1);
    }

    public void testGetLang__invalidCode() {
        assertEquals(null, Lang.get("jp"));
        assertEquals(null, Lang.get(""));
    }

    public void testGetLang__null() {
        assertEquals(null, Lang.get(null));
    }

    public void testGetLang__differentHreflangCode() {
        Lang traditionalChinese = Lang.get("zh-CHT");
        assertEquals("zh-CHT", traditionalChinese.code);
        assertEquals("zh-Hant", traditionalChinese.codeISO639_1);

        Lang simplifiedChinese = Lang.get("zh-chs");
        assertEquals("zh-CHS", simplifiedChinese.code);
        assertEquals("zh-Hans", simplifiedChinese.codeISO639_1);
    }
}
