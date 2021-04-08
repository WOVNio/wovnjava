package com.github.wovnio.wovnjava;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Tag;

class HtmlConverter {
    private final Document doc;
    private final Settings settings;
    private final HashMap<String, String> hreflangMap;
    private final HtmlReplaceMarker htmlReplaceMarker;

    private static final String[] WOVN_WIDGET_URLS = new String[] {
        "j.wovn.io",
        "j.dev-wovn.io:3000",
        Settings.DefaultApiUrlBase
    };

    HtmlConverter(Settings settings, Headers headers, String original) {
        this.settings = settings;
        this.htmlReplaceMarker = new HtmlReplaceMarker();
        this.hreflangMap = headers.getHreflangUrlMap();
        doc = Jsoup.parse(original);
        doc.outputSettings().prettyPrint(false);
    }

    String strip() {
        removeSnippetAndScripts();
        removeHrefLangIfConflicts();
        removeWovnIgnore();
        removeClassIgnore();
        removeForm();
        insertHtmlLangAttribute();
        return doc.html();
    }

    String convert(String lang) {
        removeSnippet();
        removeHrefLangIfConflicts();
        appendSnippet(lang);
        appendHrefLang();
        replaceContentType();
        insertHtmlLangAttribute();
        return doc.html();
    }

    String restore(String html) {
        return htmlReplaceMarker.revert(html);
    }

    private void removeHrefLangIfConflicts() {
        Elements elements = doc.head().getElementsByTag("link");
        for (Element element : elements) {
            String hreflang = element.attr("hreflang");
            if (hreflang != null && this.hreflangMap.containsKey(hreflang.toLowerCase())) {
                element.remove();
            }
        }
    }

    private boolean isSnippet(Element element) {
        String src = element.attr("src");
        String dataAttr = element.attr("data-wovnio");

        if (src != null) {
            for (String wovnUrl : WOVN_WIDGET_URLS) {
                if (src.contains(wovnUrl)) {
                    return true;
                }
            }
        }

        return dataAttr != null && dataAttr.length() > 0;
    }

    private void removeSnippet() {
        Elements elements = doc.getElementsByTag("script");
        for (Element element : elements) {
            if (isSnippet(element)) {
                element.remove();
            }
        }
    }

    private void removeSnippetAndScripts() {
        Elements elements = doc.getElementsByTag("script");
        for (Element element : elements) {
            if (isSnippet(element)) {
                element.remove();
            } else {
                replaceNodeToMarkerComment(element);
            }
        }
    }

    private void removeWovnIgnore() {
        for (Element element : doc.getElementsByAttribute("wovn-ignore")) {
            replaceNodeToMarkerComment(element);
        }
        for (Element element : doc.getElementsByAttribute("data-wovn-ignore")) {
            replaceNodeToMarkerComment(element);
        }
    }

    private void removeClassIgnore() {
        if (settings.ignoreClasses.isEmpty()) return;
        String classNames = "." + String.join(", .", settings.ignoreClasses);
        Elements elements = doc.select(classNames);
        for (Element element : elements) {
            replaceNodeToMarkerComment(element);
        }
    }

    private void removeForm() {
        Elements elements = doc.getElementsByTag("input");
        for (Element element : elements) {
            String type = element.attr("type");
            if (type != null && type.toLowerCase().equals("hidden")) {
                if (element.hasAttr("value")) {
                    String original = element.attr("value");
                    String key = htmlReplaceMarker.generateKey();
                    element.attr("value", key);
                    htmlReplaceMarker.addValue(key, original);
                }
            }
        }
    }

    private void appendSnippet(String lang) {
        Element js = new Element(Tag.valueOf("script"), "");
        StringBuilder sb = new StringBuilder();
        sb.append("key=");
        sb.append(settings.projectToken);
        sb.append("&backend=true&currentLang=");
        sb.append(lang);
        sb.append("&defaultLang=");
        sb.append(settings.defaultLang.code);
        sb.append("&urlPattern=");
        sb.append(settings.urlPattern);
        sb.append("&version=");
        sb.append(Settings.VERSION);
        if (!settings.sitePrefixPath.isEmpty()) {
            sb.append("&sitePrefixPath=");
            sb.append(settings.sitePrefixPath.replaceFirst("/", ""));
        }
        if (settings.langCodeAliases.size() > 0) {
            sb.append("&langCodeAliases=");
            sb.append(LanguageAliasSerializer.serializeToJson(settings.langCodeAliases));
        }
        if (settings.customDomainLanguages != null) {
            sb.append("&customDomainLangs=");
            sb.append(CustomDomainLanguageSerializer.serializeToJson(settings.customDomainLanguages));
        }
        String key = sb.toString();
        js.attr("src", settings.snippetUrl);
        js.attr("data-wovnio", key);
        js.attr("data-wovnio-type", "fallback");
        js.attr("async", "async");
        doc.head().appendChild(js);
    }

    private void appendHrefLang() {
        for (Map.Entry<String, String> hreflang : this.hreflangMap.entrySet()) {
            Element link = new Element(Tag.valueOf("link"), "");
            link.attr("ref", "alternate");
            link.attr("hreflang", hreflang.getKey());
            link.attr("href", hreflang.getValue());
            doc.head().appendChild(link);
        }
    }

    private void insertHtmlLangAttribute() {
        Element element = doc.getElementsByTag("html").first();

        if (element.attributes().hasKey("lang")) {
            return;
        }
        
        element.attr("lang", this.settings.defaultLang.codeISO639_1);
    }

    private void replaceNodeToMarkerComment(Element element) {
        String commentKey = htmlReplaceMarker.addCommentValue(htmlReplaceMarker.revert(element.html()));
        element.html("");
        element.appendChild(new Comment(commentKey));
    }

    private void replaceContentType() {
        // Remove old content type
        Elements elements = doc.getElementsByTag("meta");
        for (Element element : elements) {
            if (element.attr("http-equiv").toLowerCase().equals("content-type")) {
                element.remove();
            }
        }
        // Set new content type
        Element meta = new Element(Tag.valueOf("meta"), "");
        meta.attr("http-equiv", "Content-Type");
        meta.attr("content", "text/html; charset=UTF-8");
        doc.head().appendChild(meta);
    }
}
