package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

public class UrlPathTest extends TestCase {
    public void testGetPathAndQuery() {
        assertEquals("", UrlPath.getPath(""));
        assertEquals("/", UrlPath.getPath("/"));
        assertEquals("", UrlPath.getPath("?query"));
        assertEquals("/path/a.html", UrlPath.getPath("/path/a.html"));
        assertEquals("/path/a.html", UrlPath.getPath("/path/a.html?query"));

        assertEquals("", UrlPath.getPath("site.com"));
        assertEquals("/", UrlPath.getPath("site.com/"));
        assertEquals("", UrlPath.getPath("site.com?query"));
        assertEquals("/path/a.html", UrlPath.getPath("site.com/path/a.html"));
        assertEquals("/path/a.html", UrlPath.getPath("site.com/path/a.html?query"));

        assertEquals("", UrlPath.getPath("http://site.com"));
        assertEquals("/", UrlPath.getPath("http://site.com/"));
        assertEquals("", UrlPath.getPath("http://site.com?query"));
        assertEquals("/path/a.html", UrlPath.getPath("http://site.com/path/a.html"));
        assertEquals("/path/a.html", UrlPath.getPath("http://site.com/path/a.html?query"));
    }

    public void testRemoveFile() {
        assertEquals("/", UrlPath.removeFile(""));
        assertEquals("/", UrlPath.removeFile("/"));
        assertEquals("/", UrlPath.removeFile("holla_holla.wav"));
        assertEquals("/", UrlPath.removeFile("/holla_holla.wav"));
        assertEquals("/ja/", UrlPath.removeFile("/ja/rule"));
        assertEquals("/ja/rule/", UrlPath.removeFile("/ja/rule/"));
        assertEquals("/ja/rule/", UrlPath.removeFile("/ja/rule/holla_holla.wav"));
        assertEquals("ja/rule/", UrlPath.removeFile("ja/rule/holla_holla.wav"));
    }

    public void testJoin() {
        assertEquals("/", UrlPath.join("", ""));
        assertEquals("/", UrlPath.join("/", "/"));
        assertEquals("/jon", UrlPath.join("", "/jon"));
        assertEquals("/jon", UrlPath.join("/", "jon"));
        assertEquals("/jon", UrlPath.join("/", "/jon"));
        assertEquals("lil/jon", UrlPath.join("lil", "jon"));
        assertEquals("/lil/jon/", UrlPath.join("/lil/", "/jon/"));
        assertEquals("/lil/jon/crunk_juice.flac", UrlPath.join("/lil/jon", "crunk_juice.flac"));
    }

    public void testNormalize() {
        assertEquals("path", UrlPath.normalize("path"));
        assertEquals("/", UrlPath.normalize(""));
        assertEquals("/", UrlPath.normalize("/"));
        assertEquals("/", UrlPath.normalize("/../"));
        assertEquals("/", UrlPath.normalize("/../././../"));
        assertEquals("/", UrlPath.normalize("/bon/.././jovi/./../"));
        assertEquals("bon/bad_medicine.m4p", UrlPath.normalize("bon/./jovi/./../bad_medicine.m4p"));
        assertEquals("/bon/jovi/bad_medicine.m4p", UrlPath.normalize("/bon/jovi/bad_medicine.m4p"));
    }
}
