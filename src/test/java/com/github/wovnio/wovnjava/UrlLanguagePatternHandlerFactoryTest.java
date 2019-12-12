package com.github.wovnio.wovnjava;

import java.util.HashMap;

import junit.framework.TestCase;
import org.easymock.EasyMock;

public class UrlLanguagePatternHandlerFactoryTest extends TestCase {
    public void testCreate__UrlPatternPath() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "path");
        }});
        UrlLanguagePatternHandler result = UrlLanguagePatternHandlerFactory.create(settings);
        assertEquals(true, result instanceof PathUrlLanguagePatternHandler);
    }

    public void testCreate__UrlPatternQuery() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "query");
        }});
        UrlLanguagePatternHandler result = UrlLanguagePatternHandlerFactory.create(settings);
        assertEquals(true, result instanceof QueryUrlLanguagePatternHandler);
    }

    public void testCreate__UrlPatternSubdomain() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "subdomain");
        }});
        UrlLanguagePatternHandler result = UrlLanguagePatternHandlerFactory.create(settings);
        assertEquals(true, result instanceof SubdomainUrlLanguagePatternHandler);
    }

    public void testCreate__UrlPatternCustomDomain() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "customDomain");
            put("sitePrefixPath", "");
            put("customDomainLangs", "site.com:en,site.co.jp:ja");
            put("supportedLangs", "en,ja");
        }});
        UrlLanguagePatternHandler result = UrlLanguagePatternHandlerFactory.create(settings);
        assertEquals(true, result instanceof CustomDomainUrlLanguagePatternHandler);
    }

    public void testCreate__UrlPatternCustomDomain__CustomDomainLangsNotConfigured__ThrowError() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "customDomain");
            put("supportedLangs", "en,ja,ko");
        }});
        boolean errorThrown = false;
        try {
            UrlLanguagePatternHandlerFactory.create(settings);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }

    public void testCreate__InvalidUrlPattern__ThrowError() throws ConfigurationError {
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("urlPattern", "invalid");
        }});
        boolean errorThrown = false;
        try {
            UrlLanguagePatternHandlerFactory.create(settings);
        } catch (ConfigurationError e) {
            errorThrown = true;
        }
        assertEquals(true, errorThrown);
    }
}
