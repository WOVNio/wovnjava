package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import java.util.Map;

public class LangTest extends TestCase {
    public void testGetLang__validCode() {
        Lang english = Lang.get("en");
        assertEquals("en", english.code);
        assertEquals("en", english.hreflangCode);
    }

    public void testGetLang__validCode_capitalLetters() {
        Lang japanese = Lang.get("JA");
        assertEquals("ja", japanese.code);
        assertEquals("ja", japanese.hreflangCode);
    }

    public void testGetLang__invalidCode() {
        assertEquals(null, Lang.get("jp"));
    }

    public void testGetLang__null() {
        assertEquals(null, Lang.get(null));
    }

    public void testGetLang__differentHreflangCode() {
        Lang traditionalChinese = Lang.get("zh-CHT");
        assertEquals("zh-CHT", traditionalChinese.code);
        assertEquals("zh-Hant", traditionalChinese.hreflangCode);

        Lang simplifiedChinese = Lang.get("zh-chs");
        assertEquals("zh-CHS", simplifiedChinese.code);
        assertEquals("zh-Hans", simplifiedChinese.hreflangCode);
    }
}
