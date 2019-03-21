package com.github.wovnio.wovnjava;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.FilterConfig;
import javax.xml.bind.DatatypeConverter;

class Settings {
    public static final String VERSION = "0.4.1";
    static final String UrlPatternRegPath = "/([^/.?]+)";
    static final String UrlPatternRegQuery = "(?:(?:\\?.*&)|\\?)wovn=([^&]+)(?:&|$)";
    static final String UrlPatternRegSubdomain = "^([^.]+)\\.";

    String projectToken = "";
    boolean hasSitePrefixPath = false;
    String sitePrefixPathWithSlash = "/";
    String sitePrefixPathWithoutSlash = "";
    String secretKey = "";
    String urlPattern = "path";
    String urlPatternReg = UrlPatternRegPath;
    ArrayList<String> query;
    String snippetUrl = "//j.wovn.io/1";
    String apiUrl = "https://wovn.global.ssl.fastly.net/v0/";
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

    Settings(FilterConfig config) {
        super();

        this.query = new ArrayList<String>();
        this.supportedLangs = new ArrayList<String>();
        this.supportedLangs.add("en");
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
        this.hasSitePrefixPath = p != null && p.length() > 0;
        if (this.hasSitePrefixPath) {
            if (p.endsWith("/")) {
                this.sitePrefixPathWithSlash = p;
                this.sitePrefixPathWithoutSlash = p.substring(0, p.length() - 1);
            } else {
                this.sitePrefixPathWithSlash = p + "/";
                this.sitePrefixPathWithoutSlash = p;
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

        p = config.getInitParameter("urlPatternReg");
        if (p != null && p.length() > 0) {
            this.urlPatternReg = p;
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

    private void initialize() {
        this.defaultLang = Lang.getCode(this.defaultLang);

        if (this.supportedLangs.size() == 0) {
            this.supportedLangs.add(this.defaultLang);
        }

        if (this.urlPattern.equals("path")) {
            this.urlPatternReg = UrlPatternRegPath;
            String prefix = this.sitePrefixPathWithoutSlash;
            if (prefix.length() > 0 && !this.urlPatternReg.contains(prefix)) {
                this.urlPatternReg = prefix + UrlPatternRegPath;
            }
        } else if (this.urlPattern.equals("query")) {
            this.urlPatternReg = UrlPatternRegQuery;
        } else if (this.urlPattern.equals("subdomain")) {
            this.urlPatternReg = UrlPatternRegSubdomain;
        }

        if (this.devMode) {
          this.snippetUrl = "//j.dev-wovn.io:3000/1";
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

        if (errors.size() > 0) {
            Logger.log.error("Settings is invalid: " + errors.toString());
        }

        return valid;
    }

    String hash() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(projectToken.getBytes());
        md.update(urlPattern.getBytes());
        md.update(urlPatternReg.getBytes());
        for (String q : query) {
            md.update(q.getBytes());
        }
        md.update(sitePrefixPathWithSlash.getBytes());
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
