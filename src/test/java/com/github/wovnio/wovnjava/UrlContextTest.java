package com.github.wovnio.wovnjava;

import java.net.URL;
import java.net.MalformedURLException;

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

    public void testResolve__EmptyString() {
        String location = "";
        assertEquals("http://site.com", this.contextRootPath.resolve(location).toString());
        assertEquals("http://site.com/dir/index.html", this.contextFilePath.resolve(location).toString());
        assertEquals("http://site.com/dir/?user=tom", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__RelativePath() {
        String location = "img/cat.png";
        assertEquals("http://site.com/img/cat.png", this.contextRootPath.resolve(location).toString());
        assertEquals("http://site.com/dir/img/cat.png", this.contextFilePath.resolve(location).toString());
        assertEquals("http://site.com/dir/img/cat.png", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__AbsolutePath() {
        String location = "/img/cat.png";
        assertEquals("http://site.com/img/cat.png", this.contextRootPath.resolve(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextFilePath.resolve(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__AbsoluteUrl() {
        String location = "https://otherdomain.com/img/cat.png";
        assertEquals("https://otherdomain.com/img/cat.png", this.contextRootPath.resolve(location).toString());
        assertEquals("https://otherdomain.com/img/cat.png", this.contextFilePath.resolve(location).toString());
        assertEquals("https://otherdomain.com/img/cat.png", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__AbsoluteUrlWithoutSchema() {
        String location = "//otherdomain.com/img/cat.png";
        assertEquals("http://otherdomain.com/img/cat.png", this.contextRootPath.resolve(location).toString());
        assertEquals("http://otherdomain.com/img/cat.png", this.contextFilePath.resolve(location).toString());
        assertEquals("http://otherdomain.com/img/cat.png", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__PathAndQuery() {
        String location = "/img/cat.png?country=japan";
        assertEquals("http://site.com/img/cat.png?country=japan", this.contextRootPath.resolve(location).toString());
        assertEquals("http://site.com/img/cat.png?country=japan", this.contextFilePath.resolve(location).toString());
        assertEquals("http://site.com/img/cat.png?country=japan", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__OnlyQuery() {
        String location = "?country=japan";
        assertEquals("http://site.com/?country=japan", this.contextRootPath.resolve(location).toString());
        assertEquals("http://site.com/dir/?country=japan", this.contextFilePath.resolve(location).toString());
        assertEquals("http://site.com/dir/?country=japan", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__OnlyHost__InterpretAsPath() {
        // For the purpose of illustrating behavior
        String location = "example.com";
        assertEquals("http://site.com/example.com", this.contextRootPath.resolve(location).toString());
        assertEquals("http://site.com/dir/example.com", this.contextFilePath.resolve(location).toString());
        assertEquals("http://site.com/dir/example.com", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__DotDotRelativePath() {
        String location = "../img/cat.png";
        assertEquals("http://site.com/img/cat.png", this.contextRootPath.resolve(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextFilePath.resolve(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__InvalidDotDotAbsolutePath() {
        String location = "/../../img/cat.png";
        assertEquals("http://site.com/img/cat.png", this.contextRootPath.resolve(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextFilePath.resolve(location).toString());
        assertEquals("http://site.com/img/cat.png", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__NotNormalizedDotDotAbsolutePath() {
        String location = "/global/users/../img/cat.png";
        assertEquals("http://site.com/global/img/cat.png", this.contextRootPath.resolve(location).toString());
        assertEquals("http://site.com/global/img/cat.png", this.contextFilePath.resolve(location).toString());
        assertEquals("http://site.com/global/img/cat.png", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testResolve__NotNormalizedDotDotAbsoluteUrl() {
        String location = "http://otherdomain.com/global/users/../img/../cat.png";
        assertEquals("http://otherdomain.com/global/cat.png", this.contextRootPath.resolve(location).toString());
        assertEquals("http://otherdomain.com/global/cat.png", this.contextFilePath.resolve(location).toString());
        assertEquals("http://otherdomain.com/global/cat.png", this.contextPathAndQuery.resolve(location).toString());
    }

    public void testIsSameHost() throws MalformedURLException {
        assertEquals(true, this.contextRootPath.isSameHost(new URL("http://site.com/?country=japan")));
        assertEquals(false, this.contextRootPath.isSameHost(new URL("http://en.site.com/?country=japan")));
        assertEquals(false, this.contextRootPath.isSameHost(new URL("http://other.com/?country=japan")));
    }
}
