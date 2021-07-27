package com.github.wovnio.wovnjava;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

class HtmlChecker {
    public boolean isTextFileContentType(String contentType) {
        return contentType == null || contentType.toLowerCase().contains("text/") || contentType.toLowerCase().contains("html");
    }

    public boolean canTranslate(String contentType, String html) {
        return canTranslateContentType(contentType) &&
            canTranslateContent(html);
    }

    public boolean canTranslateContentType(String type) {
        return type == null || type.toLowerCase().contains("html");
    }

    public boolean canTranslateContent(String html) {
        if (html == null) {
            return false;
        }

        String head = getHead(html).toLowerCase();
        return isHtml(head) && !isAmp(head);
    }

    private boolean isAmp(String head) {
        Element element = Jsoup.parse(head).getElementsByTag("html").first();
        return element != null && (element.hasAttr("amp") || element.hasAttr("⚡"));
    }

    private boolean isHtml(String head) {
        // TODO: implement better HTML check, keyword might appear 
        // after the sample.
        return head.contains("<?xml")
            || head.contains("<!doctype")
            || head.contains("<html")
            || head.contains("<xhtml");
    }

    private String getHead(String html) {
        return html.substring(0, Math.min(html.length(), BUFFER_SIZE));
    }

    private final int BUFFER_SIZE = 256;
}
