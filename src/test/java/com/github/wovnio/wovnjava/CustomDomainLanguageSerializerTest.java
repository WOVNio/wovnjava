package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class CustomDomainLanguageSerializerTest extends TestCase {
    public void testDeserialize__SingleLanguage() throws ConfigurationError {
        String input = "site.com:en";
        ArrayList<CustomDomainLanguage> results = CustomDomainLanguageSerializer.deserialize(input).customDomainLanguageList;

        assertEquals(1, results.size());

        assertEquals("en", results.get(0).lang.code);
        assertEquals("site.com", results.get(0).host);
        assertEquals("", results.get(0).path);
    }

    public void testDeserialize__MultipleLanguages() throws ConfigurationError {
        String input = "site.com:en,site.com/ja:ja";
        ArrayList<CustomDomainLanguage> results = CustomDomainLanguageSerializer.deserialize(input).customDomainLanguageList;

        assertEquals(2, results.size());

        assertEquals("ja", results.get(0).lang.code);
        assertEquals("site.com", results.get(0).host);
        assertEquals("/ja", results.get(0).path);

        assertEquals("en", results.get(1).lang.code);
        assertEquals("site.com", results.get(1).host);
        assertEquals("", results.get(1).path);
    }

    public void testDeserialize__MultipleLanguages__DomainAndPathVarieties() throws ConfigurationError {
        String input = "site.co.uk/english:en,japan.site.com/:ja,japan.site.com/ko:ko";
        ArrayList<CustomDomainLanguage> results = CustomDomainLanguageSerializer.deserialize(input).customDomainLanguageList;

        assertEquals(3, results.size());

        assertEquals("en", results.get(0).lang.code);
        assertEquals("site.co.uk", results.get(0).host);
        assertEquals("/english", results.get(0).path);

        assertEquals("ko", results.get(1).lang.code);
        assertEquals("japan.site.com", results.get(1).host);
        assertEquals("/ko", results.get(1).path);

        assertEquals("ja", results.get(2).lang.code);
        assertEquals("japan.site.com", results.get(2).host);
        assertEquals("", results.get(2).path);
    }

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
}
