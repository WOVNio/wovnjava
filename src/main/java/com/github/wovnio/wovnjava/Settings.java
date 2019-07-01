package com.github.wovnio.wovnjava;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.FilterConfig;
import javax.xml.bind.DatatypeConverter;

class Settings {
    public static final String VERSION = Version.readProjectVersion();

    static final String DefaultApiUrl = "https://wovn.global.ssl.fastly.net/v0/";

    String projectToken = "";
    boolean hasSitePrefixPath = false;
    String sitePrefixPath = "";
    String secretKey = "";
    String urlPattern = "path";
    ArrayList<String> query;
    String snippetUrl = "//j.wovn.io/1";
    String apiUrl = DefaultApiUrl;
    String defaultLang = "en";
    ArrayList<String> supportedLangs;
    ArrayList<String> ignoreClasses;
    boolean useProxy = false;
    String originalUrlHeader = "";
    String originalQueryStringHeader = "";
    boolean strictHtmlCheck = false;
    final String version = VERSION;
    int connectTimeout = 1000;
    int readTimeout = 1000;
    boolean devMode = false;
    boolean debugMode = false;
    boolean enableFlushBuffer = false;

    Settings(FilterConfig config) throws ConfigurationError {
        super();

        this.query = new ArrayList<String>();
        this.supportedLangs = new ArrayList<String>();
        this.ignoreClasses = new ArrayList<String>();

        String p;

        p = config.getInitParameter("userToken");
        if (p != null && p.length() > 0) {
            this.projectToken = p;
        }

        p = config.getInitParameter("projectToken");
        if (p != null && p.length() > 0) {
            this.projectToken = p;
        }

        p = config.getInitParameter("sitePrefixPath");
        if (p != null && p.length() > 0) {
            this.hasSitePrefixPath = true;
            if (!p.startsWith("/")) p = "/" + p;
            if (p.endsWith("/")) {
                this.sitePrefixPath = p.substring(0, p.length() - 1);
            } else {
                this.sitePrefixPath = p;
            }
        }

        p = config.getInitParameter("secretKey");
        if (p != null && p.length() > 0) {
            this.secretKey = p;
        }

        p = config.getInitParameter("urlPattern");
        if (p != null && p.length() > 0) {
            this.urlPattern = p;
        }

        p = config.getInitParameter("query");
        if (p != null && p.length() > 0) {
            this.query = getArrayParameter(p);
        }

        p = config.getInitParameter("apiUrl");
        if (p != null && p.length() > 0) {
            this.apiUrl = p;
        }

        p = config.getInitParameter("defaultLang");
        if (p != null && p.length() > 0) {
            this.defaultLang = p;
        }

        p = config.getInitParameter("supportedLangs");
        if (p != null && p.length() > 0) {
            this.supportedLangs = getArrayParameter(p);
        }

        p = config.getInitParameter("ignoreClasses");
        if (p != null && p.length() > 0) {
            this.ignoreClasses = getArrayParameter(p);
        }

        p = config.getInitParameter("useProxy");
        if (p != null && p.length() > 0) {
            this.useProxy = getBoolParameter(p);
        }

        p = config.getInitParameter("originalUrlHeader");
        if (p != null && !p.isEmpty()) {
            this.originalUrlHeader = p;
        }

        p = config.getInitParameter("originalQueryStringHeader");
        if (p != null && !p.isEmpty()) {
            this.originalQueryStringHeader = p;
        }

        p = config.getInitParameter("connectTimeout");
        if (p != null && !p.isEmpty()) {
            this.connectTimeout = getIntParameter(p);
        }

        p = config.getInitParameter("readTimeout");
        if (p != null && !p.isEmpty()) {
            this.readTimeout = getIntParameter(p);
        }

        p = config.getInitParameter("strictHtmlCheck");
        if (p != null && !p.isEmpty()) {
            this.strictHtmlCheck = getBoolParameter(p);
        }

        p = config.getInitParameter("devMode");
        if (p != null && !p.isEmpty()) {
            this.devMode = getBoolParameter(p);
        }

        p = config.getInitParameter("debugMode");
        if (p != null && !p.isEmpty()) {
            this.debugMode = getBoolParameter(p);
        }

        p = config.getInitParameter("enableFlushBuffer");
        if (p != null && !p.isEmpty()) {
            this.enableFlushBuffer = getBoolParameter(p);
        }

        this.initialize();
    }

    static int getIntParameter(String param) {
        if (param == null || param.isEmpty()) {
            return 0;
        }
        int n;
        try {
            n = Integer.parseInt(param);
        } catch (NumberFormatException e) {
            Logger.log.error("NumberFormatException while parsing int parameter", e);
            n = 0;
        }
        return n;
    }

    @Contract("null -> null")
    static ArrayList<String> getArrayParameter(String param) {
        if (param == null || param.length() == 0) {
            return null;
        }

        param = param.replaceAll("^\\s+(.+)\\s+$", "$1");
        String[] params = param.split("\\s*,\\s*");
        ArrayList<String> al = new ArrayList<String>();
        Collections.addAll(al, params);
        return al;
    }

    @Contract("null -> false")
    static boolean getBoolParameter(String param) {
        if (param == null) {
            return false;
        }
        param = param.toLowerCase();
        return param.equals("on") || param.equals("true") || param.equals("1");
    }

    private void initialize() throws ConfigurationError {
        String inputCode = this.defaultLang;
        Lang defaultLangObject = Lang.getLang(inputCode);
        if (defaultLangObject == null) {
            throw new ConfigurationError("Invalid language code for defaultLang: " + inputCode);
        }
        this.defaultLang = defaultLangObject.code;

        if (this.supportedLangs.size() == 0) {
            this.supportedLangs.add(this.defaultLang);
        }

        if (this.devMode) {
            this.snippetUrl = "//j.dev-wovn.io:3000/1";
            if (this.apiUrl == DefaultApiUrl) {
              this.apiUrl = "http://localhost:3001/v0/";
            }
        }
    }

    boolean isValid() {
        boolean valid = true;
        ArrayList<String> errors = new ArrayList<String>();

        if (projectToken == null || projectToken.length() < 5 || projectToken.length() > 6) {
            valid = false;
            errors.add("Project token is not valid: " + projectToken);
        }
        if (secretKey == null || secretKey.length() == 0) {
            valid = false;
            errors.add("Secret key is not configured.");
        }
        if (urlPattern == null || urlPattern.length() == 0) {
            valid = false;
            errors.add("Url pattern is not configured.");
        }
        if (apiUrl == null || apiUrl.length() == 0) {
            valid = false;
            errors.add("API URL is not configured.");
        }
        if (defaultLang == null || defaultLang.length() == 0) {
            valid = false;
            errors.add("Default lang is not configured.");
        }
        if (supportedLangs.size() < 1) {
            valid = false;
            errors.add("Supported langs is not configured.");
        }
        if (hasSitePrefixPath && urlPattern != "path") {
            valid = false;
            errors.add("sitePrefixPath must be used together with urlPattern=path.");
        }

        if (errors.size() > 0) {
            Logger.log.error("Settings is invalid: " + errors.toString());
        }

        return valid;
    }

    String hash() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(projectToken.getBytes());
        md.update(urlPattern.getBytes());
        for (String q : query) {
            md.update(q.getBytes());
        }
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
