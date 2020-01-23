package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;

class LanguageAliases {
    public final boolean hasAliasForDefaultLang;

    private final Map<Lang, String> langMap;

    public LanguageAliases(ArrayList<Lang> supportedLangs, Map<Lang, String> langCodeAliases, Lang defaultLang) {
        this.hasAliasForDefaultLang = langCodeAliases.get(defaultLang) != null;

        HashMap<Lang, String> langMap = new HashMap<Lang, String>();
        for (Lang lang : supportedLangs) {
            String alias = langCodeAliases.get(lang);
            langMap.put(lang, (alias != null) ? alias : lang.code);
        }
        this.langMap = langMap;
    }

    public String getAlias(Lang lang) {
        return this.langMap.get(lang);
    }

    public Lang getLang(String alias) {
        for (Map.Entry<Lang, String> entry : this.langMap.entrySet()) {
            if (entry.getValue() == alias) {
                return entry.getKey();
            }
        }
        return null;
    }
}
