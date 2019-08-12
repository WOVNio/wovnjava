package com.github.wovnio.wovnjava;

import junit.framework.TestCase;
import java.util.HashMap;
import java.util.ArrayList;
import javax.servlet.FilterConfig;

public class FilterConfigReaderTest extends TestCase {
    public void testGetStringParameter() {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("(empty)", "");
            put("projectToken", "123456");
            put("urlPattern", " path  ");
        }});
        FilterConfigReader reader = new FilterConfigReader(config);

        assertEquals("", reader.getStringParameter(null));
        assertEquals("123456", reader.getStringParameter("projectToken"));
        assertEquals("path", reader.getStringParameter("urlPattern"));
    }

    public void testGetIntParameter() throws ConfigurationError {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("(empty)", "");
            put("0", "0");
            put("876", "876");
            put("float", "1.6");
            put("string", "hoge");
        }});
        FilterConfigReader reader = new FilterConfigReader(config);

        assertEquals(0, reader.getIntParameter(null));
        assertEquals(0, reader.getIntParameter("0"));
        assertEquals(876, reader.getIntParameter("876"));

        boolean errorOnFloat = false;
        try {
            reader.getIntParameter("float");
        } catch (ConfigurationError e) {
            errorOnFloat = true;
        }
        assertEquals(true, errorOnFloat);

        boolean errorOnString = false;
        try {
            reader.getIntParameter("string");
        } catch (ConfigurationError e) {
            errorOnString = true;
        }
        assertEquals(true, errorOnString);
    }

    public void testGetBoolParameter() {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("on", "on");
            put("true", "true");
            put("1", "1");
            put("0", "0");
            put("(empty)", "");
        }});
        FilterConfigReader reader = new FilterConfigReader(config);

        assertEquals(true, reader.getBoolParameter("on"));
        assertEquals(true, reader.getBoolParameter("true"));
        assertEquals(true, reader.getBoolParameter("1"));

        assertEquals(false, reader.getBoolParameter(null));
        assertEquals(false, reader.getBoolParameter("(empty)"));
        assertEquals(false, reader.getBoolParameter("0"));
        assertEquals(false, reader.getBoolParameter("hoge"));
    }

    public void testGetArrayParameter() {
        FilterConfig config = TestUtil.makeConfig(new HashMap<String, String>() {{
            put("(empty)", "");
            put("first", "one");
            put("second", "one,two");
            put("third", "  one,two ,  three");
        }});
        FilterConfigReader reader = new FilterConfigReader(config);

        ArrayList<String> al = new ArrayList<String>();
        assertEquals(al, reader.getArrayParameter(null));
        assertEquals(al, reader.getArrayParameter("(empty)"));

        al.add("one");
        assertEquals(al, reader.getArrayParameter("first"));

        al.add("two");
        assertEquals(al, reader.getArrayParameter("second"));

        al.add("three");
        assertEquals(al, reader.getArrayParameter("third"));
    }
}
