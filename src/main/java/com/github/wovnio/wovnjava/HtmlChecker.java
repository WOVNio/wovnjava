package com.github.wovnio.wovnjava;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HtmlChecker {
    public boolean canTranslate(String contentType, String path, String html) {
        return canTranslateContentType(contentType) &&
            canTranslatePath(path) &&
            canTranslateContent(html);
    }

    public boolean canTranslateContentType(String type) {
        return type == null || type.toLowerCase().contains("html");
    }

    public boolean canTranslatePath(String path) {
        if (path == null) {
            return true;
        }

        // Reduce strings for performance and keep a simple case
        path = path.replaceFirst("^.*/", "/"); // strip directries
        path = path.replaceFirst("[?#].*$", ""); // strip query or/and hash
        path = path.toLowerCase();

        return !TEXT_FILE_PATTERN.matcher(path).find()
            && !IMAGE_FILE_PATTERN.matcher(path).find()
            && !AUDIO_FILE_PATTERN.matcher(path).find()
            && !VIDEO_FILE_PATTERN.matcher(path).find()
            && !DOC_FILE_PATTERN.matcher(path).find();
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
        return head.contains("<?xml")
            || head.contains("<!doctype")
            || head.contains("<html")
            || head.contains("<xhtml");
    }

    private String getHead(String html) {
        return html.substring(0, Math.min(html.length(), BUFFER_SIZE));
    }

    private final int BUFFER_SIZE = 256;

    private final Pattern TEXT_FILE_PATTERN = Pattern.compile("\\.(?:(?:css)|(?:js)|(?:txt))$");

    // The pattern come from WOVN.php/src/wovnio/wovnphp/Utils.php
    private final Pattern IMAGE_FILE_PATTERN = Pattern.compile("(\\.((?!jp$)jpe?g?|bmp|gif|png|btif|tiff?|psd|djvu?|xif|wbmp|webp|p(n|b|g|p)m|rgb|tga|x(b|p)m|xwd|pic|ico|fh(c|4|5|7)?|xif|f(bs|px|st)))$");
    private final Pattern AUDIO_FILE_PATTERN = Pattern.compile("(\\.(mp(3|2)|m(p?2|3|p?4|pg)a|midi?|kar|rmi|web(m|a)|aif(f?|c)|w(ma|av|ax)|m(ka|3u)|sil|s3m|og(a|g)|uvv?a))$");
    private final Pattern VIDEO_FILE_PATTERN = Pattern.compile("(\\.(m(x|4)u|fl(i|v)|3g(p|2)|jp(gv|g?m)|mp(4v?|g4|e?g)|m(1|2)v|ogv|m(ov|ng)|qt|uvv?(h|m|p|s|v)|dvb|mk(v|3d|s)|f4v|as(x|f)|w(m(v|x)|vx)))$");
    private final Pattern DOC_FILE_PATTERN = Pattern.compile("(\\.((g|7)?zip|tar(.gz)?|gz|rar|ez|aw|atom(cat|svc)?|(cc)?xa?ml|cdmi(a|c|d|o|q)?|epub|g(ml|px|xf)|jar|js|ser|class|json(ml)?|do(c|t)m?|xps|pp(a|tx?|s)m?|potm?|sldm|mp(p|t)|bin|dms|lrf|mar|so|dist|distz|m?pkg|bpk|dump|rtf|tfi|pdf|pgp|apk|o(t|d)(b|c|ft?|g|h|i|p|s|t)))$");
}
