package com.github.wovnio.wovnjava;

final class UrlLanguagePatternHandlerFactory {
    private UrlLanguagePatternHandlerFactory() {}

    public static UrlLanguagePatternHandler create(Settings settings) throws ConfigurationError {
        return create(settings.urlPattern, settings.sitePrefixPath);
    }

    public static UrlLanguagePatternHandler create(String urlPattern, String sitePrefixPath) throws ConfigurationError {
        if ("path".equalsIgnoreCase(urlPattern)) {
            return new PathUrlLanguagePatternHandler(sitePrefixPath);
        } else if ("query".equalsIgnoreCase(urlPattern)) {
            return new QueryUrlLanguagePatternHandler();
        } else if ("subdomain".equalsIgnoreCase(urlPattern)) {
            return new SubdomainUrlLanguagePatternHandler();
        } else {
            throw new ConfigurationError("Invalid url pattern: " + urlPattern);
        }
    }
}

