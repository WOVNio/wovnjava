package com.github.wovnio.wovnjava;

import java.util.Map;
import java.util.LinkedHashMap;

import junit.framework.TestCase;

public class LanguageAliasSerializerTest extends TestCase {
    public void testDeserializeFilterConfig__EmptyInput() throws ConfigurationError {
        Map<Lang, String> result = LanguageAliasSerializer.deserializeFilterConfig(null);
        assertEquals(0, result.size());
    }

    public void testDeserializeFilterConfig__NullInput() throws ConfigurationError {
        Map<Lang, String> result = LanguageAliasSerializer.deserializeFilterConfig("");
        assertEquals(0, result.size());
    }

    public void testDeserializeFilterConfig__SingleAlias() throws ConfigurationError {
        Map<Lang, String> result = LanguageAliasSerializer.deserializeFilterConfig("ja:japan");
        assertEquals(1, result.size());
        assertEquals("japan", result.get(Lang.get("ja")));
    }

    public void testDeserializeFilterConfig__MultipleAliases() throws ConfigurationError {
        Map<Lang, String> result = LanguageAliasSerializer.deserializeFilterConfig("en:en,ja:japan");
        assertEquals(2, result.size());
        assertEquals("en", result.get(Lang.get("en")));
        assertEquals("japan", result.get(Lang.get("ja")));
    }

    public void testDeserializeFilterConfig__InvalidLanguageCode__ThrowError() throws ConfigurationError {
        boolean errorThrown = false;
        try {
            LanguageAliasSerializer.deserializeFilterConfig("english:us");
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testDeserializeFilterConfig__MalformedInput__ThrowError() throws ConfigurationError {
        boolean errorThrown = false;
        try {
            LanguageAliasSerializer.deserializeFilterConfig("en,ja");
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testSerializeToJson() {
        assertEquals("{}", LanguageAliasSerializer.serializeToJson(null));

        Map<Lang, String> langCodeAliases = new LinkedHashMap<Lang, String>();
        assertEquals("{}", LanguageAliasSerializer.serializeToJson(langCodeAliases));

        langCodeAliases.put(Lang.get("en"), "en");
        assertEquals("{\"en\":\"en\"}", LanguageAliasSerializer.serializeToJson(langCodeAliases));

        langCodeAliases.put(Lang.get("ja"), "japan");
        assertEquals("{\"en\":\"en\",\"ja\":\"japan\"}", LanguageAliasSerializer.serializeToJson(langCodeAliases));
    }
}
