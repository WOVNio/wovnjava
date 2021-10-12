package com.github.wovnio.wovnjava;

import junit.framework.TestCase;
import java.util.HashMap;
import java.util.ArrayList;
import javax.servlet.FilterConfig;

public class FilterConfigReaderTest extends TestCase {
    public void testGetStringParameter() {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("empty-string", "");
            put("projectToken", "123456");
            put("urlPattern", " path  ");
        }});
        FilterConfigReader reader = new FilterConfigReader(config);

        assertEquals(null, reader.getStringParameter(null));
        assertEquals(null, reader.getStringParameter("not-set"));
        assertEquals("", reader.getStringParameter("empty-string"));
        assertEquals("123456", reader.getStringParameter("projectToken"));
        assertEquals("path", reader.getStringParameter("urlPattern"));
    }

    public void testGetIntParameter() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("empty-string", "");
            put("0", "0");
            put("876", "876");
            put("-1", "-1");
            put("float", "1.6");
            put("text", "hoge");
        }});
        FilterConfigReader reader = new FilterConfigReader(config);

        assertEquals(0, reader.getIntParameter(null));
        assertEquals(0, reader.getIntParameter("not-set"));
        assertEquals(0, reader.getIntParameter("0"));
        assertEquals(876, reader.getIntParameter("876"));
        assertEquals(-1, reader.getIntParameter("-1"));

        boolean errorOnEmptyString = false;
        try {
            reader.getIntParameter("empty-string");
        } catch (ConfigurationError e) {
            errorOnEmptyString = true;
        }
        assertEquals(true, errorOnEmptyString);

        boolean errorOnFloat = false;
        try {
            reader.getIntParameter("float");
        } catch (ConfigurationError e) {
            errorOnFloat = true;
        }
        assertEquals(true, errorOnFloat);

        boolean errorOnText = false;
        try {
            reader.getIntParameter("text");
        } catch (ConfigurationError e) {
            errorOnText = true;
        }
        assertEquals(true, errorOnText);
    }

    public void testGetBoolParameterOrDefault() {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("on", "on");
            put("true", "true");
            put("1", "1");
            put("0", "0");
            put("empty-string", "");
        }});
        FilterConfigReader reader = new FilterConfigReader(config);

        assertEquals(true, reader.getBoolParameterOrDefault("on", false));
        assertEquals(true, reader.getBoolParameterOrDefault("true", false));
        assertEquals(true, reader.getBoolParameterOrDefault("1", false));

        assertEquals(false, reader.getBoolParameterOrDefault(null, false));
        assertEquals(true, reader.getBoolParameterOrDefault(null, true));
        assertEquals(false, reader.getBoolParameterOrDefault("not-in-config", false));
        assertEquals(true, reader.getBoolParameterOrDefault("not-in-config", true));
        assertEquals(false, reader.getBoolParameterOrDefault("empty-string", false));
        assertEquals(false, reader.getBoolParameterOrDefault("empty-string", true));
        assertEquals(false, reader.getBoolParameterOrDefault("0", false));
        assertEquals(false, reader.getBoolParameterOrDefault("0", true));
    }

    public void testGetArrayParameter() {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("empty-string", "");
            put("first", "one");
            put("second", "one,two");
            put("third", "  one,two ,  three");
        }});
        FilterConfigReader reader = new FilterConfigReader(config);

        ArrayList<String> al = new ArrayList<String>();
        assertEquals(al, reader.getArrayParameter(null));
        assertEquals(al, reader.getArrayParameter("not-set"));
        assertEquals(al, reader.getArrayParameter("empty-string"));

        al.add("one");
        assertEquals(al, reader.getArrayParameter("first"));

        al.add("two");
        assertEquals(al, reader.getArrayParameter("second"));

        al.add("three");
        assertEquals(al, reader.getArrayParameter("third"));
    }
}
