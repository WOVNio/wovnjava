package com.github.wovnio.wovnjava;

final class PatternHandlerFactory {
    private PatternHandlerFactory() {}

    public static PatternHandler create(Settings settings) throws SettingsException {
        return create(settings.urlPattern, settings.sitePrefixPath);
    }

    public static PatternHandler create(String urlPattern, String sitePrefixPath) throws SettingsException {
        if ("path".equalsIgnoreCase(urlPattern)) {
            return new PathPatternHandler(sitePrefixPath);
        } else if ("query".equalsIgnoreCase(urlPattern)) {
            return new QueryPatternHandler();
        } else if ("subdomain".equalsIgnoreCase(urlPattern)) {
            return new SubdomainPatternHandler();
        } else {
            throw new SettingsException("Invalid url pattern: " + urlPattern);
        }
    }
}

