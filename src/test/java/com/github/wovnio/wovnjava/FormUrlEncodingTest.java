package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormUrlEncodingTest extends TestCase {

    public void testEncode__EmptyMap__ReturnsEmpty() throws UnsupportedOperationException {
        Map<String, String> map = new LinkedHashMap<String, String>();

        String result = FormUrlEncoding.encode(map);

        assertEquals("", result);
    }

    public void testEncode__NonEmptyMap__ReturnsUrlEncodedKeysAndValues() throws UnsupportedOperationException {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("key one", "value one");
        map.put("key two", "value two");

        String result = FormUrlEncoding.encode(map);

        assertEquals("key+one=value+one&key+two=value+two", result);
    }

    public void testEncodeValue__ReturnsUrlEncodedValue() throws UnsupportedOperationException {
        assertEquals("", FormUrlEncoding.encodeValue(""));
        assertEquals("one", FormUrlEncoding.encodeValue("one"));
        assertEquals("has+spaces", FormUrlEncoding.encodeValue("has spaces"));
    }
}
