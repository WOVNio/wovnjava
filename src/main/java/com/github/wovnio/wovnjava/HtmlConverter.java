package com.github.wovnio.wovnjava;

import java.lang.StringBuilder;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

class HtmlConverter {
    private final ArrayList<String> markers = new ArrayList<String>();
    private final String WOVN_MARKER_PREFIX = "wovn-marker-";
    private final Document doc;
    private final Settings settings;

    HtmlConverter(Settings settings, String original) {
        this.settings = settings;
        doc = Jsoup.parse(original);
        doc.outputSettings().prettyPrint(false);
    }

    String strip() {
        removeSnippetAndScripts();
        removeHrefLangIfConflicts();
        removeWovnIgnore();
        removeClassIgnore();
        removeForm();
        return doc.html();
    }

    String convert(Headers headers, String lang, String type) {
        removeSnippet();
        removeHrefLangIfConflicts();
        appendSnippet(lang, type);
        appendHrefLang(headers);
        return doc.html();
    }

    String restore(String html) {
        StringBuilder sb = new StringBuilder();
        String[] list = html.split("<!--" + WOVN_MARKER_PREFIX);

        sb.append(list[0]);
        for (int i=1; i<list.length; i++) {
            String fragment = list[i];
            String commentSuffix = "-->";
            int suffixOffset = fragment.indexOf(commentSuffix);
            String indexString = fragment.substring(0, suffixOffset);
            int index = Integer.parseInt(indexString);
            sb.append(markers.get(index));
            sb.append(fragment.substring(suffixOffset + commentSuffix.length()));
        }
        return sb.toString();
    }

    private void removeHrefLangIfConflicts() {
        Elements elements = doc.head().getElementsByTag("link");
        for (Element element : elements) {
            String hreflang = element.attr("hreflang");
            if (hreflang != null && settings.supportedLangs.contains(hreflang.toLowerCase())) {
                element.remove();
            }
        }
    }

    private boolean isSnippet(String src) {
        return src != null && (src.startsWith("//j.wovn.io/") || src.startsWith("//j.dev-wovn.io:3000/"));
    }

    private void removeSnippet() {
        Elements elements = doc.getElementsByTag("script");
        for (Element element : elements) {
            String src = element.attr("src");
            if (isSnippet(src)) {
                element.remove();
            }
        }
    }

    private void removeSnippetAndScripts() {
        Elements elements = doc.getElementsByTag("script");
        for (Element element : elements) {
            String src = element.attr("src");
            if (isSnippet(src)) {
                element.remove();
            } else {
                replaceNodeToMarkerComment(element);
            }
        }
    }

    private void removeWovnIgnore() {
        Elements elements = doc.getElementsByAttribute("wovn-ignore");
        for (Element element : elements) {
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
                replaceNodeToMarkerComment(element);
            }
        }
    }

    private void appendSnippet(String lang, String type) {
        Element js = new Element(Tag.valueOf("script"), "");
        StringBuilder sb = new StringBuilder();
        sb.append("key=");
        sb.append(settings.projectToken);
        sb.append("&backend=true&currentLang=");
        sb.append(lang);
        sb.append("&defaultLang=");
        sb.append(settings.defaultLang);
        sb.append("&urlPattern=");
        sb.append(settings.urlPattern);
        sb.append("&langCodeAliases={}&version=");
        sb.append(settings.version);
        if (settings.hasSitePrefixPath) {
            sb.append("&site_prefix_path=");
            sb.append(settings.sitePrefixPathWithoutSlash.replaceFirst("/", ""));
        }
        String key = sb.toString();
        js.attr("src", "//j.wovn.io/1");
        js.attr("data-wovnio", key);
        js.attr("data-wovnio-type", type);
        js.attr("async", "async");
        doc.head().appendChild(js);
    }

    private void appendHrefLang(Headers headers) {
        for (String lang : settings.supportedLangs) {
            Element link = new Element(Tag.valueOf("link"), "");
            link.attr("ref", "alternate");
            link.attr("hreflang", Lang.normalizeIso639_1(lang));
            link.attr("href", headers.redirectLocation(lang));
            doc.head().appendChild(link);
        }
    }

    private void replaceNodeToMarkerComment(Element element) {
        Comment comment = new Comment(WOVN_MARKER_PREFIX + String.valueOf(markers.size()));
        element.replaceWith(comment);
        markers.add(restore(element.outerHtml())); // restore original text if element has marker
    }
}
