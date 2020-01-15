package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Map;
import java.net.URL;

class SupportedLanguages {
    public final boolean hasLangCodeAliasForDefaultLang;
    public final ArrayList<SupportedLanguage> supportedLanguages;

    public SupportedLanguages(ArrayList<Lang> supportedLangs, Map<Lang, String> langCodeAliases, Lang defaultLang) {
        this.hasLangCodeAliasForDefaultLang = langCodeAliases.get(defaultLang) != null;

        ArrayList<SupportedLanguage> tmp = new ArrayList<SupportedLanguage>();
        for (Lang lang : supportedLangs) {
            String alias = langCodeAliases.get(lang);
            if (alias == null) {
                alias = lang.code;
            }
            boolean isDefaultLanguage = lang == defaultLang;
            tmp.add(new SupportedLanguage(lang, alias, isDefaultLanguage));
        }
        this.supportedLanguages = tmp;
    }

    public SupportedLanguage get(String identifier) {
        for (SupportedLanguage supportedLanguage : this.supportedLanguages) {
            if (supportedLanguage.identifier == identifier) {
                return supportedLanguage;
            }
        }
        return null;
    }
}
