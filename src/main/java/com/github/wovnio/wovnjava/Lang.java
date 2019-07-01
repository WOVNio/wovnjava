package com.github.wovnio.wovnjava;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Lang {
    private static final Map<String, Lang> LANG;
    static {
        HashMap<String, Lang> map = new HashMap<String, Lang>();
        map.put("ar",     new Lang("ar", "ar", "Arabic", "ﺎﻠﻋﺮﺒﻳﺓ"));
        map.put("bg",     new Lang("bg", "bg", "Bulgarian", "Български"));
        map.put("zh-chs", new Lang("zh-CHS", "zh-Hans", "Simp Chinese", "简体中文"));
        map.put("zh-cht", new Lang("zh-CHT", "zh-Hant", "Trad Chinese", "繁體中文"));
        map.put("da",     new Lang("da", "da", "Danish", "Dansk"));
        map.put("nl",     new Lang("nl", "nl", "Dutch", "Nederlands"));
        map.put("en",     new Lang("en", "en", "English", "English"));
        map.put("fi",     new Lang("fi", "fi", "Finnish", "Suomi"));
        map.put("fr",     new Lang("fr", "fr", "French", "Français"));
        map.put("de",     new Lang("de", "de", "German", "Deutsch"));
        map.put("el",     new Lang("el", "el", "Greek", "Ελληνικά"));
        map.put("he",     new Lang("he", "he", "Hebrew", "עברית"));
        map.put("id",     new Lang("id", "id", "Indonesian", "Bahasa Indonesia"));
        map.put("it",     new Lang("it", "it", "Italian", "Italiano"));
        map.put("ja",     new Lang("ja", "ja", "Japanese", "日本語"));
        map.put("ko",     new Lang("ko", "ko", "Korean", "한국어"));
        map.put("ms",     new Lang("ms", "ms", "Malay", "Bahasa Melayu"));
        map.put("my",     new Lang("my", "my", "Burmese", "ဗမာစာ"));
        map.put("ne",     new Lang("ne", "ne", "Nepali", "नेपाली भाषा"));
        map.put("no",     new Lang("no", "no", "Norwegian", "Norsk"));
        map.put("pl",     new Lang("pl", "pl", "Polish", "Polski"));
        map.put("pt",     new Lang("pt", "pt", "Portuguese", "Português"));
        map.put("ru",     new Lang("ru", "ru", "Russian", "Русский"));
        map.put("es",     new Lang("es", "es", "Spanish", "Español"));
        map.put("sv",     new Lang("sv", "sv", "Swedish", "Svensk"));
        map.put("th",     new Lang("th", "th", "Thai", "ภาษาไทย"));
        map.put("hi",     new Lang("hi", "hi", "Hindi", "हिन्दी"));
        map.put("tr",     new Lang("tr", "tr", "Turkish", "Türkçe"));
        map.put("uk",     new Lang("uk", "uk", "Ukrainian", "Українська"));
        map.put("vi",     new Lang("vi", "vi", "Vietnamese", "Tiếng Việt"));
        map.put("tl",     new Lang("tl", "tl", "Tagalog", "Tagalog"));
        LANG = Collections.unmodifiableMap(map);
    }

    String code;
    String hreflangCode;

    Lang(String code, String hreflangCode, String englishName, String nativeName) {
        this.code = code;
        this.hreflangCode = hreflangCode;
    }

    static Lang getLang(String langCode) {
        if (langCode == null) return null;

        return LANG.get(langCode.toLowerCase());
    }
}
