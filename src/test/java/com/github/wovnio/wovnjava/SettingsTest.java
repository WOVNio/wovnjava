package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.HashMap;
import java.util.ArrayList;

import javax.servlet.FilterConfig;

public class SettingsTest extends TestCase {

    /*
    private static FilterConfig mockEmptyConfig() {
        return TestUtil.makeConfig();
    }

    private static FilterConfig mockValidConfig() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("userToken", "2Wle3");
            put("projectToken", "2Wle3");
            put("urlPattern", "query");
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
    */

    private static FilterConfig makeConfigWithValidBase(HashMap<String, String> extraValues) {
        HashMap<String, String> settings = new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }};
        settings.putAll(extrValues);
        return TestUtil.makeConfig(settings);
    }

    public void testSettings__MinimalConfiguration__DefaultValuesOK() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }});
        Settings s = new Settings(config);

        assertEquals("123456", s.projectToken);
        assertEquals("path", s.urlPattern);
        assertEquals("en", s.defaultLang);

        ArrayList<String> expectedSupportedLangs = new ArrayList<String>();
        expectedSupportedLangs.add("en");
        expectedSupportedLangs.add("ja");
        assertEquals(expectedSupportedLangs, s.supportedLangs);

        assertEquals(false, s.devMode);
        assertEquals(false, s.debugMode);
        assertEquals(false, s.useProxy);
        assertEquals(false, s.enableFlushBuffer);

        assertEquals("", s.sitePrefixPath);
        assertEquals("", s.originalUrlHeader);
        assertEquals("", s.originalQueryStringHeader);

        ArrayList<String> emptyArrayList = new ArrayList<String>();
        assertEquals(emptyArrayList, s.ignoreClasses);

        assertEquals(Settings.DefaultSnippetUrlProduction, s.snippetUrl);
        assertEquals(Settings.DefaultApiUrlProduction, s.apiUrl);

        assertEquals(Settings.DefaultTimeout, s.connectTimeout);
        assertEquals(Settings.DefaultTimeout, s.readTimeout);
    }

    public void testSettings__MissingProjectToken__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }});
        boolean errorThrown = false;
        try {
            Settings s = new Settings(config);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testSettings__MissingUrlPattern__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }});
        boolean errorThrown = false;
        try {
            Settings s = new Settings(config);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testSettings__MissingDefaultLang__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("supportedLangs", "en,ja");
        }});
        boolean errorThrown = false;
        try {
            Settings s = new Settings(config);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testSettings__InvalidDefaultLangCode__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "English");
            put("supportedLangs", "en,ja");
        }});
        boolean errorThrown = false;
        try {
            Settings s = new Settings(config);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testSettings__MissingSupportedLangs__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
        }});
        boolean errorThrown = false;
        try {
            Settings s = new Settings(config);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testSettings__InvalidSupportedLangCode__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,Japanese");
        }});
        boolean errorThrown = false;
        try {
            Settings s = new Settings(config);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testSettings__LegacyUserTokenDeclared__SetProjectTokenAsUserToken() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("userToken", "98765");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }});
        Settings s = new Settings(config);

        assertEquals("98765", s.projectToken);
    }

    public void testSettings__BothProjectTokenAndUserTokenDeclared__PreferProjectToken() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("userToken", "98765");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }});
        Settings s = new Settings(config);

        assertEquals("123456", s.projectToken);
    }

    public void testSettings__DefaultLangNotInSupportedLangs__AddDefaultLangToList() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "ja");
        }});
        Settings s = new Settings(config);

        ArrayList<String> expectedSupportedLangs = new ArrayList<String>();
        expectedSupportedLangs.add("ja");
        expectedSupportedLangs.add("en");
        assertEquals(expectedSupportedLangs, s.supportedLangs);
    }

    /*

    // urlPattern is "path".
    public void testSettingsWithEmptyConfig() throws ConfigurationError {
        FilterConfig mock = mockEmptyConfig();
        Settings s = new Settings(mock);

        assertNotNull(s);
        assertEquals("", s.projectToken);
        assertEquals("path", s.urlPattern);
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
    */
}
