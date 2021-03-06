package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.FilterConfig;

public class SettingsTest extends TestCase {
    private Lang english;
    private Lang japanese;

    protected void setUp() throws Exception {
        this.english = Lang.get("en");
        this.japanese = Lang.get("ja");
    }

    public void testDefaultSettings__MinimalConfiguration__DefaultValuesOK() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }});
        Settings s = new Settings(config);

        assertEquals("123456", s.projectToken);
        assertEquals("path", s.urlPattern);
        assertEquals(this.english, s.defaultLang);

        ArrayList<Lang> expectedSupportedLangs = new ArrayList<Lang>();
        expectedSupportedLangs.add(this.english);
        expectedSupportedLangs.add(this.japanese);
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
        assertEquals(emptyArrayList, s.ignorePaths);

        assertEquals(Settings.DefaultSnippetUrlProduction, s.snippetUrl);
        assertEquals(Settings.DefaultApiUrlProduction, s.apiUrl);

        assertEquals(Settings.DefaultTimeout, s.connectTimeout);
        assertEquals(Settings.DefaultTimeout, s.readTimeout);
    }

    public void testRequiredSettings__MissingProjectToken__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }});

        assertErrorThrown(config);
    }

    public void testRequiredSettings__MissingUrlPattern__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }});

        assertErrorThrown(config);
    }

    public void testRequiredSettings__MissingDefaultLang__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("supportedLangs", "en,ja");
        }});

        assertErrorThrown(config);
    }

    public void testRequiredSettings__InvalidDefaultLangCode__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "English");
            put("supportedLangs", "en,ja");
        }});

        assertErrorThrown(config);
    }

    public void testRequiredSettings__MissingSupportedLangs__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
        }});

        assertErrorThrown(config);
    }

    public void testRequiredSettings__InvalidSupportedLangCode__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,Japanese");
        }});

        assertErrorThrown(config);
    }

    public void testRequiredSettings__SupportedLangDeclaredMultipleTimes__throwError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,ja");
        }});
        boolean errorThrown = false;
        try {
            Settings s = new Settings(config);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testRequiredSettings__LegacyUserTokenDeclared__SetProjectTokenAsUserToken() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("userToken", "98765");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja");
        }});
        Settings s = new Settings(config);

        assertEquals("98765", s.projectToken);
    }

    public void testRequiredSettings__BothProjectTokenAndUserTokenDeclared__PreferProjectToken() throws ConfigurationError {
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

    public void testRequiredSettings__DefaultLangNotInSupportedLangs__AddDefaultLangToList() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("projectToken", "123456");
            put("urlPattern", "path");
            put("defaultLang", "en");
            put("supportedLangs", "ja");
        }});
        Settings s = new Settings(config);

        ArrayList<Lang> expectedSupportedLangs = new ArrayList<Lang>();
        expectedSupportedLangs.add(this.japanese);
        expectedSupportedLangs.add(this.english);
        assertEquals(expectedSupportedLangs, s.supportedLangs);
    }

    public void testSettings__ConfigWithValidBase__DoesNotRaiseError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
        }});
        Settings s = new Settings(config);
        assertEquals(true, true); // no error
    }

    public void testBooleanSettings__CorrectlyIdentifyTrue() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("devMode", "true");
            put("debugMode", "on");
            put("useProxy", "1");
            put("enableFlushBuffer", "false");
        }});
        Settings s = new Settings(config);
        assertEquals(true, s.devMode);
        assertEquals(true, s.debugMode);
        assertEquals(true, s.useProxy);
        assertEquals(false, s.enableFlushBuffer);
    }

    public void testSitePrefixPath__DeclareEmptyString__UseEmptyString() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("sitePrefixPath", "");
        }});
        Settings s = new Settings(config);
        assertEquals("", s.sitePrefixPath);
    }

    public void testSitePrefixPath__DeclareEmptySlash__UseEmptyString() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("sitePrefixPath", "/");
        }});
        Settings s = new Settings(config);
        assertEquals("", s.sitePrefixPath);
    }

    public void testSitePrefixPath__DeclareStringWithoutSlashes() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("sitePrefixPath", "global");
        }});
        Settings s = new Settings(config);
        assertEquals("/global", s.sitePrefixPath);
    }

    public void testSitePrefixPath__DeclareStringWithLeadingSlash() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("sitePrefixPath", "/global");
        }});
        Settings s = new Settings(config);
        assertEquals("/global", s.sitePrefixPath);
    }

    public void testSitePrefixPath__DeclareStringWithTrailingSlash() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("sitePrefixPath", "global/");
        }});
        Settings s = new Settings(config);
        assertEquals("/global", s.sitePrefixPath);
    }

    public void testSitePrefixPath__DeclareStringWithLeadingAndTrailingSlash() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("sitePrefixPath", "/global/");
        }});
        Settings s = new Settings(config);
        assertEquals("/global", s.sitePrefixPath);
    }

    public void testSitePrefixPath__DeclareDeepPath() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("sitePrefixPath", "deep/pre/fix");
        }});
        Settings s = new Settings(config);
        assertEquals("/deep/pre/fix", s.sitePrefixPath);
    }

    public void testApiUrl__ProductionMode__DefaultValue() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("devMode", "false");
        }});
        Settings s = new Settings(config);
        assertEquals(Settings.DefaultApiUrlProduction, s.apiUrl);
    }

    public void testApiUrl__ProductionMode__OverrideValue() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("devMode", "false");
            put("apiUrl", "http://test.test");
        }});
        Settings s = new Settings(config);
        assertEquals("http://test.test", s.apiUrl);
    }

    public void testApiUrl__DevelopmentMode__DefaultValue() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("devMode", "true");
        }});
        Settings s = new Settings(config);
        assertEquals(Settings.DefaultApiUrlDevelopment, s.apiUrl);
    }

    public void testApiUrl__DevelopmentMode__OverrideValue() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("devMode", "true");
            put("apiUrl", "http://test.test");
        }});
        Settings s = new Settings(config);
        assertEquals("http://test.test", s.apiUrl);
    }

    public void testSnippetUrl__ProductionMode__DefaultValue() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("devMode", "false");
        }});
        Settings s = new Settings(config);
        assertEquals(Settings.DefaultSnippetUrlProduction, s.snippetUrl);
    }

    public void testSnippetUrl__DevelopmentMode__DefaultValue() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("devMode", "true");
        }});
        Settings s = new Settings(config);
        assertEquals(Settings.DefaultSnippetUrlDevelopment, s.snippetUrl);
    }

    public void testIgnorePaths__DeclareEmptyString__UseDefaultEmptyArray() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("ignorePaths", "");
        }});
        Settings s = new Settings(config);
        ArrayList<String> emptyArrayList = new ArrayList<String>();
        assertEquals(emptyArrayList, s.ignorePaths);
    }

    public void testIgnorePaths__emptyValues__areExcluded() throws ConfigurationError {
        Settings s = createSettings(new HashMap<String, String>() {{
            put("ignorePaths", ",,,");
        }});
        ArrayList<String> emptyArrayList = new ArrayList<String>();
        assertEquals(emptyArrayList, s.ignorePaths);
    }

    public void testIgnorePaths__pathsWithoutLeadingSlash__slashAdded() throws ConfigurationError {
        Settings s = createSettings(new HashMap<String, String>() {{
            put("ignorePaths", ",path1/,path2/,pa/th3/");
        }});
        ArrayList<String> expectedArrayList = new ArrayList<String>(Arrays.asList("/path1", "/path2", "/pa/th3"));
        assertEquals(expectedArrayList, s.ignorePaths);
    }

    public void testIgnorePaths__pathsWithTrailingSlash__slashRemoved() throws ConfigurationError {
        Settings s = createSettings(new HashMap<String, String>() {{
            put("ignorePaths", ",/path1/,/path2/,/pa/th3/");
        }});
        ArrayList<String> expectedArrayList = new ArrayList<String>(Arrays.asList("/path1", "/path2", "/pa/th3"));
        assertEquals(expectedArrayList, s.ignorePaths);
    }

    public void testIgnoreClasses__DeclareEmptyString__UseDefaultEmptyArray() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("ignoreClasses", "");
        }});
        Settings s = new Settings(config);
        ArrayList<String> emptyArrayList = new ArrayList<String>();
        assertEquals(emptyArrayList, s.ignoreClasses);
    }

    public void testIgnoreClasses__DeclareSimpleString() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("ignoreClasses", "ignore-me");
        }});
        Settings s = new Settings(config);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("ignore-me");
        assertEquals(arrayList, s.ignoreClasses);
    }

    public void testIgnoreClasses__DeclareCommaSeparatedStrings() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("ignoreClasses", "ignore-me,svgicon,user-name");
        }});
        Settings s = new Settings(config);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("ignore-me");
        arrayList.add("svgicon");
        arrayList.add("user-name");
        assertEquals(arrayList, s.ignoreClasses);
    }

    public void testOriginalUrlHeader__AcceptSetting() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("originalUrlHeader", "X-Netflix-Original-Url");
        }});
        Settings s = new Settings(config);
        assertEquals("X-Netflix-Original-Url", s.originalUrlHeader);
    }

    public void testOriginalQueryStringHeader__AcceptSetting() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("originalQueryStringHeader", "X-Netflix-Original-Query-String");
        }});
        Settings s = new Settings(config);
        assertEquals("X-Netflix-Original-Query-String", s.originalQueryStringHeader);
    }

    public void testTimeouts__ValidPositiveInteger__AcceptSetting() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("connectTimeout", "1500");
            put("readTimeout", "1");
        }});
        Settings s = new Settings(config);
        assertEquals(1500, s.connectTimeout);
        assertEquals(1, s.readTimeout);
    }

    public void testTimeouts__InvalidNumberRange__UseDefaultValue() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("connectTimeout", "-20");
            put("readTimeout", "0");
        }});
        Settings s = new Settings(config);
        assertEquals(Settings.DefaultTimeout, s.connectTimeout);
        assertEquals(Settings.DefaultTimeout, s.readTimeout);
    }

    public void testCustomDomainLangs__SuccessfullyParseCustomDomainLangs() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
            put("customDomainLangs", "site.com:en,site.com/ja:ja,france.com/:fr");
        }});
        Settings s = new Settings(config);
        ArrayList<CustomDomainLanguage> customDomainLanguageList = s.customDomainLanguages.customDomainLanguageList;

        assertEquals("ja", customDomainLanguageList.get(0).lang.code);
        assertEquals("site.com", customDomainLanguageList.get(0).host);
        assertEquals("/ja", customDomainLanguageList.get(0).path);

        assertEquals("en", customDomainLanguageList.get(1).lang.code);
        assertEquals("site.com", customDomainLanguageList.get(1).host);
        assertEquals("", customDomainLanguageList.get(1).path);
    }

    public void testConnectTimeout__InvalidInteger__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("connectTimeout", "hoge");
        }});

        assertErrorThrown(config);
    }

    public void testReadTimeout__InvalidInteger__ThrowError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("readTimeout", "");
        }});

        assertErrorThrown(config);
    }

    public void testOutboundProxyHost__Empty_ThrowsError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("outboundProxyHost", "");
        }});

        assertErrorThrown(config);
    }

    public void testOutboundProxyHost__NonEmpty_AcceptSetting() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("outboundProxyHost", "proxy.com");
        }});

        Settings settings = new Settings(config);

        assertEquals("proxy.com", settings.outboundProxyHost);
    }

    public void testOutboundProxyPort__Empty_ThrowsError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("outboundProxyPort", "");
        }});

        assertErrorThrown(config);
    }

    public void testOutboundProxyPort__NonInteger_ThrowsError() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("outboundProxyPort", "abc");
        }});
        assertErrorThrown(config);
    }

    public void testOutboundProxyPort__Integer_AcceptSetting() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfigWithValidDefaults(new HashMap<String, String>() {{
            put("outboundProxyPort", "4000");
        }});

        Settings settings = new Settings(config);

        assertEquals(4000, settings.outboundProxyPort);
    }

    private void assertErrorThrown(FilterConfig config) {
        boolean errorThrown = false;
        try {
            Settings s = new Settings(config);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    private Settings createSettings(HashMap<String, String> values) throws ConfigurationError {
        return new Settings(TestUtil.makeConfigWithValidDefaults(values));
    }
}
