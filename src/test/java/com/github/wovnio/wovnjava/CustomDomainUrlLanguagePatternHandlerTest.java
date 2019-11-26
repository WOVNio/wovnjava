package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class CustomDomainUrlLanguagePatternHandlerTest extends TestCase {
    private Lang english;
    private Lang french;
    private Lang japanese;
    private Lang korean;
    private UrlLanguagePatternHandler sut;

    protected void setUp() throws Exception {
        this.english = Lang.get("en");
        this.french = Lang.get("fr");
        this.japanese = Lang.get("ja");
        this.korean = Lang.get("ko");

        CustomDomainLanguage englishCDL = new CustomDomainLanguage("site.co.uk", "/", this.english);
        CustomDomainLanguage frenchCDL= new CustomDomainLanguage("site.co.uk", "/fr/", this.french);
        CustomDomainLanguage japaneseCDL = new CustomDomainLanguage("japan.site.com", "/", this.japanese);
        CustomDomainLanguage koreanCDL = new CustomDomainLanguage("korean.com", "/ko/", this.korean);

        ArrayList<CustomDomainLanguage> langs = new ArrayList<CustomDomainLanguage>();
        langs.add(englishCDL);
        langs.add(frenchCDL);
        langs.add(japaneseCDL);
        langs.add(koreanCDL);

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

    public void testGetLang__HasMatch__ReturnConvertedUrl() {
        assertEquals(this.english, sut.getLang("http://site.co.uk"));
        assertEquals(this.english, sut.getLang("http://site.co.uk/"));
        assertEquals(this.english, sut.getLang("http://site.co.uk/global/"));
        assertEquals(this.english, sut.getLang("https://site.co.uk/global/graph.png"));
        assertEquals(this.english, sut.getLang("http://site.co.uk?user=tom"));

        assertEquals(this.french, sut.getLang("http://site.co.uk/fr"));
        assertEquals(this.french, sut.getLang("http://site.co.uk/fr/"));
        assertEquals(this.french, sut.getLang("http://site.co.uk/fr/global"));
        assertEquals(this.french, sut.getLang("https://site.co.uk/fr/graph.png?user=tom"));
        assertEquals(this.french, sut.getLang("http://site.co.uk/fr?user=tom"));

        assertEquals(this.japanese, sut.getLang("http://japan.site.com"));
        assertEquals(this.japanese, sut.getLang("http://japan.site.com/"));
        assertEquals(this.japanese, sut.getLang("http://japan.site.com/global"));
        assertEquals(this.japanese, sut.getLang("https://japan.site.com/global/graph.png?user=tom"));
        assertEquals(this.japanese, sut.getLang("http://japan.site.com?user=tom"));

        assertEquals(this.korean, sut.getLang("http://korean.com/ko"));
        assertEquals(this.korean, sut.getLang("http://korean.com/ko/"));
        assertEquals(this.korean, sut.getLang("http://korean.com/ko/global"));
        assertEquals(this.korean, sut.getLang("https://korean.com/ko?user=tom"));
    }

    public void testGetLang__InvalidUrl__ReturnNull() {
        assertEquals(null, sut.getLang(""));
        assertEquals(null, sut.getLang("invalid"));
        assertEquals(null, sut.getLang("http:"));
        assertEquals(null, sut.getLang("site.co.uk"));
        assertEquals(null, sut.getLang("/fr/cat.png"));
        assertEquals(null, sut.getLang("japan.site.com/cat.png"));
    }

    public void testGetLang__NonMatchingUrl__ReturnNull() {
        assertEquals(null, sut.getLang("http://site.com"));
        assertEquals(null, sut.getLang("http://site.com/cat.png"));
        assertEquals(null, sut.getLang("http://japan.site.co.uk/fr/cat.png"));
        assertEquals(null, sut.getLang("http://korean.com"));
        assertEquals(null, sut.getLang("http://korean.com/"));
        assertEquals(null, sut.getLang("http://korean.com/cat.png"));
        assertEquals(null, sut.getLang("http://korean.com?user=tom"));
    }

    public void testConvertToDefaultLanguage__ValidRequest__ReturnUrlInDefaultLanguage() {
        assertEquals("http://site.co.uk", sut.convertToDefaultLanguage("http://site.co.uk"));
        assertEquals("http://site.co.uk/friday/", sut.convertToDefaultLanguage("http://site.co.uk/friday/"));

        assertEquals("http://site.co.uk", sut.convertToDefaultLanguage("http://site.co.uk/fr"));
        assertEquals("http://site.co.uk/", sut.convertToDefaultLanguage("http://site.co.uk/fr/"));
        assertEquals("http://site.co.uk/cat.png", sut.convertToDefaultLanguage("http://site.co.uk/fr/cat.png"));
        assertEquals("http://site.co.uk?user=tom", sut.convertToDefaultLanguage("http://site.co.uk/fr?user=tom"));

        assertEquals("http://site.co.uk", sut.convertToDefaultLanguage("http://japan.site.com"));
        assertEquals("http://site.co.uk/", sut.convertToDefaultLanguage("http://japan.site.com/"));
        assertEquals("http://site.co.uk/cat.png", sut.convertToDefaultLanguage("http://japan.site.com/cat.png"));
        assertEquals("http://site.co.uk?user=tom", sut.convertToDefaultLanguage("http://japan.site.com?user=tom"));

        assertEquals("https://site.co.uk", sut.convertToDefaultLanguage("https://korean.com/ko"));
        assertEquals("https://site.co.uk/", sut.convertToDefaultLanguage("https://korean.com/ko/"));
        assertEquals("https://site.co.uk/cat.png", sut.convertToDefaultLanguage("https://korean.com/ko/cat.png"));
        assertEquals("https://site.co.uk?user=tom", sut.convertToDefaultLanguage("https://korean.com/ko?user=tom"));
    }

    public void testConvertToDefaultLanguage__NonMatchingUrl__DoNotModify() {
        assertEquals("http://korean.com", sut.convertToDefaultLanguage("http://korean.com"));
        assertEquals("http://korean.com/", sut.convertToDefaultLanguage("http://korean.com/"));
        assertEquals("http://korean.com/cat.png", sut.convertToDefaultLanguage("http://korean.com/cat.png"));
        assertEquals("http://korean.com?user=tom", sut.convertToDefaultLanguage("http://korean.com?user=tom"));

        assertEquals("http://example.com", sut.convertToDefaultLanguage("http://example.com"));
        assertEquals("http://example.com/fr", sut.convertToDefaultLanguage("http://example.com/fr"));
        assertEquals("http://example.com/fr/", sut.convertToDefaultLanguage("http://example.com/fr/"));
        assertEquals("http://example.com/fr/cat.png", sut.convertToDefaultLanguage("http://example.com/fr/cat.png"));
        assertEquals("http://example.com/fr?user=tom", sut.convertToDefaultLanguage("http://example.com/fr?user=tom"));
    }
}
