package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.FilterConfig;
import javax.xml.bind.DatatypeConverter;

class Settings {
    public static final String VERSION = Version.readProjectVersion();

    // Default configuration values
    public static final int DefaultTimeout = 1000;
    public static final String DefaultApiUrlProduction  = "https://wovn.global.ssl.fastly.net/v0/";
    public static final String DefaultApiUrlDevelopment = "http://localhost:3001/v0/";
    public static final String DefaultSnippetUrlProduction  = "//j.wovn.io/1";
    public static final String DefaultSnippetUrlDevelopment = "//j.dev-wovn.io:3000/1";

    // Required settings
    public final String projectToken;
    public final String urlPattern;
    public final Lang defaultLang;
    public final ArrayList<Lang> supportedLangs;

    // Optional settings
    public final boolean devMode;
    public final boolean debugMode;
    public final boolean useProxy;
    public final boolean enableFlushBuffer;
    public final boolean showVersion;

    public final String sitePrefixPath;
    public final CustomDomainLanguages customDomainLanguages;

    public final String snippetUrl;
    public final String apiUrl;
    public final String originalUrlHeader;
    public final String originalQueryStringHeader;

    public final ArrayList<String> ignoreClasses;

    public final int connectTimeout;
    public final int readTimeout;

    public final String outboundProxyHost;
    public final int outboundProxyPort;

    public final String fixedHost;
    public final String fixedScheme;
    public final int fixedPort;
    public final boolean hasUrlOverride;

    Settings(FilterConfig config) throws ConfigurationError {
        FilterConfigReader reader = new FilterConfigReader(config);

        // Required settings
        this.projectToken = verifyToken(reader.getStringParameter("userToken"), reader.getStringParameter("projectToken"));
        this.urlPattern = verifyUrlPattern(reader.getStringParameter("urlPattern"));
        this.defaultLang = verifyDefaultLang(reader.getStringParameter("defaultLang"));
        this.supportedLangs = verifySupportedLangs(reader.getArrayParameter("supportedLangs"), this.defaultLang);

        // Optional settings
        this.devMode = reader.getBoolParameterDefaultFalse("devMode");
        this.debugMode = reader.getBoolParameterDefaultFalse("debugMode");
        this.useProxy = reader.getBoolParameterDefaultFalse("useProxy");
        this.enableFlushBuffer = reader.getBoolParameterDefaultFalse("enableFlushBuffer");
        this.showVersion = reader.getBoolParameterDefaultTrue("showVersion");

        this.sitePrefixPath = normalizeSitePrefixPath(reader.getStringParameter("sitePrefixPath"));
        this.customDomainLanguages = parseCustomDomainLangs(reader.getStringParameter("customDomainLangs"), this.supportedLangs);

        String defaultApiUrl = this.devMode ? DefaultApiUrlDevelopment : DefaultApiUrlProduction;
        this.apiUrl = stringOrDefault(reader.getStringParameter("apiUrl"), defaultApiUrl);
        this.snippetUrl = this.devMode ? DefaultSnippetUrlDevelopment : DefaultSnippetUrlProduction;

        this.ignoreClasses = reader.getArrayParameter("ignoreClasses");

        this.originalUrlHeader = stringOrDefault(reader.getStringParameter("originalUrlHeader"), "");
        this.originalQueryStringHeader = stringOrDefault(reader.getStringParameter("originalQueryStringHeader"), "");

        this.connectTimeout = positiveIntOrDefault(reader.getIntParameter("connectTimeout"), DefaultTimeout);
        this.readTimeout = positiveIntOrDefault(reader.getIntParameter("readTimeout"), DefaultTimeout);

        this.outboundProxyHost = nonEmptyString(reader, "outboundProxyHost");
        this.outboundProxyPort = reader.getIntParameter("outboundProxyPort");

        this.fixedHost = stringOrDefault(reader.getStringParameter("fixedHost"), "");
        this.fixedScheme = stringOrDefault(reader.getStringParameter("fixedScheme"), "");
        this.fixedPort = positiveIntOrDefault(reader.getIntParameter("fixedPort"), -1);
        this.verifyFixedURLConfigs(this.fixedHost, this.fixedScheme, this.fixedPort);
        this.hasUrlOverride = this.fixedPort != -1;
    }

    private void verifyFixedURLConfigs(String fixedHost, String fixedScheme, int fixedPort) throws ConfigurationError {
        // all three settings are either all set, or none is set
        Boolean allTheSame = fixedHost.isEmpty() == fixedScheme.isEmpty() && fixedScheme.isEmpty() == (fixedPort == -1);

        if (!allTheSame) {
            throw new ConfigurationError("Missing configuration: \"fixedHost\", \"fixedScheme\" and \"fixedPort\" must all be defined");
        }
    }

