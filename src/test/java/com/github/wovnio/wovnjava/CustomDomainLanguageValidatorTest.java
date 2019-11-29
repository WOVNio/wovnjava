package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class CustomDomainLanguageValidatorTest extends TestCase {
    private ArrayList<Lang> supportedLangs;

    protected void setUp() throws Exception {
        this.supportedLangs = new ArrayList<Lang>();
        this.supportedLangs.add(Lang.get("en"));
        this.supportedLangs.add(Lang.get("ja"));
        this.supportedLangs.add(Lang.get("ko"));
    }

    public void testValidate__ValidConfiguration__ResultSuccess() throws ConfigurationError {
        ArrayList<CustomDomainLanguage> customDomainLanguageList = new ArrayList<CustomDomainLanguage>();
        customDomainLanguageList.add(new CustomDomainLanguage("foo.com", "", Lang.get("en")));
        customDomainLanguageList.add(new CustomDomainLanguage("foo.com", "/ja", Lang.get("ja")));
        customDomainLanguageList.add(new CustomDomainLanguage("korea.foo.com", "/ko", Lang.get("ko")));

        ValidationResult result = CustomDomainLanguageValidator.validate(customDomainLanguageList, this.supportedLangs);
        assertEquals(true, result.success);
    }

    /*
    public void testDeserialize__SameLanguageSpecifiedMoreThanOnce__ThrowError() throws ConfigurationError {
        String input = "site.com/english:en,japan.site.com/:ja,site.com/ja:ja";

        boolean errorThrown = false;
        try {
            CustomDomainLanguageSerializer.deserialize(input);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testDeserialize__TwoLanguagesWithSameDomainAndPath__ThrowError() throws ConfigurationError {
        String input = "site.com/english:en,site.com:ja,site.com:ko";

        boolean errorThrown = false;
        try {
            CustomDomainLanguageSerializer.deserialize(input);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testDeserialize__DomainAndPathDifferingOnlyByTrailingSlash__ThrowError() throws ConfigurationError {
        String input = "site.com/global:en,site.com:ja,site.com/global/:ko";

        boolean errorThrown = false;
        try {
            CustomDomainLanguageSerializer.deserialize(input);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testCreate__UrlPatternCustomDomain__CustomDomainLangsNotDeclaredForAllSupportedLangs__ThrowError() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "customDomain");
            put("sitePrefixPath", "");
            put("customDomainLangs", "site.com:en,site.co.jp:ja");
            put("supportedLangs", "en,ja,ko");
        }});
        boolean errorThrown = false;
        try {
            UrlLanguagePatternHandlerFactory.create(settings);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testCreate__UrlPatternCustomDomain__CustomDomainLangDeclaredForLanguageNotInSupportedLangs__ThrowError() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "customDomain");
            put("sitePrefixPath", "");
            put("customDomainLangs", "site.com:en,site.co.jp:ja,site.kr:ko");
            put("supportedLangs", "en,ja");
        }});
        boolean errorThrown = false;
        try {
            UrlLanguagePatternHandlerFactory.create(settings);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testCreate__InvalidUrlPattern__ThrowError() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "invalid");
        }});
        boolean errorThrown = false;
        try {
            UrlLanguagePatternHandlerFactory.create(settings);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }
    */
}
