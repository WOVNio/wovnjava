package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import java.util.Map;

public class LangTest extends TestCase {
    public void testGetLang__validCode() {
        Lang english = Lang.getLang("en");
        assertEquals("en", english.code);
        assertEquals("en", english.hreflangCode);
    }

    public void testGetLang__validCode_capitalLetters() {
        Lang japanese = Lang.getLang("JA");
        assertEquals("ja", japanese.code);
        assertEquals("ja", japanese.hreflangCode);
    }

    public void testGetLang__invalidCode() {
        assertEquals(null, Lang.getLang("jp"));
    }

    public void testGetLang__null() {
        assertEquals(null, Lang.getLang(null));
    }

    public void testGetLang__differentHreflangCode() {
        Lang traditionalChinese = Lang.getLang("zh-CHT");
        assertEquals("zh-CHT", traditionalChinese.code);
        assertEquals("zh-Hant", traditionalChinese.hreflangCode);

        Lang simplifiedChinese = Lang.getLang("zh-chs");
        assertEquals("zh-CHS", simplifiedChinese.code);
        assertEquals("zh-Hans", simplifiedChinese.hreflangCode);
    }
}
