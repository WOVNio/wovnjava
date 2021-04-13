package com.github.wovnio.wovnjava;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;
import org.easymock.EasyMock;

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
        String expected = "<html lang=\"en\"><head></head><body>\n " + "hello" + "\t\n</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(expected, html);
    }

    public void testRemoveWovnSnippet() throws ConfigurationError {
        String original = "<html><head><script src=\"https://wovn.global.ssl.fastly.net/widget/abcdef\"></script><script src=\"https://j.dev-wovn.io:3000\"></script><script src=\"//j.wovn.io/1\" data-wovnio=\"key=NCmbvk&amp;backend=true&amp;currentLang=en&amp;defaultLang=en&amp;urlPattern=path&amp;version=0.0.0\" data-wovnio-type=\"backend_without_api\" async></script></head><body></body></html>";
        String removedHtml = "<html lang=\"en\"><head></head><body></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(removedHtml, stripExtraSpaces(html));
        assertEquals(removedHtml, stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveScripts() throws ConfigurationError {
        String original = "<html lang=\"en\"><head><script>alert(1)</script></head><body>a <script>console.log(1)</script>b</body></html>";
        String removedHtml = "<html lang=\"en\"><head><script><!--wovn-marker-0--></script></head><body>a <script><!--wovn-marker-1--></script>b</body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(removedHtml, stripExtraSpaces(html));
        assertEquals(stripExtraSpaces(original), stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveHrefLangIfConflicts() throws ConfigurationError {
        String original = "<html lang=\"en\"><head><link ref=\"altername\" hreflang=\"en\" href=\"http://localhost:8080/\"><link ref=\"altername\" hreflang=\"ja\" href=\"http://localhost:8080/ja/\"><link ref=\"altername\" hreflang=\"ar\" href=\"http://localhost:8080/ar/\"></head><body></body></html>";
        String removedHtml = "<html lang=\"en\"><head><link ref=\"altername\" hreflang=\"ar\" href=\"http://localhost:8080/ar/\"></head><body></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(removedHtml, stripExtraSpaces(html));
        assertEquals(removedHtml, stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveWovnIgnore() throws ConfigurationError {
        String original = "<html lang=\"en\"><head></head><body><div>Hello <span wovn-ignore>Duke</span><span data-wovn-ignore>Silver</span>.</div></body></html>";
        String removedHtml = "<html lang=\"en\"><head></head><body><div>Hello <span wovn-ignore><!--wovn-marker-0--></span><span data-wovn-ignore><!--wovn-marker-1--></span>.</div></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();
        assertEquals(removedHtml, stripExtraSpaces(html));
        assertEquals(original, stripExtraSpaces(converter.restore(html)));
    }

    public void testRemoveClassIgnore() throws ConfigurationError {
        String original = "<html lang=\"en\"><head></head><body>" +
          "<p class=\"no-ignore\">The pizza needs <b class=\"ingredient\">pineapple</b>, <span class=\"name\">Chad</span>!</p>" +
          "<p class=\"ignore-me\">It's a fruit, <span class=\"name\">Louie</span>!</p>" +
          "</body></html>";
        String removedHtml = "<html lang=\"en\"><head></head><body>" +
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
        String original = "<html lang=\"en\"><head></head><body><form><input type=\"hidden\" name=\"csrf\" value=\"random\"><INPUT TYPE=\"HIDDEN\" NAME=\"CSRF_TOKEN\" VALUE=\"RANDOM\"></form></body></html>";
        String removedHtml = "<html lang=\"en\"><head></head><body><form><input type=\"hidden\" name=\"csrf\" value=\"wovn-marker-0\"><input TYPE=\"HIDDEN\" NAME=\"CSRF_TOKEN\" value=\"wovn-marker-1\"></form></body></html>";
        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{ put("supportedLangs", "en,fr,ja"); }});
        HtmlConverter converter = this.createHtmlConverter(settings, location, original);
        String html = converter.strip();

        assertEquals(removedHtml, stripExtraSpaces(html));
        // jsoup make lower case tag name
        assertEquals(original.replace("INPUT", "input").replace("VALUE", "value"), stripExtraSpaces(converter.restore(html)));
    }

    public void testNested() throws ConfigurationError {
        String original = "<html lang=\"en\"><head></head><body><form wovn-ignore><script></script><input type=\"hidden\" name=\"csrf\" value=\"random\"><INPUT TYPE=\"HIDDEN\" NAME=\"CSRF_TOKEN\" value=\"RANDOM\"></form></body></html>";
        String removedHtml = "<html lang=\"en\"><head></head><body><form wovn-ignore><!--wovn-marker-1--></form></body></html>";
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
        String expectedHrefLangs = "<link ref=\"alternate\" hreflang=\"ja\" href=\"https://site.com/global/tokyo/\">" +
                                   "<link ref=\"alternate\" hreflang=\"en\" href=\"https://site.com/global/en/tokyo/\">" +
                                   "<link ref=\"alternate\" hreflang=\"th\" href=\"https://site.com/global/th/tokyo/\">";
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
        String expectedHrefLangs = "<link ref=\"alternate\" hreflang=\"ja\" href=\"https://site.com/japan/tokyo\">" +
                                   "<link ref=\"alternate\" hreflang=\"en\" href=\"https://site.com/en/tokyo\">";
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
        String expectedHrefLangs = "<link ref=\"alternate\" hreflang=\"ja\" href=\"https://site.co.jp/tokyo\">" +
                                   "<link ref=\"alternate\" hreflang=\"en\" href=\"https://site.com/english/tokyo\">";
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
            "<link ref=\"altername\" hreflang=\"en\" href=\"http://localhost:8080/\"><link ref=\"altername\" hreflang=\"ja\" href=\"http://localhost:8080/ja/\"><link ref=\"altername\" hreflang=\"ar\" href=\"http://localhost:8080/ar/\">" +
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
            "<link ref=\"altername\" hreflang=\"ar\" href=\"http://localhost:8080/ar/\">" +
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

    private String stripExtraSpaces(String html) {
        return html.replaceAll("\\s +", "").replaceAll(">\\s+<", "><");
    }
}
