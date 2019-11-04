package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.MalformedURLException;
import java.net.URL;

class CustomDomainLanguageSerializer {
    private CustomDomainLanguageSerializer() {}

    public static CustomDomainLanguages deserialize(String rawCustomDomainLanguages) throws ConfigurationError {
        if (rawCustomDomainLanguages == null || rawCustomDomainLanguages.isEmpty()) {
            throw new ConfigurationError("Using \"urlPattern=customDomain\", missing required configuration for \"customDomainLangs\".");
        }

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
                customDomainLanguageList.add(new CustomDomainLanguage(url.getHost(), url.getPath(), lang));
            }
            catch (MalformedURLException e) {
                throw new ConfigurationError("MalformedURLException when parsing \"customDomainLangs\": " + e.getMessage());
            }
        }

        validateUniqueness(customDomainLanguageList);

        return new CustomDomainLanguages(customDomainLanguageList);
    }

    private static void validateUniqueness(ArrayList<CustomDomainLanguage> customDomainLanguageList) throws ConfigurationError {
        ArrayList<Lang> includedLangs = new ArrayList<Lang>();
        ArrayList<String> includedUrls = new ArrayList<String>();

        for (CustomDomainLanguage customDomainLanguage : customDomainLanguageList) {
            Lang lang = customDomainLanguage.lang;
            if (includedLangs.contains(lang)) {
                throw new ConfigurationError("Each language can only be specified once for \"customDomainLangs\". \"" + lang.code + "\" is specified more than once.");
            } else {
                includedLangs.add(lang);
            }

            String url = customDomainLanguage.host + removeTrailingSlash(customDomainLanguage.path);
            if (includedUrls.contains(url)) {
                throw new ConfigurationError("Each language must be mapped to a unique domain and path combination for \"customDomainLangs\". \"" + url + "\" is used twice.");
            } else {
                includedUrls.add(url);
            }
        }
    }

    private static String removeTrailingSlash(String path) {
        return path.replaceAll("/$", "");
    }
}
