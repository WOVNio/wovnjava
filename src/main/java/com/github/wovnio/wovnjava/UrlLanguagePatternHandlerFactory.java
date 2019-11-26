package com.github.wovnio.wovnjava;

import java.util.ArrayList;

final class UrlLanguagePatternHandlerFactory {
    private UrlLanguagePatternHandlerFactory() {}

    public static UrlLanguagePatternHandler create(Settings settings) throws ConfigurationError {
        return create(settings.defaultLang, settings.supportedLangs, settings.urlPattern, settings.sitePrefixPath, settings.rawCustomDomainLangs);
    }

    public static UrlLanguagePatternHandler create(Lang defaultLang, ArrayList<Lang> supportedLangs, String urlPattern, String sitePrefixPath, String rawCustomDomainLangs) throws ConfigurationError {
        if ("path".equalsIgnoreCase(urlPattern)) {
            return new PathUrlLanguagePatternHandler(defaultLang, supportedLangs, sitePrefixPath);
        } else if ("query".equalsIgnoreCase(urlPattern)) {
            return new QueryUrlLanguagePatternHandler(defaultLang, supportedLangs);
        } else if ("subdomain".equalsIgnoreCase(urlPattern)) {
            return new SubdomainUrlLanguagePatternHandler(defaultLang, supportedLangs);
        } else if ("customDomain".equalsIgnoreCase(urlPattern)) {
            CustomDomainLanguages customDomainLanguages = CustomDomainLanguageSerializer.deserialize(rawCustomDomainLangs);
            if (!isCustomDomainLanguagesCompatible(customDomainLanguages, supportedLangs)) {
                throw new ConfigurationError("\"customDomainLanguages\" does not match \"supportedLangs\". A custom domain must be declared for each supported language.");
            }
            return new CustomDomainUrlLanguagePatternHandler(defaultLang, customDomainLanguages);
        } else {
            throw new ConfigurationError("Invalid url pattern: " + urlPattern);
        }
    }

    private static boolean isCustomDomainLanguagesCompatible(CustomDomainLanguages customDomainLanguages, ArrayList<Lang> supportedLangs) {
        if (customDomainLanguages.customDomainLanguageList.size() != supportedLangs.size()) {
            return false;
        }
        for (CustomDomainLanguage customDomainLanguage : customDomainLanguages.customDomainLanguageList) {
            if (!supportedLangs.contains(customDomainLanguage.lang)) {
                return false;
            }
        }
        return true;
    }
}
