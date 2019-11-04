package com.github.wovnio.wovnjava;

import java.util.ArrayList;

final class UrlLanguagePatternHandlerFactory {
    private UrlLanguagePatternHandlerFactory() {}

    public static UrlLanguagePatternHandler create(Settings settings) throws ConfigurationError {
        return create(settings.urlPattern, settings.sitePrefixPath, settings.rawCustomDomainLangs, settings.supportedLangs);
    }

    public static UrlLanguagePatternHandler create(String urlPattern, String sitePrefixPath, String rawCustomDomainLangs, ArrayList<String> supportedLangs) throws ConfigurationError {
        if ("path".equalsIgnoreCase(urlPattern)) {
            return new PathUrlLanguagePatternHandler(sitePrefixPath);
        } else if ("query".equalsIgnoreCase(urlPattern)) {
            return new QueryUrlLanguagePatternHandler();
        } else if ("subdomain".equalsIgnoreCase(urlPattern)) {
            return new SubdomainUrlLanguagePatternHandler();
        } else if ("customDomain".equalsIgnoreCase(urlPattern)) {
            CustomDomainLanguages customDomainLanguages = CustomDomainLanguageSerializer.deserialize(rawCustomDomainLangs);
            if (!isCustomDomainLanguagesCompatible(customDomainLanguages, supportedLangs)) {
                throw new ConfigurationError("\"customDomainLanguages\" does not match \"supportedLangs\". A custom domain must be declared for each supported language.");
            }
            return new CustomDomainUrlLanguagePatternHandler(customDomainLanguages);
        } else {
            throw new ConfigurationError("Invalid url pattern: " + urlPattern);
        }
    }

    private static boolean isCustomDomainLanguagesCompatible(CustomDomainLanguages customDomainLanguages, ArrayList<String> supportedLangs) {
        if (customDomainLanguages.customDomainLanguageList.size() != supportedLangs.size()) {
            return false;
        }
        for (CustomDomainLanguage customDomainLanguage : customDomainLanguages.customDomainLanguageList) {
            if (!supportedLangs.contains(customDomainLanguage.lang.code)) {
                return false;
            }
        }
        return true;
    }
}

