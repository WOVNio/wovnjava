package com.github.wovnio.wovnjava;

final class UrlLangPatternHandlerFactory {
    private UrlLangPatternHandlerFactory() {}

    public static UrlLangPatternHandler create(Settings settings) throws ConfigurationError {
        return create(settings.urlPattern, settings.sitePrefixPath);
    }

    public static UrlLangPatternHandler create(String urlPattern, String sitePrefixPath) throws ConfigurationError {
        if ("path".equalsIgnoreCase(urlPattern)) {
            return new PathUrlLangPatternHandler(sitePrefixPath);
        } else if ("query".equalsIgnoreCase(urlPattern)) {
            return new QueryUrlLangPatternHandler();
        } else if ("subdomain".equalsIgnoreCase(urlPattern)) {
            return new SubdomainUrlLangPatternHandler();
        } else {
            throw new ConfigurationError("Invalid url pattern: " + urlPattern);
        }
    }
}

