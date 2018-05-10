package com.github.wovnio.wovnjava;

class Interceptor {
    private final String version;
    private final Settings settings;
    private final Headers headers;

    Interceptor(String version, Headers headers, Settings settings) {
        this.version = version;
        this.headers = headers;
        this.settings = settings;
    }

    String translate(String body) {
        String lang = headers.langCode();
        boolean canTranslate = lang.length() > 0 && !lang.equals(settings.defaultLang);
        if (canTranslate) {
            return apiTranslate(body);
        } else {
            return localTranslate(body);
        }
    }

    private String apiTranslate(String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        appendWovnTag(sb, " data-wovnio-type=\"backend_without_api_because_timeout\"");
        appendHrefLangTag(sb);
        sb.append("</head>");
        return body.replace("</head>", sb.toString());
    }

    private String localTranslate(String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        appendWovnTag(sb, " data-wovnio-type=\"backend_without_api\"");
        appendHrefLangTag(sb);
        sb.append("</head>");
        return body.replace("</head>", sb.toString());
    }

    private void appendHrefLangTag(StringBuilder sb) {
        for (String lang : settings.supportedLangs) {
            if (lang.equals(settings.defaultLang)) {
                continue;
            }
            sb.append("<link ref=\"altername\" hreflang=\"");
            sb.append(Lang.normalizeIso639_1(lang));
            sb.append("\" href=\"");
            sb.append(headers.redirectLocation(lang));
            sb.append("\">\n");
        }
    }

    private void appendWovnTag(StringBuilder sb, String extra) {
        sb.append("<script src=\"//j.wovn.io/1\" data-wovnio=\"key=");
        sb.append(settings.projectToken);
        sb.append("&amp;backend=true&amp;currentLang=");
        sb.append(headers.getPathLang());
        sb.append("&amp;defaultLang=");
        sb.append(settings.defaultLang);
        sb.append("&amp;urlPattern=");
        sb.append(settings.urlPattern);
        sb.append(";&amp;langCodeAliases=[]&amp;version=");
        sb.append(version);
        sb.append("\"");
        sb.append(extra);
        sb.append(" async></script>\n");
    }
}
