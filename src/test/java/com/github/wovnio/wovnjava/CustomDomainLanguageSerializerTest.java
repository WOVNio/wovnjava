package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class CustomDomainLanguageSerializerTest extends TestCase {
    public void testDeserialize__SingleLanguage() throws ConfigurationError {
        String input = "site.com:en";
        ArrayList<CustomDomainLanguage> results = CustomDomainLanguageSerializer.deserialize(input);

        assertEquals(1, results.size());

        assertEquals("en", results.get(0).lang.code);
        assertEquals("site.com", results.get(0).host);
        assertEquals("", results.get(0).path);
    }

    public void testDeserialize__MultipleLanguages() throws ConfigurationError {
        String input = "site.com:en,site.com/ja:ja";
        ArrayList<CustomDomainLanguage> results = CustomDomainLanguageSerializer.deserialize(input);

        assertEquals(2, results.size());

        assertEquals("en", results.get(0).lang.code);
        assertEquals("site.com", results.get(0).host);
        assertEquals("", results.get(0).path);

        assertEquals("ja", results.get(1).lang.code);
        assertEquals("site.com", results.get(1).host);
        assertEquals("/ja", results.get(1).path);
    }

    public void testDeserialize__MultipleLanguages__DomainAndPathVarieties__StripTrailingSlashFromPath() throws ConfigurationError {
        String input = "site.co.uk/english/:en,japan.site.com/:ja,japan.site.com/ko:ko";
        ArrayList<CustomDomainLanguage> results = CustomDomainLanguageSerializer.deserialize(input);

        assertEquals(3, results.size());

        assertEquals("en", results.get(0).lang.code);
        assertEquals("site.co.uk", results.get(0).host);
        assertEquals("/english", results.get(0).path);

        assertEquals("ja", results.get(1).lang.code);
        assertEquals("japan.site.com", results.get(1).host);
        assertEquals("", results.get(1).path);

        assertEquals("ko", results.get(2).lang.code);
        assertEquals("japan.site.com", results.get(2).host);
        assertEquals("/ko", results.get(2).path);
    }

    public void testDeserialize__InvalidFormat__ThrowError() throws ConfigurationError {
        String input = "site.com:en,site.com:8000:ja";

        boolean errorThrown = false;
        try {
            CustomDomainLanguageSerializer.deserialize(input);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testDeserialize__InvalidLanguageCode__ThrowError() throws ConfigurationError {
        String input = "site.com:en,site.com:japanese";

        boolean errorThrown = false;
        try {
            CustomDomainLanguageSerializer.deserialize(input);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }
}
