package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletResponse;

class Interceptor {
    private final Settings settings;
    private final Headers headers;
    private final Api api;
    private final ResponseHeaders responseHeaders;

    Interceptor(Headers headers, Settings settings, Api api, ResponseHeaders responseHeaders) {
        this.headers = headers;
        this.settings = settings;
        this.api = api;
        this.responseHeaders = responseHeaders;
    }

    String translate(String body) {
        String lang = headers.getRequestLang();
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
            responseHeaders.setApiStatus("Success");
            return converter.restore(translatedBody);
        } catch (ApiException e) {
            responseHeaders.setApiStatus(e.getType());
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
