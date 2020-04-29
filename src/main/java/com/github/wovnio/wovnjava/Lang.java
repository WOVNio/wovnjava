package com.github.wovnio.wovnjava;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Lang {
    private static final Map<String, Lang> LANG;
    static {
        HashMap<String, Lang> map = new HashMap<String, Lang>();
        map.put("ar",     new Lang("ar", "ar", "Arabic", "العربية"));
        map.put("eu",     new Lang("eu", "eu", "Basque", "Euskara"));
        map.put("bn",     new Lang("bn", "bn", "Bengali", "বাংলা ভাষা"));
        map.put("bg",     new Lang("bg", "bg", "Bulgarian", "Български"));
        map.put("ca",     new Lang("ca", "ca", "Catalan", "Català"));
        map.put("zh-chs", new Lang("zh-CHS", "zh-Hans", "Simp Chinese", "简体中文"));
        map.put("zh-cht", new Lang("zh-CHT", "zh-Hant", "Trad Chinese", "繁體中文"));
        map.put("zh-cn",  new Lang("zh-CN", "zh-CN", "Simp Chinese (China)", "简体中文（中国）"));
        map.put("zh-hant-hk", new Lang("zh-Hant-HK", "zh-Hant-HK", "Trad Chinese (Hong Kong)", "繁體中文（香港）"));
        map.put("zh-hant-tw", new Lang("zh-Hant-TW", "zh-Hant-TW", "Trad Chinese (Taiwan)", "繁體中文（台湾）"));
        map.put("da",     new Lang("da", "da", "Danish", "Dansk"));
        map.put("nl",     new Lang("nl", "nl", "Dutch", "Nederlands"));
        map.put("en",     new Lang("en", "en", "English", "English"));
        map.put("en-au",  new Lang("en-AU", "en-AU", "English (Australia)", "English (Australia)"));
        map.put("en-ca",  new Lang("en-CA", "en-CA", "English (Canada)", "English (Canada)"));
        map.put("en-in",  new Lang("en-IN", "en-IN", "English (India)", "English (India)"));
        map.put("en-nz",  new Lang("en-NZ", "en-NZ", "English (New Zealand)", "English (New Zealand)"));
        map.put("en-za",  new Lang("en-ZA", "en-ZA", "English (South Africa)", "English (South Africa)"));
        map.put("en-gb",  new Lang("en-GB", "en-GB", "English (United Kingdom)", "English (United Kingdom)"));
        map.put("en-sg",  new Lang("en-SG", "en-SG", "English (Singapore)", "English (Singapore)"));
        map.put("en-us",  new Lang("en-US", "en-US", "English (United States)", "English (United States)"));
        map.put("fi",     new Lang("fi", "fi", "Finnish", "Suomi"));
        map.put("fr",     new Lang("fr", "fr", "French", "Français"));
        map.put("fr-ca",  new Lang("fr-CA", "fr-CA", "French (Canada)", "Français (Canada)"));
        map.put("fr-fr",  new Lang("fr-FR", "fr-FR", "French (France)", "Français (France)"));
        map.put("fr-ch",  new Lang("fr-CH", "fr-CH", "French (Switzerland)", "Français (Suisse)"));
        map.put("gl",     new Lang("gl", "gl", "Galician", "Galego"));
        map.put("de",     new Lang("de", "de", "German", "Deutsch"));
        map.put("de-at",  new Lang("de-AT", "de-AT", "German (Austria)", "Deutsch (Österreich)"));
        map.put("de-de",  new Lang("de-DE", "de-DE", "German (Germany)", "Deutsch (Deutschland)"));
        map.put("de-li",  new Lang("de-LI", "de-LI", "German (Liechtenstien)", "Deutsch (Liechtenstien)"));
        map.put("de-ch",  new Lang("de-CH", "de-CH", "German (Switzerland)", "Deutsch (Schweiz)"));
        map.put("el",     new Lang("el", "el", "Greek", "Ελληνικά"));
        map.put("he",     new Lang("he", "he", "Hebrew", "עברית"));
        map.put("hu",     new Lang("hu", "hu", "Hungarian", "Magyar"));
        map.put("id",     new Lang("id", "id", "Indonesian", "Bahasa Indonesia"));
        map.put("it",     new Lang("it", "it", "Italian", "Italiano"));
        map.put("it-it",  new Lang("it-IT", "it-IT", "Italian (Italy)", "Italiano (Italia)"));
        map.put("it-ch",  new Lang("it-CH", "it-CH", "Italian (Switzerland)", "Italiano (Svizzera)"));
        map.put("ja",     new Lang("ja", "ja", "Japanese", "日本語"));
        map.put("ko",     new Lang("ko", "ko", "Korean", "한국어"));
        map.put("lv",     new Lang("lv", "lv", "Latvian", "Latviešu"));
        map.put("ms",     new Lang("ms", "ms", "Malay", "Bahasa Melayu"));
        map.put("my",     new Lang("my", "my", "Burmese", "ဗမာစာ"));
        map.put("ne",     new Lang("ne", "ne", "Nepali", "नेपाली भाषा"));
        map.put("no",     new Lang("no", "no", "Norwegian", "Norsk"));
        map.put("fa",     new Lang("fa", "fa", "Persian", "زبان_فارسی"));
        map.put("pl",     new Lang("pl", "pl", "Polish", "Polski"));
        map.put("pt",     new Lang("pt", "pt", "Portuguese", "Português"));
        map.put("pt-br",  new Lang("pt-BR", "pt-BR", "Portuguese (Brazil)", "Português (Brasil)"));
        map.put("pt-pt",  new Lang("pt-PT", "pt-PT", "Portuguese (Portugal)", "Português (Portugal)"));
        map.put("ru",     new Lang("ru", "ru", "Russian", "Русский"));
        map.put("es",     new Lang("es", "es", "Spanish", "Español"));
        map.put("es-ra",  new Lang("es-RA", "es-RA", "Spanish (Argentina)", "Español (Argentina)"));
        map.put("es-cl",  new Lang("es-CL", "es-CL", "Spanish (Chile)", "Español (Chile)"));
        map.put("es-co",  new Lang("es-CO", "es-CO", "Spanish (Colombia)", "Español (Colombia)"));
        map.put("es-cr",  new Lang("es-CR", "es-CR", "Spanish (Costa Rica)", "Español (Costa Rica)"));
        map.put("es-hn",  new Lang("es-HN", "es-HN", "Spanish (Honduras)", "Español (Honduras)"));
        map.put("es-419", new Lang("es-419", "es-419", "Spanish (Latin America)", "Español (Latinoamérica)"));
        map.put("es-mx",  new Lang("es-MX", "es-MX", "Spanish (Mexico)", "Español (México)"));
        map.put("es-pe",  new Lang("es-PE", "es-PE", "Spanish (Peru)", "Español (Perú)"));
        map.put("es-es",  new Lang("es-ES", "es-ES", "Spanish (Spain)", "Español (España)"));
        map.put("es-us",  new Lang("es-US", "es-US", "Spanish (United States)", "Español (Estados Unidos)"));
        map.put("es-uy",  new Lang("es-UY", "es-UY", "Spanish (Uruguay)", "Español (Uruguay)"));
        map.put("es-ve",  new Lang("es-VE", "es-VE", "Spanish (Venezuela)", "Español (Venezuela)"));
        map.put("sw",     new Lang("sw", "sw", "Swahili", "Kiswahili"));
        map.put("sv",     new Lang("sv", "sv", "Swedish", "Svensk"));
        map.put("tl",     new Lang("tl", "tl", "Tagalog", "Tagalog"));
        map.put("th",     new Lang("th", "th", "Thai", "ภาษาไทย"));
        map.put("hi",     new Lang("hi", "hi", "Hindi", "हिन्दी"));
        map.put("tr",     new Lang("tr", "tr", "Turkish", "Türkçe"));
        map.put("uk",     new Lang("uk", "uk", "Ukrainian", "Українська"));
        map.put("ur",     new Lang("ur", "ur", "Urdu", "اردو"));
        map.put("vi",     new Lang("vi", "vi", "Vietnamese", "Tiếng Việt"));
        map.put("tl",     new Lang("tl", "tl", "Tagalog", "Tagalog"));
        LANG = Collections.unmodifiableMap(map);
    }

    /* `code` is the primary language code used by Wovn */
    String code;
    /* `codeISO639_1` is the "ISO639_1" standard for language code used in hreflangs */
    String codeISO639_1;

    Lang(String code, String codeISO639_1, String englishName, String nativeName) {
        this.code = code;
        this.codeISO639_1 = codeISO639_1;
    }

    static Lang get(String langCode) {
        if (langCode == null) return null;

        return LANG.get(langCode.toLowerCase());
    }
}
