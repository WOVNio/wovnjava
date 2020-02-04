package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

class LanguageAliasSerializer {
    private LanguageAliasSerializer() {}

    public static Map<Lang, String> deserializeFilterConfig(String rawLangCodeAliases) throws ConfigurationError {
        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();

        if (rawLangCodeAliases == null || rawLangCodeAliases.isEmpty()) {
            return langCodeAliases;
        }

        for (String rawPair : rawLangCodeAliases.split(",")) {
            if (rawPair == null || rawPair.isEmpty()) {
                continue;
            }
            String[] splitPair = rawPair.split(":");
            if (splitPair.length != 2) {
                throw new ConfigurationError("Invalid configuration format for \"langCodeAliases\": " + rawLangCodeAliases);
            }

            Lang lang = Lang.get(splitPair[0].trim());
            if (lang == null) {
                throw new ConfigurationError("Invalid source language for \"langCodeAliases\", each left-hand value must match a supported language code.");
            }

            String alias = splitPair[1].trim();
            langCodeAliases.put(lang, alias);
        }

        return langCodeAliases;
    }

    public static String serializeToJson(Map<Lang, String> langCodeAliases) {
        if (langCodeAliases == null || langCodeAliases.size() < 1) return "{}";

        ArrayList<String> items = new ArrayList<String>();
        for (Map.Entry<Lang, String> langCodeAlias : langCodeAliases.entrySet()) {
            Lang lang = langCodeAlias.getKey();
            String alias = langCodeAlias.getValue();
            items.add(lang.code + "\":\"" + alias);
        }
        return "{\"" + String.join("\",\"", items) + "\"}";
    }
}
