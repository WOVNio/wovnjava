package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;

public class CustomDomainLanguageValidatorTest extends TestCase {
    private Lang english;
    private Lang japanese;
    private Lang korean;
    private Lang french;

    private ArrayList<Lang> supportedLangs;

    protected void setUp() throws Exception {
        this.english = Lang.get("en");
        this.japanese = Lang.get("ja");
        this.korean = Lang.get("ko");
        this.french = Lang.get("fr");

        this.supportedLangs = new ArrayList<Lang>();
        this.supportedLangs.add(this.english);
        this.supportedLangs.add(this.japanese);
        this.supportedLangs.add(this.korean);
    }

    public void testValidate__ValidConfiguration__ResultSuccess() throws ConfigurationError {
        ArrayList<CustomDomainLanguage> customDomainLanguageList = new ArrayList<CustomDomainLanguage>();
        customDomainLanguageList.add(new CustomDomainLanguage("site.com", "", this.english));
        customDomainLanguageList.add(new CustomDomainLanguage("site.com", "/ja", this.japanese));
        customDomainLanguageList.add(new CustomDomainLanguage("korea.site.com", "/ko", this.korean));

        ValidationResult result = CustomDomainLanguageValidator.validate(customDomainLanguageList, this.supportedLangs);
        assertEquals(true, result.success);
    }

    public void testValidate__SameLanguageSpecifiedMoreThanOnce__ThrowError() throws ConfigurationError {
        ArrayList<CustomDomainLanguage> customDomainLanguageList = new ArrayList<CustomDomainLanguage>();
        customDomainLanguageList.add(new CustomDomainLanguage("site.com", "/english", this.english));
        customDomainLanguageList.add(new CustomDomainLanguage("japan.site.com", "", this.japanese));
        customDomainLanguageList.add(new CustomDomainLanguage("site.com", "/ja", this.japanese));

        ValidationResult result = CustomDomainLanguageValidator.validate(customDomainLanguageList, this.supportedLangs);

        assertEquals(false, result.success);
    }

    public void testValidate__TwoLanguagesWithSameDomainAndPath__ThrowError() throws ConfigurationError {
        ArrayList<CustomDomainLanguage> customDomainLanguageList = new ArrayList<CustomDomainLanguage>();
        customDomainLanguageList.add(new CustomDomainLanguage("site.com", "/english", this.english));
        customDomainLanguageList.add(new CustomDomainLanguage("japan.site.com", "", this.japanese));
        customDomainLanguageList.add(new CustomDomainLanguage("japan.site.com", "", this.korean));

        ValidationResult result = CustomDomainLanguageValidator.validate(customDomainLanguageList, this.supportedLangs);

        assertEquals(false, result.success);
    }

    public void testValidate__CustomDomainLangsNotDeclaredForAllSupportedLangs__ThrowError() throws ConfigurationError {
        ArrayList<CustomDomainLanguage> customDomainLanguageList = new ArrayList<CustomDomainLanguage>();
        customDomainLanguageList.add(new CustomDomainLanguage("site.com", "/english", this.english));
        customDomainLanguageList.add(new CustomDomainLanguage("japan.site.com", "", this.japanese));

        ValidationResult result = CustomDomainLanguageValidator.validate(customDomainLanguageList, this.supportedLangs);

        assertEquals(false, result.success);
    }

    public void testValidate__CustomDomainLangDeclaredForExtraLanguageNotInSupportedLangs__ThrowError() throws ConfigurationError {
        ArrayList<CustomDomainLanguage> customDomainLanguageList = new ArrayList<CustomDomainLanguage>();
        customDomainLanguageList.add(new CustomDomainLanguage("site.com", "/english", this.english));
        customDomainLanguageList.add(new CustomDomainLanguage("japan.site.com", "", this.japanese));
        customDomainLanguageList.add(new CustomDomainLanguage("korea.site.com", "", this.japanese));
        customDomainLanguageList.add(new CustomDomainLanguage("france.site.com", "/fr", this.french));

        ValidationResult result = CustomDomainLanguageValidator.validate(customDomainLanguageList, this.supportedLangs);

        assertEquals(false, result.success);
    }

    public void testValidate__CustomDomainLangDeclaredForDifferentLanguagesFromSupportedLangs__ThrowError() throws ConfigurationError {
        ArrayList<CustomDomainLanguage> customDomainLanguageList = new ArrayList<CustomDomainLanguage>();
        customDomainLanguageList.add(new CustomDomainLanguage("site.com", "/english", this.english));
        customDomainLanguageList.add(new CustomDomainLanguage("japan.site.com", "", this.japanese));
        customDomainLanguageList.add(new CustomDomainLanguage("france.site.com", "/fr", this.french));

        ValidationResult result = CustomDomainLanguageValidator.validate(customDomainLanguageList, this.supportedLangs);

        assertEquals(false, result.success);
    }
}
