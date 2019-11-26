package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class CustomDomainLanguagesTest extends TestCase {
    private CustomDomainLanguage french;
    private CustomDomainLanguage japanese;
    private CustomDomainLanguage german;
    private CustomDomainLanguage english;

    private CustomDomainLanguages sut;

    protected void setUp() throws Exception {
        this.french = new CustomDomainLanguage("foo.com", "/", Lang.get("fr"));
        this.japanese = new CustomDomainLanguage("foo.com", "/path", Lang.get("ja"));
        this.german = new CustomDomainLanguage("foo.com", "/dir/path", Lang.get("de"));
        this.english = new CustomDomainLanguage("english.foo.com", "/", Lang.get("en"));

        ArrayList<CustomDomainLanguage> languageList = new ArrayList<CustomDomainLanguage>();
        languageList.add(this.french);
        languageList.add(this.japanese);
        languageList.add(this.german);
        languageList.add(this.english);
        this.sut = new CustomDomainLanguages(languageList);
    }

    public void testGetCustomDomainLanguageByLang__UnknownLanguage__ReturnNull() throws MalformedURLException {
        assertEquals(null, this.sut.getCustomDomainLanguageByLang(null));
    }

    public void testGetCustomDomainLanguageByLang__LanguageExists__ReturnMatch() throws MalformedURLException {
        assertEquals(this.french, this.sut.getCustomDomainLanguageByLang(Lang.get("fr")));
        assertEquals(this.japanese, this.sut.getCustomDomainLanguageByLang(Lang.get("ja")));
        assertEquals(this.german, this.sut.getCustomDomainLanguageByLang(Lang.get("de")));
        assertEquals(this.english, this.sut.getCustomDomainLanguageByLang(Lang.get("en")));
    }

    public void testGetCustomDomainLanguageByUrl__UrlMatchesDomain__ReturnsMatch() throws MalformedURLException {
        assertEquals(this.french, this.sut.getCustomDomainLanguageByUrl(new URL("http://foo.com")));
        assertEquals(this.french, this.sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/")));
        assertEquals(this.french, this.sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/test.html")));

        assertEquals(this.japanese, this.sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/path")));
        assertEquals(this.japanese, this.sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/path/")));
        assertEquals(this.japanese, this.sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/path/test.html")));

        assertEquals(this.german, this.sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/dir/path")));
        assertEquals(this.german, this.sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/dir/path/")));
        assertEquals(this.german, this.sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/dir/path/test.html")));

        assertEquals(this.english, this.sut.getCustomDomainLanguageByUrl(new URL("http://english.foo.com")));
        assertEquals(this.english, this.sut.getCustomDomainLanguageByUrl(new URL("http://english.foo.com/dir/path")));
        assertEquals(this.english, this.sut.getCustomDomainLanguageByUrl(new URL("http://english.foo.com/dir/path/")));
        assertEquals(this.english, this.sut.getCustomDomainLanguageByUrl(new URL("http://english.foo.com/dir/path/test.html")));
    }

    public void testGetCustomDomainLanguageByUrl__NestedPaths__ReturnsMatch() throws MalformedURLException {
        CustomDomainLanguage japanese = new CustomDomainLanguage("foo.com", "/path", Lang.get("ja"));
        CustomDomainLanguage english = new CustomDomainLanguage("foo.com", "/path/en", Lang.get("en"));
        CustomDomainLanguage french = new CustomDomainLanguage("foo.com", "/path/fr", Lang.get("fr"));

        ArrayList<CustomDomainLanguage> languageList = new ArrayList<CustomDomainLanguage>();
        languageList.add(french);
        languageList.add(japanese);
        languageList.add(english);
        CustomDomainLanguages sut = new CustomDomainLanguages(languageList);

        assertEquals(japanese, sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/path")));
        assertEquals(english, sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/path/en")));
        assertEquals(french, sut.getCustomDomainLanguageByUrl(new URL("http://foo.com/path/fr")));
    }
}
