package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;

class CustomDomainLanguageSerializer {
    private CustomDomainLanguageSerializer() {}

    public static ArrayList<CustomDomainLanguage> deserializeFilterConfig(String rawCustomDomainLanguages) throws ConfigurationError {
        ArrayList<CustomDomainLanguage> customDomainLanguageList = new ArrayList<CustomDomainLanguage>();

        for (String rawPair : rawCustomDomainLanguages.split(",")) {
            if (rawPair == null || rawPair.isEmpty()) {
                continue;
            }
            String[] splitPair = rawPair.split(":");
            if (splitPair.length != 2) {
                throw new ConfigurationError("Invalid configuration format for \"customDomainLangs\": " + rawCustomDomainLanguages);
            }

            Lang lang = Lang.get(splitPair[1].trim());
            if (lang == null) {
                throw new ConfigurationError("Invalid target language for \"customDomainLangs\", each value must match a supported language code.");
            }

            String hostAndPath = splitPair[0].trim();
            try {
                URL url = new URL("http://" + hostAndPath);
                customDomainLanguageList.add(new CustomDomainLanguage(url.getHost(), removeTrailingSlash(url.getPath()), lang));
            }
            catch (MalformedURLException e) {
                throw new ConfigurationError("MalformedURLException when parsing \"customDomainLangs\": " + e.getMessage());
            }
        }

        return customDomainLanguageList;
    }

    public static String serializeToJson(CustomDomainLanguages customDomainLanguages) {
        if (customDomainLanguages == null) {
            return "";
        }

        ArrayList<String> items = new ArrayList<String>();
        for (CustomDomainLanguage cdl : customDomainLanguages.customDomainLanguageList) {
            items.add(cdl.host + cdl.path + "/\":\"" + cdl.lang.code);
        }
        return "{\"" + StringUtil.join("\",\"", items) + "\"}";
    }

    private static String removeTrailingSlash(String path) {
        return path.replaceAll("/$", "");
    }
}
