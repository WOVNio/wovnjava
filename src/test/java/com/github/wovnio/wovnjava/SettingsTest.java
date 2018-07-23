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
            put("secretKey", "secret");
            put("urlPattern", "query");
            put("urlPatternReg", "aaa");
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
            put("secretKey", "secret");
            put("urlPattern", "query");
            put("urlPatternReg", "aaa");
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
            put("secretKey", "secret");
            put("urlPattern", "query");
        }};
        return TestUtil.makeConfig(parameters);
    }

    // urlPattern is "path".
    public void testSettingsWithEmptyConfig() {
        FilterConfig mock = mockEmptyConfig();
        Settings s = new Settings(mock);

        assertNotNull(s);
        assertEquals("", s.projectToken);
        assertEquals("", s.secretKey);
        assertEquals("path", s.urlPattern);
        assertEquals(Settings.UrlPatternRegPath, s.urlPatternReg);
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
    public void testSettingsWithValidConfig() {
        FilterConfig mock = mockValidConfig();
        Settings s = new Settings(mock);

        assertNotNull(s);
        assertEquals("2Wle3", s.projectToken);
        assertEquals("secret", s.secretKey);
        assertEquals("query", s.urlPattern);
        assertEquals(Settings.UrlPatternRegQuery, s.urlPatternReg);
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
    public void testSettingsWithQueryConfig() {
        FilterConfig mock = mockQueryConfig();
        Settings s = new Settings(mock);

        assertNotNull(s);
        assertEquals("query", s.urlPattern);
        assertEquals(Settings.UrlPatternRegQuery, s.urlPatternReg);
    }

    public void testIsValidWithEmptyConfig() {
        FilterConfig mock = mockEmptyConfig();
        Settings s = new Settings(mock);
        assertFalse(s.isValid());
    }
    public void testIsValidWithValidConfig() {
        FilterConfig mock = mockValidConfig();
        Settings s = new Settings(mock);
        assertTrue(s.isValid());
    }

    public void testGetBoolParameter() {
        assertTrue(Settings.getBoolParameter("on"));
        assertTrue(Settings.getBoolParameter("true"));
        assertTrue(Settings.getBoolParameter("1"));

        assertFalse(Settings.getBoolParameter(null));
        assertFalse(Settings.getBoolParameter(""));
        assertFalse(Settings.getBoolParameter("0"));
    }

    public void testGetArrayParameterWithoutComma() {
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("foo");
        assertEquals(expected, Settings.getArrayParameter("foo"));
    }
    public void testGetArrayParameterWithComma() {
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("foo");
        expected.add("bar");
        expected.add("baz");
        assertEquals(expected, Settings.getArrayParameter("foo,bar,baz"));
    }
    public void testGetArrayParameterWithNull() {
        assertNull(Settings.getArrayParameter(null));
    }
    public void testGetArrayParameterWithEmptyString() {
        assertNull(Settings.getArrayParameter(""));
    }

    public void testGetIntParamterWithInvalidString() {
        assertEquals(0, Settings.getIntParameter(null));
        assertEquals(0, Settings.getIntParameter(""));
        assertEquals(0, Settings.getIntParameter("a"));
        assertEquals(0, Settings.getIntParameter("3.14"));
    }
    public void testGetIntParameter() {
        assertEquals(0, Settings.getIntParameter("0"));
        assertEquals(1, Settings.getIntParameter("1"));
        assertEquals(2, Settings.getIntParameter("2"));
        assertEquals(13, Settings.getIntParameter("13"));
    }

    public void testSettingsWithValidConfigMultipleToken() {
        FilterConfig mock = mockValidConfigMultipleToken();
        Settings s = new Settings(mock);

        assertNotNull(s);
        assertEquals("3elW2", s.projectToken);
        assertEquals("secret", s.secretKey);
        assertEquals("query", s.urlPattern);
        assertEquals(Settings.UrlPatternRegQuery, s.urlPatternReg);
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

    public void testSettingsWithoutSitePrefix() {
        Settings s = TestUtil.makeSettings();
        assertFalse(s.hasSitePrefixPath);
        assertEquals("/", s.sitePrefixPathWithSlash);
        assertEquals("", s.sitePrefixPathWithoutSlash);
    }

    public void testSettingsWithSitePrefix() {
        HashMap<String, String> option = new HashMap<String, String>();
        option.put("sitePrefixPath", "/global/");
        Settings s = TestUtil.makeSettings(option);
        assertTrue(s.hasSitePrefixPath);
        assertEquals("/global/", s.sitePrefixPathWithSlash);
        assertEquals("/global", s.sitePrefixPathWithoutSlash);
    }
}
