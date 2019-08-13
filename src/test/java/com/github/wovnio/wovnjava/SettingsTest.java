package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.HashMap;
import java.util.ArrayList;

import javax.servlet.FilterConfig;

public class SettingsTest extends TestCase {

    private static FilterConfig mockEmptyConfig() {
        return TestUtil.makeConfig();
    }

    private static FilterConfig mockValidConfig() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("urlPattern", "query");
            put("query", "foo,bar");
            put("apiUrl", "https://example.com/v0/values");
            put("defaultLang", "ja");
            put("supportedLangs", "en,ja");
            put("originalUrlHeader", "REDIRECT_URL");
            put("originalQueryStringHeader", "REDIRECT_QUERY_STRING");
        }};
        return TestUtil.makeConfig(parameters);
    }

    private static FilterConfig mockValidConfigMultipleToken() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "3elW2");
            put("urlPattern", "query");
            put("query", "foo,bar");
            put("apiUrl", "https://example.com/v0/values");
            put("defaultLang", "ja");
            put("supportedLangs", "en,ja");
            put("originalUrlHeader", "REDIRECT_URL");
            put("originalQueryStringHeader", "REDIRECT_QUERY_STRING");
        }};
        return TestUtil.makeConfig(parameters);
    }

    private static FilterConfig mockQueryConfig() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("urlPattern", "query");
        }};
        return TestUtil.makeConfig(parameters);
    }

    // urlPattern is "path".
    public void testSettingsWithEmptyConfig() throws ConfigurationError {
        FilterConfig mock = mockEmptyConfig();
        Settings s = new Settings(mock);

        assertNotNull(s);
        assertEquals("", s.projectToken);
        assertEquals("path", s.urlPattern);
        assertEquals(new ArrayList<String>(), s.query);
        assertEquals("https://wovn.global.ssl.fastly.net/v0/", s.apiUrl);
        assertEquals("en", s.defaultLang);
        ArrayList<String> supportedLangs = new ArrayList<String>();
        supportedLangs.add("en");
        assertEquals(supportedLangs, s.supportedLangs);

        assertEquals("", s.originalUrlHeader);
        assertEquals("", s.originalQueryStringHeader);
    }
    // urlPattern is "subdomain".
    public void testSettingsWithValidConfig() throws ConfigurationError {
        FilterConfig mock = mockValidConfig();
        Settings s = new Settings(mock);

        assertNotNull(s);
        assertEquals("2Wle3", s.projectToken);
        assertEquals("query", s.urlPattern);
        ArrayList<String> query = new ArrayList<String>();
        query.add("foo");
        query.add("bar");
        assertEquals(query, s.query);
        assertEquals("https://example.com/v0/values", s.apiUrl);
        assertEquals("ja", s.defaultLang);
        ArrayList<String> supportedLangs = new ArrayList<String>();
        supportedLangs.add("en");
        supportedLangs.add("ja");
        assertEquals(supportedLangs, s.supportedLangs);

        assertEquals("REDIRECT_URL", s.originalUrlHeader);
        assertEquals("REDIRECT_QUERY_STRING", s.originalQueryStringHeader);
    }
    public void testSettingsWithQueryConfig() throws ConfigurationError {
        FilterConfig mock = mockQueryConfig();
        Settings s = new Settings(mock);

        assertNotNull(s);
        assertEquals("query", s.urlPattern);
    }

    public void testSettings__invalidDefaultLang() throws ConfigurationError {
        HashMap<String, String> parametersWithInvalidDefaultLang = new HashMap<String, String>() {{
            put("projectToken", "2Wle3");
            put("urlPattern", "query");
            put("supportedLangs", "en,ja");
            put("defaultLang", "INVALID");
        }};
        boolean exceptionThrown = false;
        try {
            TestUtil.makeSettings(parametersWithInvalidDefaultLang);
        } catch (ConfigurationError e) {
            exceptionThrown = true;
        }
        assertEquals(true, exceptionThrown);
    }

    public void testSettings__invalidSupportedLangs() throws ConfigurationError {
        HashMap<String, String> parametersWithInvalidSupportedLangs = new HashMap<String, String>() {{
            put("projectToken", "2Wle3");
            put("urlPattern", "query");
            put("defaultLang", "en");
            put("supportedLangs", "en,japan,korean");
        }};
        boolean exceptionThrown = false;
        try {
            TestUtil.makeSettings(parametersWithInvalidSupportedLangs);
        } catch (ConfigurationError e) {
            exceptionThrown = true;
        }
        assertEquals(true, exceptionThrown);
    }

    public void testSettingsWithValidConfigMultipleToken() throws ConfigurationError {
        FilterConfig mock = mockValidConfigMultipleToken();
        Settings s = new Settings(mock);

        assertNotNull(s);
        assertEquals("3elW2", s.projectToken);
        assertEquals("query", s.urlPattern);
        ArrayList<String> query = new ArrayList<String>();
        query.add("foo");
        query.add("bar");
        assertEquals(query, s.query);
        assertEquals("https://example.com/v0/values", s.apiUrl);
        assertEquals("ja", s.defaultLang);
        ArrayList<String> supportedLangs = new ArrayList<String>();
        supportedLangs.add("en");
        supportedLangs.add("ja");
        assertEquals(supportedLangs, s.supportedLangs);

        assertEquals("REDIRECT_URL", s.originalUrlHeader);
        assertEquals("REDIRECT_QUERY_STRING", s.originalQueryStringHeader);
    }

    public void testSettingsWithoutSitePrefix() throws ConfigurationError {
        Settings s = TestUtil.makeSettings();
        assertEquals("", s.sitePrefixPath);
    }

    public void testSettingsWithSitePrefix() throws ConfigurationError {
        HashMap<String, String> option = new HashMap<String, String>();
        option.put("sitePrefixPath", "/global/");
        Settings s = TestUtil.makeSettings(option);
        assertEquals("/global", s.sitePrefixPath);
    }
}
