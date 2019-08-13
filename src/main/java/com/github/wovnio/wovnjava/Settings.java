package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.FilterConfig;
import javax.xml.bind.DatatypeConverter;

class Settings {
    public static final String VERSION = Version.readProjectVersion();

    static final int DefaultTimeout = 1000;
    static final String DefaultApiUrlProduction  = "https://wovn.global.ssl.fastly.net/v0/";
    static final String DefaultApiUrlDevelopment = "http://localhost:3001/v0/";
    static final String DefaultSnippetUrlProduction  = "//j.wovn.io/1";
    static final String DefaultSnippetUrlDevelopment = "//j.dev-wovn.io:3000/1";

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
    public final String originalUrlHeader;
    public final String originalQueryStringHeader;

    public final ArrayList<String> ignoreClasses;

    public final String snippetUrl;
    public final String apiUrl;

    public final int connectTimeout;
    public final int readTimeout;

    public final ArrayList<String> query = new ArrayList<String>(); // TODO REMOVE

    Settings(FilterConfig config) throws ConfigurationError {
        FilterConfigReader reader = new FilterConfigReader(config);

        // Required settings
        this.projectToken = verifyToken(reader.getStringParameter("userToken"), reader.getStringParameter("projectToken"));
        this.urlPattern = verifyUrlPattern(reader.getStringParameter("urlPattern"));
        this.defaultLang = verifyDefaultLang(reader.getStringParameter("defaultLang"));
        this.supportedLangs = verifySupportedLangs(reader.getArrayParameter("supportedLangs"), this.defaultLang);

        // Optional settings
        this.sitePrefixPath = verifySitePrefixPath(reader.getStringParameter("sitePrefixPath"));

        this.devMode = reader.getBoolParameterDefaultFalse("devMode");
        this.debugMode = reader.getBoolParameterDefaultFalse("debugMode");
        this.useProxy = reader.getBoolParameterDefaultFalse("useProxy");
        this.enableFlushBuffer = reader.getBoolParameterDefaultFalse("enableFlushBuffer");

        String defaultApiUrl = this.devMode ? DefaultApiUrlDevelopment : DefaultApiUrlProduction;
        String declaredApiUrl = reader.getStringParameter("apiUrl");
        this.apiUrl = declaredApiUrl.isEmpty() ? defaultApiUrl : declaredApiUrl;

        this.snippetUrl = this.devMode ? DefaultSnippetUrlDevelopment : DefaultSnippetUrlProduction;

        this.ignoreClasses = reader.getArrayParameter("ignoreClasses");

        this.originalUrlHeader = reader.getStringParameter("originalUrlHeader");
        this.originalQueryStringHeader = reader.getStringParameter("originalQueryStringHeader");

        int declaredConnectTimeout = reader.getIntParameter("connectTimeout");
        this.connectTimeout = declaredConnectTimeout > 0 ? declaredConnectTimeout : DefaultTimeout;

        int declaredReadTimeout = reader.getIntParameter("readTimeout");
        this.readTimeout = declaredReadTimeout > 0 ? declaredReadTimeout : DefaultTimeout;
    }

    private String verifyToken(String declaredUserToken, String declaredProjectToken) throws ConfigurationError {
        String value = declaredProjectToken.isEmpty() ? declaredUserToken : declaredProjectToken;
        if (value.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"projectToken\".");
        } else if (value.length() < 5 || value.length() > 6) {
            throw new ConfigurationError("Invalid configuration for \"projecToken\", must be 5 or 6 characters long.");
        }
        return value;
    }

    private String verifyUrlPattern(String value) throws ConfigurationError {
        if (value.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"urlPattern\".");
        }
        return value;
    }

    private String verifyDefaultLang(String value) throws ConfigurationError {
        if (value.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"defaultLang\".");
        }
        String code = Lang.get(value).code;
        if (code == null) {
            throw new ConfigurationError("Invalid configuration for \"defaultLang\", must match a supported language code.");
        }
        return code;
    }

    private ArrayList<String> verifySupportedLangs(ArrayList<String> values, String defaultLangCode) throws ConfigurationError {
        if (values.isEmpty()) {
            throw new ConfigurationError("Missing required configuration for \"supportedLangs\".");
        }
        ArrayList<String> verifiedLangs = new ArrayList<String>();
        String code;
        for (String val : values) {
            code = Lang.get(val).code;
            if (code == null) {
                throw new ConfigurationError("Invalid configuration for \"supportedLangs\", each value must match a supported language code.");
            }
            verifiedLangs.add(code);
        }
        if (!verifiedLangs.contains(defaultLangCode)) {
            verifiedLangs.add(defaultLangCode);
        }
        return verifiedLangs;
    }

    private String verifySitePrefixPath(String value) throws ConfigurationError {
        if (!value.startsWith("/")) value = "/" + value;
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        } else {
            return value;
        }
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
