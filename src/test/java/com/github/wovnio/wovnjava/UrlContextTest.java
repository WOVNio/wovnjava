package com.github.wovnio.wovnjava;

import java.net.URL;

import junit.framework.TestCase;

public class UrlContextTest extends TestCase {
    private UrlContext contextRootPath;
    private UrlContext contextFilePath;
    private UrlContext contextPathAndQuery;

    protected void setUp() throws Exception {
        this.contextRootPath = new UrlContext(new URL("http://site.com"));
        this.contextFilePath = new UrlContext(new URL("http://site.com/dir/index.html"));
        this.contextPathAndQuery = new UrlContext(new URL("http://site.com/dir/?user=tom"));
    }

    public void testCreateAbsoluteUrl__RelativePath() {
        String location = "img/cat.png";
        assertEquals("http://site.com/img/cat.png", this.contextRootPath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/dir/img/cat.png", this.contextFilePath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/dir/img/cat.png", this.contextPathAndQuery.createAbsoluteUrl(location).toString());
    }

    public void testCreateAbsoluteUrl__InvalidRelativePath() {
        String location = "../../img/cat.png";
        assertEquals("http://site.com/img/cat.png", this.contextRootPath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextFilePath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextPathAndQuery.createAbsoluteUrl(location).toString());
    }

    public void testCreateAbsoluteUrl__AbsolutePath() {
        String location = "/img/cat.png";
        assertEquals("http://site.com/img/cat.png", this.contextRootPath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextFilePath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextPathAndQuery.createAbsoluteUrl(location).toString());
    }

    public void testCreateAbsoluteUrl__AbsoluteUrl() {
        String location = "https://otherdomain.com/img/cat.png";
        assertEquals("https://otherdomain.com/img/cat.png", this.contextRootPath.createAbsoluteUrl(location).toString());
        assertEquals("https://otherdomain.com/img/cat.png", this.contextFilePath.createAbsoluteUrl(location).toString());
        assertEquals("https://otherdomain.com/img/cat.png", this.contextPathAndQuery.createAbsoluteUrl(location).toString());
    }

    public void testCreateAbsoluteUrl__AbsoluteUrlWithoutSchema() {
        String location = "//otherdomain.com/img/cat.png";
        assertEquals("http://otherdomain.com/img/cat.png", this.contextRootPath.createAbsoluteUrl(location).toString());
        assertEquals("http://otherdomain.com/img/cat.png", this.contextFilePath.createAbsoluteUrl(location).toString());
        assertEquals("http://otherdomain.com/img/cat.png", this.contextPathAndQuery.createAbsoluteUrl(location).toString());
    }

    public void testCreateAbsoluteUrl__PathAndQuery() {
        String location = "/img/cat.png?country=japan";
        assertEquals("http://site.com/img/cat.png?country=japan", this.contextRootPath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/img/cat.png?country=japan", this.contextFilePath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/img/cat.png?country=japan", this.contextPathAndQuery.createAbsoluteUrl(location).toString());
    }

    public void testCreateAbsoluteUrl__OnlyQuery() {
        String location = "?country=japan";
        assertEquals("http://site.com/?country=japan", this.contextRootPath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/dir/?country=japan", this.contextFilePath.createAbsoluteUrl(location).toString());
        assertEquals("http://site.com/dir/?country=japan", this.contextPathAndQuery.createAbsoluteUrl(location).toString());
    }
}
