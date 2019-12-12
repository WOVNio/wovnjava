package com.github.wovnio.wovnjava;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class StringUtilTest extends TestCase {
    public void testJoin__ArrayList__Empty() {
        ArrayList<String> elements = new ArrayList<String>();
        assertEquals("", StringUtil.join(", ", elements));
    }

    public void testJoin__ArrayList__SingleElement() {
        ArrayList<String> elements = new ArrayList<String>();
        elements.add("one");
        assertEquals("one", StringUtil.join(", ", elements));
    }

    public void testJoin__ArrayList__MultipleElements() {
        ArrayList<String> elements = new ArrayList<String>();
        elements.add("one");
        elements.add("two");
        elements.add("three");
        assertEquals("one, two, three", StringUtil.join(", ", elements));
    }
}
