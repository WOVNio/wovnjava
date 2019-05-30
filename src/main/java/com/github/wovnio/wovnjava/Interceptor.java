package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletResponse;

class Interceptor {
    private final Settings settings;
    private final Headers headers;
    private final Api api;
    private final HttpServletResponse response;

    Interceptor(Headers headers, Settings settings, Api api, HttpServletResponse response) {
        this.headers = headers;
        this.settings = settings;
        this.api = api;
        this.response = response;
    }

    String translate(String body) {
        String lang = headers.getPathLang();
        boolean canTranslate = lang.length() > 0 && !lang.equals(settings.defaultLang);
        if (canTranslate) {
            return apiTranslate(lang, body);
        } else {
            return localTranslate(settings.defaultLang, body);
        }
    }

    private String apiTranslate(String lang, String body) {
        try {
            HtmlConverter converter = new HtmlConverter(settings, body);
            String convertedBody = converter.strip();
            String translatedBody = api.translate(lang, convertedBody);
            this.response.setHeader("X-Wovn-Api", "Success");
            return converter.restore(translatedBody);
        } catch (ApiException e) {
            this.response.setHeader("X-Wovn-Api", e.getType());
            Logger.log.error("ApiException", e);
            return apiTranslateFail(body, lang);
        }
    }

    private String apiTranslateFail(String body, String lang) {
        return new HtmlConverter(settings, body).convert(headers, lang);
    }

    private String localTranslate(String lang, String body) {
        return new HtmlConverter(settings, body).convert(headers, lang);
    }
}
