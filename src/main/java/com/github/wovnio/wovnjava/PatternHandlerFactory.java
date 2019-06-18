package com.github.wovnio.wovnjava;

import java.util.ArrayList;

final class PatternHandlerFactory {
    private PatternHandlerFactory() {}

    public static PatternHandler create(Settings settings) throws ConfigurationError {
        return create(settings.urlPattern, settings.supportedLangs, settings.sitePrefixPath);
    }

    public static PatternHandler create(String urlPattern, ArrayList<String> supportedLangs, String sitePrefixPath) throws ConfigurationError {
        if (supportedLangs.size() == 0) {
            throw new ConfigurationError("Missing supportedLangs");
        }
        if ("path".equalsIgnoreCase(urlPattern)) {
            return new PathPatternHandler(supportedLangs, sitePrefixPath);
        } else if ("query".equalsIgnoreCase(urlPattern)) {
            return new QueryPatternHandler(supportedLangs);
        } else if ("subdomain".equalsIgnoreCase(urlPattern)) {
            return new SubdomainPatternHandler(supportedLangs);
        } else {
            throw new ConfigurationError("Invalid url pattern: " + urlPattern);
        }
    }
}