    private String verifyToken(String declaredUserToken, String declaredProjectToken) throws ConfigurationError {
        String value = declaredProjectToken == null ? declaredUserToken : declaredProjectToken;
        if (value == null || value.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"projectToken\".");
        } else if (value.length() < 5 || value.length() > 6) {
            throw new ConfigurationError("Invalid configuration for \"projecToken\", must be 5 or 6 characters long.");
        }
        return value;
    }

    private String verifyUrlPattern(String value) throws ConfigurationError {
        // Valid value for `urlPattern` is checked in UrlLanguagePatternHandlerFactory,
        // so here we only assert that some string is declared for this setting.
        if (value == null || value.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"urlPattern\".");
        } else if ("customdomain".equalsIgnoreCase(value)) {
            // Other systems expect snake_case, but we want to also accept camelCase for java
            value = "custom_domain";
        }
        return value;
    }

    private Lang verifyDefaultLang(String value) throws ConfigurationError {
        if (value == null || value.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"defaultLang\".");
        }
        Lang lang = Lang.get(value);
        if (lang == null) {
            throw new ConfigurationError("Invalid configuration for \"defaultLang\", must match a supported language code.");
        }
        return lang;
    }

    private ArrayList<Lang> verifySupportedLangs(ArrayList<String> values, Lang defaultLang) throws ConfigurationError {
        if (values.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"supportedLangs\".");
        }
        ArrayList<Lang> verifiedLangs = new ArrayList<Lang>();
        Lang lang;
        for (String val : values) {
            lang = Lang.get(val);
            if (lang == null) {
                throw new ConfigurationError("Invalid configuration for \"supportedLangs\", each value must match a supported language code.");
            }
            if (verifiedLangs.contains(lang)) {
                throw new ConfigurationError("Invalid configuration for \"supportedLangs\", each language must only be specified once.");
            }
            verifiedLangs.add(lang);
        }
        if (!verifiedLangs.contains(defaultLang)) {
            verifiedLangs.add(defaultLang);
        }
        return verifiedLangs;
    }

    // Empty string means `sitePrefixPath` is not used (default)
    // If set, `sitePrefixPath` must start with "/", and must not end with "/"
    private String normalizeSitePrefixPath(String value) throws ConfigurationError {
        if (value == null || value.isEmpty()) {
            return "";
        }
        value = value.toLowerCase();
        if (!value.startsWith("/")) value = "/" + value;
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        } else {
            return value;
        }
    }

    private CustomDomainLanguages parseCustomDomainLangs(String rawCustomDomainLangs, ArrayList<Lang> supportedLangs) throws ConfigurationError {
        if (rawCustomDomainLangs == null || rawCustomDomainLangs.isEmpty()) {
            return null;
        }
        ArrayList<CustomDomainLanguage> customDomainLanguageList = CustomDomainLanguageSerializer.deserializeFilterConfig(rawCustomDomainLangs);
        ValidationResult validationResult = CustomDomainLanguageValidator.validate(customDomainLanguageList, supportedLangs);
        if (!validationResult.success) {
            throw new ConfigurationError(validationResult.errorMessage);
        }
        return new CustomDomainLanguages(customDomainLanguageList);
    }

    private String stringOrDefault(String declaredValue, String defaultValue) {
        return declaredValue != null ? declaredValue : defaultValue;
    }

    private int positiveIntOrDefault(int declaredValue, int defaultValue) {
        return declaredValue > 0 ? declaredValue : defaultValue;
    }

    private String nonEmptyString(FilterConfigReader reader, String key) throws ConfigurationError {
        String value = reader.getStringParameter(key);
        if (value != null && value.isEmpty()) {
            throw new ConfigurationError(String.format("Invalid configuration for \"%s\", value cannot be empty.", value));
        }
        return value;
    }

    String hash() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(projectToken.getBytes());
        md.update(urlPattern.getBytes());
        md.update(sitePrefixPath.getBytes());
        md.update(defaultLang.code.getBytes());
        for (Lang lang : supportedLangs) {
            md.update(lang.code.getBytes());
        }
        md.update(useProxy ? new byte[]{ 0 } : new byte[] { 1 });
        md.update(originalUrlHeader.getBytes());
        md.update(originalQueryStringHeader.getBytes());
        md.update(showVersion ? new byte[] { 0 } : new byte[] { 1 });
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }
}
