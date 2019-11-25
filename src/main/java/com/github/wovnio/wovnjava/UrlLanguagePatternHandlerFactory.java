package com.github.wovnio.wovnjava;

import java.util.ArrayList;

final class UrlLanguagePatternHandlerFactory {
    private UrlLanguagePatternHandlerFactory() {}

    public static UrlLanguagePatternHandler create(Settings settings) throws ConfigurationError {
        return create(settings.defaultLang, settings.supportedLangs, settings.urlPattern, settings.sitePrefixPath);
    }

    public static UrlLanguagePatternHandler create(Lang defaultLang, ArrayList<Lang> supportedLangs, String urlPattern, String sitePrefixPath) throws ConfigurationError {
        if ("path".equalsIgnoreCase(urlPattern)) {
            return new PathUrlLanguagePatternHandler(defaultLang, supportedLangs, sitePrefixPath);
        } else if ("query".equalsIgnoreCase(urlPattern)) {
            return new QueryUrlLanguagePatternHandler(defaultLang, supportedLangs);
        } else if ("subdomain".equalsIgnoreCase(urlPattern)) {
            return new SubdomainUrlLanguagePatternHandler(defaultLang, supportedLangs);
        } else {
            throw new ConfigurationError("Invalid url pattern: " + urlPattern);
        }
    }
}

