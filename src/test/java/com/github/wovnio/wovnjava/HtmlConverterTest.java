package com.github.wovnio.wovnjava;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

public class HtmlConverterTest extends TestCase {
    private String location = "https://site.com/global/tokyo/";

    private HtmlConverter createHtmlConverter(Settings settings, String location, String original) throws ConfigurationError {
        HttpServletRequest mockRequest = MockHttpServletRequest.create(location);
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        Headers headers = new Headers(mockRequest, settings, urlLanguagePatternHandler);
        return new HtmlConverter(settings, headers, original);
    }

    public void testDisablePrettyPrint() throws ConfigurationError {
        String original = "<html><head></head><body>\n " + "hello" + "\t\n</body></html>";
        String expected = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body>\n " + "hello" + "\t\n</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(expected, html);
    }

    public void testRemoveWovnSnippet() throws ConfigurationError {
        String original = "<html><head><script src=\"https://j.wovn.io/1\"></script><script src=\"https://j.dev-wovn.io:3000\"></script><script src=\"//j.wovn.io/1\" data-wovnio=\"key=NCmbvk&amp;backend=true&amp;currentLang=en&amp;defaultLang=en&amp;urlPattern=path&amp;version=0.0.0\" data-wovnio-type=\"backend_without_api\" async></script></head><body></body></html>";
        String removedHtml = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(removedHtml, stripExtraSpaces(html));
        assertEquals(removedHtml, stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveScripts() throws ConfigurationError {
        String original = "<html lang=\"en\"><head><script>alert(1)</script><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body>a <script>console.log(1)</script>b</body></html>";
        String removedHtml = "<html lang=\"en\"><head><script><!--wovn-marker-0--></script><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body>a <script><!--wovn-marker-1--></script>b</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(removedHtml, stripExtraSpaces(html));
        assertEquals(stripExtraSpaces(original), stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveHrefLangIfConflicts() throws ConfigurationError {
        String original = "<html lang=\"en\"><head><link rel=\"altername\" hreflang=\"en\" href=\"http://localhost:8080/\"><link rel=\"altername\" hreflang=\"ja\" href=\"http://localhost:8080/ja/\"><link rel=\"altername\" hreflang=\"ar\" href=\"http://localhost:8080/ar/\"></head><body></body></html>";
        String removedHtml = "<html lang=\"en\"><head><link rel=\"altername\" hreflang=\"ar\" href=\"http://localhost:8080/ar/\"><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(removedHtml, stripExtraSpaces(html));
        assertEquals(removedHtml, stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveWovnIgnore() throws ConfigurationError {
        String original = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><div>Hello <span wovn-ignore>Duke</span><span data-wovn-ignore>Silver</span>.</div></body></html>";
        String removedHtml = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><div>Hello <span wovn-ignore><!--wovn-marker-0--></span><span data-wovn-ignore><!--wovn-marker-1--></span>.</div></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(removedHtml, stripExtraSpaces(html));
        assertEquals(original, stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveClassIgnore() throws ConfigurationError {
        String original = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body>" +
          "<p class=\"no-ignore\">The pizza needs <b class=\"ingredient\">pineapple</b>, <span class=\"name\">Chad</span>!</p>" +
          "<p class=\"ignore-me\">It's a fruit, <span class=\"name\">Louie</span>!</p>" +
          "</body></html>";
        String removedHtml = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body>" +
        "<p class=\"no-ignore\">The pizza needs <b class=\"ingredient\"><!--wovn-marker-0--></b>, <span class=\"name\"><!--wovn-marker-1--></span>!</p>" +
        "<p class=\"ignore-me\"><!--wovn-marker-2--></p>" +
        "</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("supportedLangs", "en,fr,ja");
            put("ignoreClasses", "ignore-me,name,ingredient");
        }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();

        assertEquals("ignore-me & name & ingredient", String.join(" & ", settings.ignoreClasses));
        assertEquals(removedHtml, stripExtraSpaces(html));
        assertEquals(original, stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveForm() throws ConfigurationError {
        String original = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><form><input type=\"hidden\" name=\"csrf\" value=\"random\"><INPUT type=\"HIDDEN\" name=\"CSRF_TOKEN\" VALUE=\"RANDOM\"></form></body></html>";
        String removedHtml = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><form><input type=\"hidden\" name=\"csrf\" value=\"wovn-marker-0\"><input type=\"HIDDEN\" name=\"CSRF_TOKEN\" value=\"wovn-marker-1\"></form></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();

        assertEquals(removedHtml, stripExtraSpaces(html));
        // jsoup make lower case tag name
        assertEquals(original.replace("INPUT", "input").replace("VALUE", "value"), stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveForm__Sanitize__DoubleQuotes() throws ConfigurationError {
        String original = "<html lang=\"en\"><head></head><body><form><input type=\"hidden\" name=\"csrf\" value=\"&test=true&quot;&gt;&lt;script &gt;alert(String.fromCharCode(88,83,83))&lt;/script&gt;\"></form></body></html>";
        String sanitized = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><form><input type=\"hidden\" name=\"csrf\" value=\"&amp;test=true&quot;&gt;&lt;script &gt;alert(String.fromCharCode(88,83,83))&lt;/script&gt;\"></form></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();

        assertEquals(sanitized, stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveForm__Sanitize__SingleQuotes() throws ConfigurationError {
        String original = "<html lang=\"en\"><head></head><body><form><input type=\"hidden\" name=\"csrf\" value=\'&test=true&#39;&gt;&lt;script &gt;alert(String.fromCharCode(88,83,83))&lt;/script&gt;\'></form></body></html>";
        String sanitized = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><form><input type=\"hidden\" name=\"csrf\" value=\"&amp;test=true&#39;&gt;&lt;script &gt;alert(String.fromCharCode(88,83,83))&lt;/script&gt;\"></form></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();

        assertEquals(sanitized, stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveForm__MultipleHiddenFields() throws ConfigurationError {
        String original = "<HTML lang=\"en\"><HEAD></HEAD><BODY><FORM><INPUT type=\"hidden\" name=\"field_1\" value=\"text\"><INPUT type=\"hidden\" name=\"field_2\" value=\"\"><INPUT type=\"hidden\" name=\"field_3\" value=\"1\"><INPUT type=\"hidden\" name=\"field_4\" value=\"\"><INPUT type=\"hidden\" name=\"field_5\" value=\"\"><INPUT type=\"hidden\" name=\"field_6\" value=\"1234\"><INPUT type=\"hidden\" name=\"field_7\" value=\"000\"><INPUT type=\"hidden\" name=\"field_8\" value=\"1234-000\"><INPUT type=\"hidden\" name=\"field_9\" value=\"\"><INPUT type=\"hidden\" name=\"field_10\" value=\"\"><INPUT type=\"hidden\" name=\"field_11\" value=\"\"><INPUT type=\"hidden\" name=\"field_12\" value=\"0\"><INPUT type=\"hidden\" name=\"field_13\" value=\"\"><INPUT type=\"hidden\" name=\"field_14\" value=\"\"><INPUT type=\"hidden\" name=\"field_15\" value=\"\"><INPUT type=\"hidden\" name=\"field_16\" value=\"\"><INPUT type=\"hidden\" name=\"field_17\" value=\"0\"><INPUT type=\"hidden\" name=\"field_18\" value=\"\"><INPUT type=\"hidden\" name=\"field_19\" value=\"\"><INPUT type=\"hidden\" name=\"field_20\" value=\"\"><INPUT type=\"hidden\" name=\"field_21\" value=\"https://example.com/a\"><INPUT type=\"hidden\" name=\"field_22\" value=\"https://example.com/b\"><INPUT type=\"hidden\" name=\"field_23\" value=\"https://example.com/c\"><INPUT type=\"hidden\" name=\"field_24\" value=\"https://example.com/d\"><INPUT type=\"hidden\" name=\"field_25\" value=\"\"><INPUT type=\"hidden\" name=\"field_26\" value=\"\"><INPUT type=\"hidden\" name=\"field_27\" value=\"\"><INPUT type=\"hidden\" name=\"field_28\" value=\"foo\"><INPUT type=\"hidden\" name=\"field_29\" value=\"\"><INPUT type=\"hidden\" name=\"field_30\" value=\"\"><INPUT type=\"hidden\" name=\"field_31\" value=\"100\"></FORM></BODY></HTML>";
        String removedHtml = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><form><input type=\"hidden\" name=\"field_1\" value=\"text\"><input type=\"hidden\" name=\"field_2\" value=\"\"><input type=\"hidden\" name=\"field_3\" value=\"1\"><input type=\"hidden\" name=\"field_4\" value=\"\"><input type=\"hidden\" name=\"field_5\" value=\"\"><input type=\"hidden\" name=\"field_6\" value=\"1234\"><input type=\"hidden\" name=\"field_7\" value=\"000\"><input type=\"hidden\" name=\"field_8\" value=\"1234-000\"><input type=\"hidden\" name=\"field_9\" value=\"\"><input type=\"hidden\" name=\"field_10\" value=\"\"><input type=\"hidden\" name=\"field_11\" value=\"\"><input type=\"hidden\" name=\"field_12\" value=\"0\"><input type=\"hidden\" name=\"field_13\" value=\"\"><input type=\"hidden\" name=\"field_14\" value=\"\"><input type=\"hidden\" name=\"field_15\" value=\"\"><input type=\"hidden\" name=\"field_16\" value=\"\"><input type=\"hidden\" name=\"field_17\" value=\"0\"><input type=\"hidden\" name=\"field_18\" value=\"\"><input type=\"hidden\" name=\"field_19\" value=\"\"><input type=\"hidden\" name=\"field_20\" value=\"\"><input type=\"hidden\" name=\"field_21\" value=\"https://example.com/a\"><input type=\"hidden\" name=\"field_22\" value=\"https://example.com/b\"><input type=\"hidden\" name=\"field_23\" value=\"https://example.com/c\"><input type=\"hidden\" name=\"field_24\" value=\"https://example.com/d\"><input type=\"hidden\" name=\"field_25\" value=\"\"><input type=\"hidden\" name=\"field_26\" value=\"\"><input type=\"hidden\" name=\"field_27\" value=\"\"><input type=\"hidden\" name=\"field_28\" value=\"foo\"><input type=\"hidden\" name=\"field_29\" value=\"\"><input type=\"hidden\" name=\"field_30\" value=\"\"><input type=\"hidden\" name=\"field_31\" value=\"100\"></form></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();

        assertEquals(removedHtml, stripExtraSpaces(converter.restore(html)));
    }

    public void testStrip__Sanitize() throws ConfigurationError {
        String original = "<html lang=\"en\"><head></head><body><a title=\"&quot;&gt;&lt;script &gt;alert(String.fromCharCode(88,83,83))&lt;/script&gt;\"></a></body></html>";
        String sanitized = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><a title=\"&quot;><script >alert(String.fromCharCode(88,83,83))</script>\"></a></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();

        assertEquals(sanitized, stripExtraSpaces(converter.restore(html)));
    }

    public void testNested() throws ConfigurationError {
        String original = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><form wovn-ignore><script></script><input type=\"hidden\" name=\"csrf\" value=\"random\"><INPUT type=\"HIDDEN\" name=\"CSRF_TOKEN\" value=\"RANDOM\"></form></body></html>";
        String removedHtml = "<html lang=\"en\"><head><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\"></head><body><form wovn-ignore><!--wovn-marker-1--></form></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();

        assertEquals(removedHtml, stripExtraSpaces(html));
        // jsoup make lower case tag name
        assertEquals(original.replace("INPUT", "input"), stripExtraSpaces(converter.restore(html)));
    }

    public void testConvertWithSitePrefixPath() throws ConfigurationError {
        String original = "<html><head></head><body></body></html>";
        String expectedSnippet = "<script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=ja&amp;defaultLang=ja&amp;urlPattern=path&amp;version=" + Settings.VERSION + "&amp;sitePrefixPath=global\" data-wovnio-type=\"fallback\" async></script>";
        String expectedHrefLangs = "<link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/global/tokyo/\">" +
                                   "<link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/en/tokyo/\">" +
                                   "<link rel=\"alternate\" hreflang=\"th\" href=\"https://site.com/global/th/tokyo/\">";
        String expectedContentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        String expectedHtml = "<html lang=\"ja\"><head>" + expectedSnippet + expectedHrefLangs + expectedContentType + "</head><body></body></html>";

        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ja,en,th");
            put("urlPattern", "path");
            put("sitePrefixPath", "global");
        }};
        Settings settings = TestUtil.makeSettings(option);
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);

        assertEquals(expectedHtml, converter.convert("ja"));
    }

    public void testConvert__HasLangCodeAliasSetting() throws ConfigurationError {
        String original = "<html><head></head><body></body></html>";
        String expectedSnippet = "<script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=ja&amp;defaultLang=ja&amp;urlPattern=path&amp;version=" + Settings.VERSION + "&amp;langCodeAliases={&quot;en&quot;:&quot;en&quot;,&quot;ja&quot;:&quot;japan&quot;}\" data-wovnio-type=\"fallback\" async></script>";
        String expectedHrefLangs = "<link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/japan/tokyo\">" +
                                   "<link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/en/tokyo\">";
        String expectedContentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        String expectedHtml = "<html lang=\"ja\"><head>" + expectedSnippet + expectedHrefLangs + expectedContentType + "</head><body></body></html>";

        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ja,en");
            put("urlPattern", "path");
            put("langCodeAliases", "en:en,ja:japan");
        }};
        Settings settings = TestUtil.makeSettings(option);
        HtmlConverter converter = this.createHtmlConverter(settings, "https://site.com/japan/tokyo", original);

        assertEquals(expectedHtml, converter.convert("ja"));
    }

    public void testConvertWithCustomDomain() throws ConfigurationError {
        String original = "<html><head></head><body></body></html>";
        String expectedSnippet = "<script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=ja&amp;defaultLang=ja&amp;urlPattern=custom_domain&amp;version=" + Settings.VERSION + "&amp;customDomainLangs={&quot;site.com/english/&quot;:&quot;en&quot;,&quot;site.co.jp/&quot;:&quot;ja&quot;}\" data-wovnio-type=\"fallback\" async></script>";
        String expectedHrefLangs = "<link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.co.jp/tokyo\">" +
                                   "<link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/english/tokyo\">";
        String expectedContentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        String expectedHtml = "<html lang=\"ja\"><head>" + expectedSnippet + expectedHrefLangs + expectedContentType + "</head><body></body></html>";

        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ja,en");
            put("urlPattern", "customDomain");
            put("customDomainLangs", "site.co.jp:ja,site.com/english:en");
        }};
        Settings settings = TestUtil.makeSettings(option);
        HtmlConverter converter = this.createHtmlConverter(settings, "https://site.co.jp/tokyo", original);

        assertEquals(expectedHtml, converter.convert("ja"));
    }

    public void testMixAllCase() throws ConfigurationError {
        String original = "<html lang=\"en\"><head>" +
            "<script src=\"//j.wovn.io/1\" data-wovnio=\"key=NCmbvk&amp;backend=true&amp;currentLang=en&amp;defaultLang=en&amp;urlPattern=path&amp;version=0.0.0\" data-wovnio-type=\"backend_without_api\" async></script>" +
            "<script>alert(1)</script>" +
            "<link rel=\"altername\" hreflang=\"en\" href=\"http://localhost:8080/\"><link rel=\"altername\" hreflang=\"ja\" href=\"http://localhost:8080/ja/\"><link rel=\"altername\" hreflang=\"ar\" href=\"http://localhost:8080/ar/\">" +
            "</head><body>" +
            "a <script>console.log(1)</script>b" +
            "<div>Hello <span wovn-ignore>Duke</span>.</div>" +
            "<form><input type=\"hidden\" name=\"csrf\" value=\"random\"></form>" +
            "<script>4</script>" +
            "<script>5</script>" +
            "<script>6</script>" +
            "<script>7</script>" +
            "<script>8</script>" +
            "<div class=\"class-ignore-test\">" +
            "<p class=\"no-ignore\">The pizza needs <b class=\"ingredient\">pineapple</b>, <span class=\"name\" wovn-ignore>Chad</span>!</p>" +
            "<p class=\"ignore-me\">It's a fruit, <span class=\"name\" wovn-ignore>Louie</span>!</p>" +
            "</div>" +
            "<script>9</script>" +
            "<script>10</script>" +
            "</body></html>";
        String removedHtml = "<html lang=\"en\"><head>" +
            "<script><!--wovn-marker-0--></script>" +
            "<link rel=\"altername\" hreflang=\"ar\" href=\"http://localhost:8080/ar/\"><link rel=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/tokyo/\"><link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr/global/tokyo/\"><link rel=\"alternate\" hreflang=\"ja\" href=\"https://site.com/ja/global/tokyo/\">" +
            "</head><body>" +
            "a <script><!--wovn-marker-1--></script>b" +
            "<div>Hello <span wovn-ignore><!--wovn-marker-9--></span>.</div>" +
            "<form><input type=\"hidden\" name=\"csrf\" value=\"wovn-marker-16\"></form>" +
            "<script><!--wovn-marker-2--></script>" +
            "<script><!--wovn-marker-3--></script>" +
            "<script><!--wovn-marker-4--></script>" +
            "<script><!--wovn-marker-5--></script>" +
            "<script><!--wovn-marker-6--></script>" +
            "<div class=\"class-ignore-test\">" +
            "<p class=\"no-ignore\">The pizza needs <b class=\"ingredient\"><!--wovn-marker-12--></b>, <span class=\"name\" wovn-ignore><!--wovn-marker-13--></span>!</p>" +
            "<p class=\"ignore-me\"><!--wovn-marker-14--></p>" +
            "</div>" +
            "<script><!--wovn-marker-7--></script>" +
            "<script><!--wovn-marker-8--></script>" +
            "</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("supportedLangs", "en,fr,ja");
            put("ignoreClasses", "ignore-me,name,ingredient");
        }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();

        assertEquals(removedHtml, stripExtraSpaces(html));
    }

    public void testInsertDocLangIfEmpty()  throws ConfigurationError {
        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "en");
            put("supportedLangs", "en,vi");
            put("urlPattern", "path");
            put("sitePrefixPath", "global");
        }};
        Settings settings = TestUtil.makeSettings(option);

        String original = "<html><head></head><body><a>hello</a></body></html>";
        String expected = "<html lang=\"en\"";
        String html = this.createHtmlConverter(settings, location, original).convert("en");
        assertTrue("general case - insert lang attribute", stripExtraSpaces(html).indexOf(expected) != -1);

        original = "<html lang=\"ja\"><head></head><body><a>hello</a></body></html>";
        expected = "<html lang=\"ja\"";
        html = this.createHtmlConverter(settings, location, original).convert("en");
        assertTrue("lang attribute exists - keep existing lang", stripExtraSpaces(html).indexOf(expected) != -1);
    }

    public void testConvert__TranslateCanonicalTagEnabled__CanonicalUrlIsExternal__DoesNotModifyCanonicalUrl() throws ConfigurationError {
        String requestUrl = "http://site.com/en/has-canonical.html";
        String expectedJaUrl = "http://site.com/has-canonical.html";
        String expectedEnUrl = requestUrl;

        String html = "<html><head><link rel=\"canonical\" href=\"http://google.com\"></head></html>";

        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ja,en");
            put("urlPattern", "path");
            put("translateCanonicalTag", "true");
        }};
        Settings settings = TestUtil.makeSettings(option);
        HtmlConverter converter = this.createHtmlConverter(settings, requestUrl, html);
        String result = converter.convert("en");

        String expectedSnippet = String.format("<script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=ja&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script>", Settings.VERSION);
        String expectedHrefLangs = String.format("<link rel=\"alternate\" hreflang=\"ja\" href=\"%s\">", expectedJaUrl) +
                                   String.format("<link rel=\"alternate\" hreflang=\"en\" href=\"%s\">", expectedEnUrl);
        String expectedContentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        String expectedHtml = String.format("<html lang=\"ja\"><head><link rel=\"canonical\" href=\"http://google.com\">%s%s%s</head><body></body></html>", expectedSnippet, expectedHrefLangs, expectedContentType);

        assertEquals(expectedHtml, result);
    }

    public void testConvert__TranslateCanonicalTagEnabled__CanonicalUrlIsInternal__AbsoluteUrl__DefaultLang__DoesNotModifyCanonicalUrl() throws ConfigurationError {
        String requestUrl = "http://site.com/has-canonical.html?foo=bar";
        String expectedJaUrl = requestUrl;
        String expectedEnUrl = "http://site.com/en/has-canonical.html?foo=bar";
        String canonicalUrl = requestUrl;

        String html = String.format("<html><head><link rel=\"canonical\" href=\"%s\"></head></html>", canonicalUrl);

        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ja,en");
            put("urlPattern", "path");
            put("translateCanonicalTag", "true");
        }};
        Settings settings = TestUtil.makeSettings(option);
        HtmlConverter converter = this.createHtmlConverter(settings, requestUrl, html);
        String result = converter.convert("en");

        String expectedSnippet = String.format("<script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=ja&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script>", Settings.VERSION);
        String expectedHrefLangs = String.format("<link rel=\"alternate\" hreflang=\"ja\" href=\"%s\">", expectedJaUrl) +
                                   String.format("<link rel=\"alternate\" hreflang=\"en\" href=\"%s\">", expectedEnUrl);
        String expectedContentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        String expectedHtml = String.format("<html lang=\"ja\"><head><link rel=\"canonical\" href=\"%s\">%s%s%s</head><body></body></html>", canonicalUrl, expectedSnippet, expectedHrefLangs, expectedContentType);

        assertEquals(expectedHtml, result);
    }

