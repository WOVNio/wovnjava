package com.github.wovnio.wovnjava;

import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Lang {
    static final Map<String, Lang> LANG;
    static {
        HashMap<String, Lang> map = new HashMap<String, Lang>();
        map.put("ar", new Lang("ﺎﻠﻋﺮﺒﻳﺓ", "ar", "Arabic"));
        map.put("bg", new Lang("Български", "bg", "Bulgarian"));
        map.put("zh-CHS", new Lang("简体中文", "zh-CHS", "Simp Chinese"));
        map.put("zh-CHT", new Lang("繁體中文", "zh-CHT", "Trad Chinese"));
        map.put("da", new Lang("Dansk", "da", "Danish"));
        map.put("nl", new Lang("Nederlands", "nl", "Dutch"));
        map.put("en", new Lang("English", "en", "English"));
        map.put("fi", new Lang("Suomi", "fi", "Finnish"));
        map.put("fr", new Lang("Français", "fr", "French"));
        map.put("de", new Lang("Deutsch", "de", "German"));
        map.put("el", new Lang("Ελληνικά", "el", "Greek"));
        map.put("he", new Lang("עברית", "he", "Hebrew"));
        map.put("id", new Lang("Bahasa Indonesia", "id", "Indonesian"));
        map.put("it", new Lang("Italiano", "it", "Italian"));
        map.put("ja", new Lang("日本語", "ja", "Japanese"));
        map.put("ko", new Lang("한국어", "ko", "Korean"));
        map.put("ms", new Lang("Bahasa Melayu", "ms", "Malay"));
        map.put("no", new Lang("Norsk", "no", "Norwegian"));
        map.put("pl", new Lang("Polski", "pl", "Polish"));
        map.put("pt", new Lang("Português", "pt", "Portuguese"));
        map.put("ru", new Lang("Русский", "ru", "Russian"));
        map.put("es", new Lang("Español", "es", "Spanish"));
        map.put("sv", new Lang("Svensk", "sv", "Swedish"));
        map.put("th", new Lang("ภาษาไทย", "th", "Thai"));
        map.put("hi", new Lang("हिन्दी", "hi", "Hindi"));
        map.put("tr", new Lang("Türkçe", "tr", "Turkish"));
        map.put("uk", new Lang("Українська", "uk", "Ukrainian"));
        map.put("vi", new Lang("Tiếng Việt", "vi", "Vietnamese"));
        LANG = Collections.unmodifiableMap(map);
    }

    String name;
    String code;
    String en;

    Lang(String n, String c, String e) {
        super();
        this.name = n;
        this.code = c;
        this.en = e;
    }

    @Contract("null -> null")
    static String getCode(String langName) {
        if (langName == null || langName.length() == 0) {
            return null;
        }
        if (LANG.get(langName) != null) {
            return langName;
        }
        for (Map.Entry<String, Lang> e : LANG.entrySet()) {
            Lang l = e.getValue();
            String langNameLC = langName.toLowerCase();
            if ( langNameLC.equals(l.name.toLowerCase())
                    || langNameLC.equals(l.en.toLowerCase())
                    || langNameLC.equals(l.code.toLowerCase())
                    ) {
                return l.code;
            }
        }
        return null;
    }

    static Lang getLang(String langName) {
        String langCode = getCode(langName);
        return LANG.get(langCode);
    }

    static String normalizeIso639_1(String langCode) {
        String langCodeLC = langCode.toLowerCase();
        if (langCodeLC.equals("zh-cht")) {
            return "zh-Hant";
        } else if (langCodeLC.equals("zh-chs")) {
            return "zh-Hans";
        } else {
            return langCode;
        }
    }
}
