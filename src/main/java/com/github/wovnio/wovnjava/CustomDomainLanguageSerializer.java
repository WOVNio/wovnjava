package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
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
            // TODO: raise error if `hostAndPath` includes protocol (match against "http" or "://", for example)
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

    private static String removeTrailingSlash(String path) {
        return path.replaceAll("/$", "");
    }
}
