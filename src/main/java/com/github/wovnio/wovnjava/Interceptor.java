package com.github.wovnio.wovnjava;


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
        Lang lang = headers.getRequestLang();
        if (lang != settings.defaultLang) {
            return apiTranslate(lang.code, body);
        } else {
            return localTranslate(settings.defaultLang.code, body);
        }
    }

    private String apiTranslate(String lang, String body) {
        try {
            HtmlConverter converter = new HtmlConverter(this.settings, this.headers, body);
            String convertedBody = converter.strip();
            String translatedBody = api.translate(lang, convertedBody);
            responseHeaders.setApiStatus("Success");
            return converter.restore(translatedBody);
        } catch (ApiException e) {
            responseHeaders.setApiStatus(e.getType());
            WovnLogger.log("ApiException", e);
            return apiTranslateFail(body, lang);
        }
    }

    private String apiTranslateFail(String body, String lang) {
        return new HtmlConverter(this.settings, this.headers, body).convert(lang);
    }

    private String localTranslate(String lang, String body) {
        return new HtmlConverter(this.settings, this.headers, body).convert(lang);
    }
}