    public void testConvert__TranslateCanonicalTagEnabled__CanonicalUrlIsInternal__AbsoluteUrl__TargetLang__TranslatesCanonicalUrl() throws ConfigurationError {
        String requestUrl = "http://site.com/en/has-canonical.html?foo=bar";
        String expectedJaUrl = "http://site.com/has-canonical.html?foo=bar";
        String expectedEnUrl = "http://site.com/en/has-canonical.html?foo=bar";
        String canonicalUrl = "http://site.com/has-canonical.html?foo=bar";
        String expectedCanonicalUrl = expectedEnUrl;

        String html = String.format("<html><head><link rel=\"canonical\" href=\"%s\"></head></html>", canonicalUrl);

        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ja,en");
            put("urlPattern", "path");
            put("translateCanonicalTag", "true");
        }};
        Settings settings = TestUtil.makeSettings(option);
        HtmlConverter converter = this.createHtmlConverter(settings, requestUrl, html);
        String result = converter.convert("en");

        String expectedSnippet = String.format("<script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=ja&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script>", Settings.VERSION);
        String expectedHrefLangs = String.format("<link rel=\"alternate\" hreflang=\"ja\" href=\"%s\">", expectedJaUrl) +
                                   String.format("<link rel=\"alternate\" hreflang=\"en\" href=\"%s\">", expectedEnUrl);
        String expectedContentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        String expectedHtml = String.format("<html lang=\"ja\"><head><link rel=\"canonical\" href=\"%s\">%s%s%s</head><body></body></html>", expectedCanonicalUrl, expectedSnippet, expectedHrefLangs, expectedContentType);

        assertEquals(expectedHtml, result);
    }

    public void testConvert__TranslateCanonicalTagEnabled__CanonicalUrlIsInternal__AbsolutePath__DefaultLang__DoesNotModifyCanonicalUrl() throws ConfigurationError {
        String requestUrl = "http://site.com/has-canonical.html?foo=bar";
        String expectedJaUrl = requestUrl;
        String expectedEnUrl = "http://site.com/en/has-canonical.html?foo=bar";
        String canonicalUrl = "/has-canonical.html?foo=bar";

        String html = String.format("<html><head><link rel=\"canonical\" href=\"%s\"></head></html>", canonicalUrl);

        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ja,en");
            put("urlPattern", "path");
            put("translateCanonicalTag", "true");
        }};
        Settings settings = TestUtil.makeSettings(option);
        HtmlConverter converter = this.createHtmlConverter(settings, requestUrl, html);
        String result = converter.convert("en");

        String expectedSnippet = String.format("<script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=ja&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script>", Settings.VERSION);
        String expectedHrefLangs = String.format("<link rel=\"alternate\" hreflang=\"ja\" href=\"%s\">", expectedJaUrl) +
                                   String.format("<link rel=\"alternate\" hreflang=\"en\" href=\"%s\">", expectedEnUrl);
        String expectedContentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        String expectedHtml = String.format("<html lang=\"ja\"><head><link rel=\"canonical\" href=\"%s\">%s%s%s</head><body></body></html>", canonicalUrl, expectedSnippet, expectedHrefLangs, expectedContentType);

        assertEquals(expectedHtml, result);
    }

    public void testConvert__TranslateCanonicalTagEnabled__CanonicalUrlIsInternal__AbsolutePath__TargetLang__TranslatesCanonicalUrl() throws ConfigurationError {
        String requestUrl = "http://site.com/en/has-canonical.html?foo=bar";
        String expectedJaUrl = "http://site.com/has-canonical.html?foo=bar";
        String expectedEnUrl = "http://site.com/en/has-canonical.html?foo=bar";
        String canonicalUrl = "/has-canonical.html?foo=bar";
        String expectedCanonicalUrl = "http://site.com/en/has-canonical.html?foo=bar";;

        String html = String.format("<html><head><link rel=\"canonical\" href=\"%s\"></head></html>", canonicalUrl);

        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ja,en");
            put("urlPattern", "path");
            put("translateCanonicalTag", "true");
        }};
        Settings settings = TestUtil.makeSettings(option);
        HtmlConverter converter = this.createHtmlConverter(settings, requestUrl, html);
        String result = converter.convert("en");

        String expectedSnippet = String.format("<script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=ja&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script>", Settings.VERSION);
        String expectedHrefLangs = String.format("<link rel=\"alternate\" hreflang=\"ja\" href=\"%s\">", expectedJaUrl) +
                                   String.format("<link rel=\"alternate\" hreflang=\"en\" href=\"%s\">", expectedEnUrl);
        String expectedContentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        String expectedHtml = String.format("<html lang=\"ja\"><head><link rel=\"canonical\" href=\"%s\">%s%s%s</head><body></body></html>", expectedCanonicalUrl, expectedSnippet, expectedHrefLangs, expectedContentType);

        assertEquals(expectedHtml, result);
    }

    public void testConvert__TranslateCanonicalTagDisabled__CanonicalUrlIsInternal__TargetLang__DoesNotModifyCanonicalUrl() throws ConfigurationError {
        String requestUrl = "http://site.com/en/has-canonical.html?foo=bar";
        String expectedJaUrl = "http://site.com/has-canonical.html?foo=bar";
        String expectedEnUrl = "http://site.com/en/has-canonical.html?foo=bar";
        String canonicalUrl = "/has-canonical.html?foo=bar";

        String html = String.format("<html><head><link rel=\"canonical\" href=\"%s\"></head></html>", canonicalUrl);

        HashMap<String, String> option = new HashMap<String, String>() {{
            put("defaultLang", "ja");
            put("supportedLangs", "ja,en");
            put("urlPattern", "path");
            put("translateCanonicalTag", "false");
        }};
        Settings settings = TestUtil.makeSettings(option);
        HtmlConverter converter = this.createHtmlConverter(settings, requestUrl, html);
        String result = converter.convert("en");

        String expectedSnippet = String.format("<script src=\"//j.wovn.io/1\" data-wovnio=\"key=123456&amp;backend=true&amp;currentLang=en&amp;defaultLang=ja&amp;urlPattern=path&amp;version=%s\" data-wovnio-type=\"fallback\" async></script>", Settings.VERSION);
        String expectedHrefLangs = String.format("<link rel=\"alternate\" hreflang=\"ja\" href=\"%s\">", expectedJaUrl) +
                                   String.format("<link rel=\"alternate\" hreflang=\"en\" href=\"%s\">", expectedEnUrl);
        String expectedContentType = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        String expectedHtml = String.format("<html lang=\"ja\"><head><link rel=\"canonical\" href=\"%s\">%s%s%s</head><body></body></html>", canonicalUrl, expectedSnippet, expectedHrefLangs, expectedContentType);

        assertEquals(expectedHtml, result);
    }

    private String stripExtraSpaces(String html) {
        return html.replaceAll("\\s +", "").replaceAll(">\\s+<", "><");
    }
}
