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
    public final String defaultLang;
    public final ArrayList<String> supportedLangs;

    // Optional settings
    public final boolean devMode;
    public final boolean debugMode;
    public final boolean useProxy;
    public final boolean enableFlushBuffer;

    public final String sitePrefixPath;
    public final String rawCustomDomainLangs;

    public final String snippetUrl;
    public final String apiUrl;
    public final String originalUrlHeader;
    public final String originalQueryStringHeader;

    public final ArrayList<String> ignoreClasses;

    public final int connectTimeout;
    public final int readTimeout;

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

        this.sitePrefixPath = normalizeSitePrefixPath(reader.getStringParameter("sitePrefixPath"));
        this.rawCustomDomainLangs = reader.getStringParameter("customDomainLangs");

        String defaultApiUrl = this.devMode ? DefaultApiUrlDevelopment : DefaultApiUrlProduction;
        this.apiUrl = stringOrDefault(reader.getStringParameter("apiUrl"), defaultApiUrl);
        this.snippetUrl = this.devMode ? DefaultSnippetUrlDevelopment : DefaultSnippetUrlProduction;

        this.ignoreClasses = reader.getArrayParameter("ignoreClasses");

        this.originalUrlHeader = stringOrDefault(reader.getStringParameter("originalUrlHeader"), "");
        this.originalQueryStringHeader = stringOrDefault(reader.getStringParameter("originalQueryStringHeader"), "");

        this.connectTimeout = positiveIntOrDefault(reader.getIntParameter("connectTimeout"), DefaultTimeout);
        this.readTimeout = positiveIntOrDefault(reader.getIntParameter("readTimeout"), DefaultTimeout);
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
        }
        return value;
    }

    private String verifyDefaultLang(String value) throws ConfigurationError {
        if (value == null || value.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"defaultLang\".");
        }
        Lang lang = Lang.get(value);
        if (lang == null) {
            throw new ConfigurationError("Invalid configuration for \"defaultLang\", must match a supported language code.");
        }
        return lang.code;
    }

    private ArrayList<String> verifySupportedLangs(ArrayList<String> values, String defaultLangCode) throws ConfigurationError {
        if (values.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"supportedLangs\".");
        }
        ArrayList<String> verifiedLangs = new ArrayList<String>();
        Lang lang;
        for (String val : values) {
            lang = Lang.get(val);
            if (lang == null) {
                throw new ConfigurationError("Invalid configuration for \"supportedLangs\", each value must match a supported language code.");
            }
            if (verifiedLangs.contains(lang.code)) {
                throw new ConfigurationError("Invalid configuration for \"supportedLangs\", each language must only be specified once.");
            }
            verifiedLangs.add(lang.code);
        }
        if (!verifiedLangs.contains(defaultLangCode)) {
            verifiedLangs.add(defaultLangCode);
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

    private String stringOrDefault(String declaredValue, String defaultValue) {
        return declaredValue != null ? declaredValue : defaultValue;
    }

    private int positiveIntOrDefault(int declaredValue, int defaultValue) {
        return declaredValue > 0 ? declaredValue : defaultValue;
    }

    String hash() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(projectToken.getBytes());
        md.update(urlPattern.getBytes());
        md.update(sitePrefixPath.getBytes());
        md.update(defaultLang.getBytes());
        for (String lang : supportedLangs) {
            md.update(lang.getBytes());
        }
        md.update(useProxy ? new byte[]{ 0 } : new byte[] { 1 });
        md.update(originalUrlHeader.getBytes());
        md.update(originalQueryStringHeader.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }
}
