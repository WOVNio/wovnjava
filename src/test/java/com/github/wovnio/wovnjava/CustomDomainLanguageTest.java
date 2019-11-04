package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class CustomDomainLanguageTest extends TestCase {
    CustomDomainLanguage customDomainRootPath;
    CustomDomainLanguage customDomainWithPathNoTrailingSlash;
    CustomDomainLanguage customDomainWithPathTrailingSlash;
    CustomDomainLanguage customDomainWithPathEncodedSpaces;

    protected void setUp() throws Exception {
        this.customDomainRootPath = new CustomDomainLanguage("foo.com", "/", Lang.get("en"));
        this.customDomainWithPathNoTrailingSlash = new CustomDomainLanguage("foo.com", "/path", Lang.get("en"));
        this.customDomainWithPathTrailingSlash = new CustomDomainLanguage("foo.com", "/path/", Lang.get("en"));
        this.customDomainWithPathEncodedSpaces = new CustomDomainLanguage("foo.com", "/dir%20path", Lang.get("en"));
    }

    public void testIsMatch__nullURL__False() throws MalformedURLException {
        assertEquals(false, this.customDomainRootPath.isMatch(null));
    }

    public void testIsMatch__DifferentDomain__False() throws MalformedURLException {
        assertEquals(false, this.customDomainRootPath.isMatch(new URL("http://otherdomain.com/other/test.html")));
    }

    public void testIsMatch__DifferentPortNumber__IsIgnored() throws MalformedURLException {
        assertEquals(true, this.customDomainRootPath.isMatch(new URL("http://foo.com:3000/other/test.html")));
        assertEquals(true, this.customDomainRootPath.isMatch(new URL("http://foo.com:80/other/test.html")));
        assertEquals(true, this.customDomainRootPath.isMatch(new URL("http://foo.com/other/test.html")));
    }

    public void testIsMatch__DifferentSchema__IsIgnored() throws MalformedURLException {
        assertEquals(true, this.customDomainRootPath.isMatch(new URL("https://foo.com/other/test.html")));
    }

    public void testIsMatch__Subdomain__False() throws MalformedURLException {
        assertEquals(false, this.customDomainRootPath.isMatch(new URL("http://en.foo.com/other/test.html")));
    }

    public void testIsMatch__SameDomain__True() throws MalformedURLException {
        assertEquals(true, this.customDomainRootPath.isMatch(new URL("http://foo.com/other/test.html")));
    }

    public void testIsMatch__SameDomainDifferentCasing__True() throws MalformedURLException {
        assertEquals(true, this.customDomainRootPath.isMatch(new URL("http://FOO.com/other/test.html")));
    }

    public void testIsMatch__PathStartsWithCustomPath__True() throws MalformedURLException {
        assertEquals(true, this.customDomainRootPath.isMatch(new URL("http://foo.com")));
        assertEquals(true, this.customDomainRootPath.isMatch(new URL("http://foo.com/")));
        assertEquals(true, this.customDomainRootPath.isMatch(new URL("http://foo.com/other/test.html?foo=bar")));

        assertEquals(true, this.customDomainWithPathNoTrailingSlash.isMatch(new URL("http://foo.com/path")));
        assertEquals(true, this.customDomainWithPathNoTrailingSlash.isMatch(new URL("http://foo.com/path/")));
        assertEquals(true, this.customDomainWithPathNoTrailingSlash.isMatch(new URL("http://foo.com/path/other/test.html?foo=bar")));

        assertEquals(true, this.customDomainWithPathTrailingSlash.isMatch(new URL("http://foo.com/path")));
        assertEquals(true, this.customDomainWithPathTrailingSlash.isMatch(new URL("http://foo.com/path/")));
        assertEquals(true, this.customDomainWithPathTrailingSlash.isMatch(new URL("http://foo.com/path/other/test.html?foo=bar")));

        assertEquals(true, this.customDomainWithPathEncodedSpaces.isMatch(new URL("http://foo.com/dir%20path")));
        assertEquals(true, this.customDomainWithPathEncodedSpaces.isMatch(new URL("http://foo.com/dir%20path?foo=bar")));
    }

    public void testIsMatch__PathMatchesSubstring__False() throws MalformedURLException {
        assertEquals(false, this.customDomainWithPathNoTrailingSlash.isMatch(new URL("http://foo.com/pathsuffix/other/test.html")));
        assertEquals(false, this.customDomainWithPathTrailingSlash.isMatch(new URL("http://foo.com/pathsuffix/other/test.html")));
        assertEquals(false, this.customDomainWithPathEncodedSpaces.isMatch(new URL("http://foo.com/dir%20pathsuffix/other/test.html")));
    }

    public void testIsMatch__PathMatchesCustomPathAsSuffix__False() throws MalformedURLException {
        assertEquals(false, this.customDomainWithPathNoTrailingSlash.isMatch(new URL("http://foo.com/images/path/foo.png")));
        assertEquals(false, this.customDomainWithPathTrailingSlash.isMatch(new URL("http://foo.com/images/path/foo.png")));
    }

    public void testIsMatch__PathPartiallyMatchesCustomPath__False() throws MalformedURLException {
        assertEquals(false, this.customDomainWithPathNoTrailingSlash.isMatch(new URL("http://foo.com")));
        assertEquals(false, this.customDomainWithPathNoTrailingSlash.isMatch(new URL("http://foo.com/")));
        assertEquals(false, this.customDomainWithPathTrailingSlash.isMatch(new URL("http://foo.com")));
        assertEquals(false, this.customDomainWithPathTrailingSlash.isMatch(new URL("http://foo.com/")));
    }
}
