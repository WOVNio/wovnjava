package com.github.wovnio.wovnjava;

import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

class HtmlChecker {
    public boolean isTextFileContentType(String contentType) {
        return contentType == null 
            || contentType.toLowerCase().contains("text/")
            || contentType.toLowerCase().contains("html");
    }

    public boolean canTranslate(HttpServletResponse response, String html) {
        return canTranslateContentType(response.getContentType()) && canTranslateContent(html)
                && canTranslateStatusCode(response.getStatus());
    }

    private boolean canTranslateContentType(String type) {
        return type == null || type.toLowerCase().contains("html");
    }

    private boolean canTranslateContent(String html) {
        if (html == null) {
            return false;
        }

        String head = getHead(html).toLowerCase();
        return isHtml(head) && !isAmp(head);
    }

    private boolean canTranslateStatusCode(int statusCode) {
        return statusCode >= 200 && statusCode < 300 || statusCode >= 400;
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
