package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Lang {
    private static final Map<String, Lang> LANG;
    static {
        ArrayList<Lang> langs = new ArrayList<Lang>();
        langs.add(new Lang("ar", "ar", "Arabic", "العربية"));
        langs.add(new Lang("eu", "eu", "Basque", "Euskara"));
        langs.add(new Lang("bn", "bn", "Bengali", "বাংলা ভাষা"));
        langs.add(new Lang("bg", "bg", "Bulgarian", "Български"));
        langs.add(new Lang("ca", "ca", "Catalan", "Català"));
        langs.add(new Lang("zh-CHS", "zh-Hans", "Simp Chinese", "简体中文"));
        langs.add(new Lang("zh-CHT", "zh-Hant", "Trad Chinese", "繁體中文"));
        langs.add(new Lang("zh-CN", "zh-CN", "Simp Chinese (China)", "简体中文（中国）"));
        langs.add(new Lang("zh-Hant-HK", "zh-Hant-HK", "Trad Chinese (Hong Kong)", "繁體中文（香港）"));
        langs.add(new Lang("zh-Hant-TW", "zh-Hant-TW", "Trad Chinese (Taiwan)", "繁體中文（台湾）"));
        langs.add(new Lang("da", "da", "Danish", "Dansk"));
        langs.add(new Lang("nl", "nl", "Dutch", "Nederlands"));
        langs.add(new Lang("en", "en", "English", "English"));
        langs.add(new Lang("en-AU", "en-AU", "English (Australia)", "English (Australia)"));
        langs.add(new Lang("en-CA", "en-CA", "English (Canada)", "English (Canada)"));
        langs.add(new Lang("en-IN", "en-IN", "English (India)", "English (India)"));
        langs.add(new Lang("en-NZ", "en-NZ", "English (New Zealand)", "English (New Zealand)"));
        langs.add(new Lang("en-ZA", "en-ZA", "English (South Africa)", "English (South Africa)"));
        langs.add(new Lang("en-GB", "en-GB", "English (United Kingdom)", "English (United Kingdom)"));
        langs.add(new Lang("en-SG", "en-SG", "English (Singapore)", "English (Singapore)"));
        langs.add(new Lang("en-US", "en-US", "English (United States)", "English (United States)"));
        langs.add(new Lang("fi", "fi", "Finnish", "Suomi"));
        langs.add(new Lang("fr", "fr", "French", "Français"));
        langs.add(new Lang("fr-CA", "fr-CA", "French (Canada)", "Français (Canada)"));
        langs.add(new Lang("fr-FR", "fr-FR", "French (France)", "Français (France)"));
        langs.add(new Lang("fr-CH", "fr-CH", "French (Switzerland)", "Français (Suisse)"));
        langs.add(new Lang("gl", "gl", "Galician", "Galego"));
        langs.add(new Lang("de", "de", "German", "Deutsch"));
        langs.add(new Lang("de-AT", "de-AT", "German (Austria)", "Deutsch (Österreich)"));
        langs.add(new Lang("de-DE", "de-DE", "German (Germany)", "Deutsch (Deutschland)"));
        langs.add(new Lang("de-LI", "de-LI", "German (Liechtenstien)", "Deutsch (Liechtenstien)"));
        langs.add(new Lang("de-CH", "de-CH", "German (Switzerland)", "Deutsch (Schweiz)"));
        langs.add(new Lang("el", "el", "Greek", "Ελληνικά"));
        langs.add(new Lang("he", "he", "Hebrew", "עברית"));
        langs.add(new Lang("hu", "hu", "Hungarian", "Magyar"));
        langs.add(new Lang("id", "id", "Indonesian", "Bahasa Indonesia"));
        langs.add(new Lang("it", "it", "Italian", "Italiano"));
        langs.add(new Lang("it-IT", "it-IT", "Italian (Italy)", "Italiano (Italia)"));
        langs.add(new Lang("it-CH", "it-CH", "Italian (Switzerland)", "Italiano (Svizzera)"));
        langs.add(new Lang("ja", "ja", "Japanese", "日本語"));
        langs.add(new Lang("ko", "ko", "Korean", "한국어"));
        langs.add(new Lang("lv", "lv", "Latvian", "Latviešu"));
        langs.add(new Lang("ms", "ms", "Malay", "Bahasa Melayu"));
        langs.add(new Lang("my", "my", "Burmese", "ဗမာစာ"));
        langs.add(new Lang("ne", "ne", "Nepali", "नेपाली भाषा"));
        langs.add(new Lang("no", "no", "Norwegian", "Norsk"));
        langs.add(new Lang("fa", "fa", "Persian", "زبان_فارسی"));
        langs.add(new Lang("pl", "pl", "Polish", "Polski"));
        langs.add(new Lang("pt", "pt", "Portuguese", "Português"));
        langs.add(new Lang("pt-BR", "pt-BR", "Portuguese (Brazil)", "Português (Brasil)"));
        langs.add(new Lang("pt-PT", "pt-PT", "Portuguese (Portugal)", "Português (Portugal)"));
        langs.add(new Lang("ru", "ru", "Russian", "Русский"));
        langs.add(new Lang("es", "es", "Spanish", "Español"));
        langs.add(new Lang("es-RA", "es-RA", "Spanish (Argentina)", "Español (Argentina)"));
        langs.add(new Lang("es-CL", "es-CL", "Spanish (Chile)", "Español (Chile)"));
        langs.add(new Lang("es-CO", "es-CO", "Spanish (Colombia)", "Español (Colombia)"));
        langs.add(new Lang("es-CR", "es-CR", "Spanish (Costa Rica)", "Español (Costa Rica)"));
        langs.add(new Lang("es-HN", "es-HN", "Spanish (Honduras)", "Español (Honduras)"));
        langs.add(new Lang("es-419", "es-419", "Spanish (Latin America)", "Español (Latinoamérica)"));
        langs.add(new Lang("es-MX", "es-MX", "Spanish (Mexico)", "Español (México)"));
        langs.add(new Lang("es-PE", "es-PE", "Spanish (Peru)", "Español (Perú)"));
        langs.add(new Lang("es-ES", "es-ES", "Spanish (Spain)", "Español (España)"));
        langs.add(new Lang("es-US", "es-US", "Spanish (United States)", "Español (Estados Unidos)"));
        langs.add(new Lang("es-UY", "es-UY", "Spanish (Uruguay)", "Español (Uruguay)"));
        langs.add(new Lang("es-VE", "es-VE", "Spanish (Venezuela)", "Español (Venezuela)"));
        langs.add(new Lang("sw", "sw", "Swahili", "Kiswahili"));
        langs.add(new Lang("sv", "sv", "Swedish", "Svensk"));
        langs.add(new Lang("th", "th", "Thai", "ภาษาไทย"));
        langs.add(new Lang("hi", "hi", "Hindi", "हिन्दी"));
        langs.add(new Lang("tr", "tr", "Turkish", "Türkçe"));
        langs.add(new Lang("uk", "uk", "Ukrainian", "Українська"));
        langs.add(new Lang("ur", "ur", "Urdu", "اردو"));
        langs.add(new Lang("vi", "vi", "Vietnamese", "Tiếng Việt"));
        langs.add(new Lang("tl", "tl", "Tagalog", "Tagalog"));

        HashMap<String, Lang> map = new HashMap<String, Lang>();
        for (Lang lang : langs) {
            map.put(lang.code.toLowerCase(), lang);
        }
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
