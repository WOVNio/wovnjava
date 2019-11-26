package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class CustomDomainUrlLanguagePatternHandlerTest extends TestCase {
    private Lang english;
    private Lang japanese;
    private Lang french;
    private UrlLanguagePatternHandler sut;

    protected void setUp() throws Exception {
        this.english = Lang.get("en");
        this.japanese = Lang.get("ja");
        this.french = Lang.get("fr");

        CustomDomainLanguage englishCDL = new CustomDomainLanguage("site.co.uk", "/", this.english);
        CustomDomainLanguage japaneseCDL = new CustomDomainLanguage("japan.site.com", "/", this.japanese);
        CustomDomainLanguage frenchCDL= new CustomDomainLanguage("site.co.uk", "/fr/", this.french);

        ArrayList<CustomDomainLanguage> langs = new ArrayList<CustomDomainLanguage>();
        langs.add(englishCDL);
        langs.add(japaneseCDL);
        langs.add(frenchCDL);

        CustomDomainLanguages customDomainLanguages = new CustomDomainLanguages(langs);
        this.sut = new CustomDomainUrlLanguagePatternHandler(this.english, customDomainLanguages);
        /*
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "customDomain");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
            put("customDomainLangs", "site.co.uk:en,japan.site.com:ja,site.co.uk/fr:fr");
        }});
        this.sut = UrlLanguagePatternHandlerFactory.create(settings);
        */
    }

    public void testGetLang__non() {
        assertEquals(null, sut.getLang("invalid"));
        assertEquals(this.english, sut.getLang("http://site.co.uk/global/cat.png"));
    }
}
