package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

class FileExtensionMatcher {
    private final Pattern TEXT_FILE_PATTERN;
    private final Pattern IMAGE_FILE_PATTERN;
    private final Pattern AUDIO_FILE_PATTERN;
    private final Pattern VIDEO_FILE_PATTERN;
    private final Pattern DOC_FILE_PATTERN;

    FileExtensionMatcher() {
        this.TEXT_FILE_PATTERN = Pattern.compile("\\.(?:(?:css)|(?:js)|(?:txt))$");
        this.IMAGE_FILE_PATTERN = Pattern.compile("(\\.(jpe|jpe?g|bmp|gif|png|btif|tiff?|psd|djvu?|xif|wbmp|webp|p(n|b|g|p)m|rgb|tga|x(b|p)m|xwd|pic|ico|fh(c|4|5|7)?|xif|f(bs|px|st)))$");
        this.AUDIO_FILE_PATTERN = Pattern.compile("(\\.(mp(3|2)|m(p?2|3|p?4|pg)a|midi?|kar|rmi|web(m|a)|aif(f?|c)|w(ma|av|ax)|m(ka|3u)|sil|s3m|og(a|g)|uvv?a))$");
        this.VIDEO_FILE_PATTERN = Pattern.compile("(\\.(m(x|4)u|fl(i|v)|3g(p|2)|jp(gv|g?m)|mp(4v?|g4|e?g)|m(1|2)v|ogv|m(ov|ng)|qt|uvv?(h|m|p|s|v)|dvb|mk(v|3d|s)|f4v|as(x|f)|w(m(v|x)|vx)))$");
        this.DOC_FILE_PATTERN = Pattern.compile("(\\.((g|7)?zip|7z|tar|gz|rar|ez|aw|atom(cat|svc)?|(cc)?xa?ml|cdmi(a|c|d|o|q)?|epub|g(ml|px|xf)|jar|js|ser|class|json(ml)?|do(c|t)(m|x)?|xls(m|x)?|xps|pp(a|tx?|s)m?|potm?|sldm|mp(p|t)|bin|dms|lrf|mar|so|dist|distz|m?pkg|bpk|dump|rtf|tfi|pdf|pgp|apk|o(t|d)(b|c|ft?|g|h|i|p|s|t)))$");
    }

    public boolean isFile(String path) {
        // Reduce strings for performance and keep a simple case
        path = path.replaceFirst("^.*/", "/"); // strip directries
        path = path.replaceFirst("[?#].*$", ""); // strip query or/and hash
        path = path.toLowerCase();

        return TEXT_FILE_PATTERN.matcher(path).find()
            || IMAGE_FILE_PATTERN.matcher(path).find()
            || AUDIO_FILE_PATTERN.matcher(path).find()
            || VIDEO_FILE_PATTERN.matcher(path).find()
            || DOC_FILE_PATTERN.matcher(path).find();
    }
}
