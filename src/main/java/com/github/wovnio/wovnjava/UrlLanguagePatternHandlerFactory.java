package com.github.wovnio.wovnjava;

final class UrlLanguagePatternHandlerFactory {
    private UrlLanguagePatternHandlerFactory() {}

    public static UrlLanguagePatternHandler create(Settings settings) throws ConfigurationError {
        LanguageAliases languageAliases = new LanguageAliases(settings.supportedLangs, settings.langCodeAliases, settings.defaultLang);

        return create(settings.defaultLang, languageAliases, settings.urlPattern, settings.sitePrefixPath, settings.customDomainLanguages);
    }

    public static UrlLanguagePatternHandler create(Lang defaultLang, LanguageAliases languageAliases, String urlPattern, String sitePrefixPath, CustomDomainLanguages customDomainLanguages) throws ConfigurationError {
        if ("path".equalsIgnoreCase(urlPattern)) {
            return new PathUrlLanguagePatternHandler(defaultLang, languageAliases, sitePrefixPath);
        } else if ("query".equalsIgnoreCase(urlPattern)) {
            return new QueryUrlLanguagePatternHandler(defaultLang, languageAliases);
        } else if ("subdomain".equalsIgnoreCase(urlPattern)) {
            return new SubdomainUrlLanguagePatternHandler(defaultLang, languageAliases);
        } else if ("custom_domain".equalsIgnoreCase(urlPattern)) {
            if (customDomainLanguages == null) {
                throw new ConfigurationError("\"customDomainLangs\" setting must be configured when \"urlPattern=customDomain\".");
            }
            return new CustomDomainUrlLanguagePatternHandler(defaultLang, customDomainLanguages);
        } else {
            throw new ConfigurationError("Invalid url pattern: " + urlPattern);
        }
    }
}
