package com.github.wovnio.wovnjava;

class SupportedLanguage {
    public final Lang lang;
    public final String identifier;
    public final boolean isDefaultLanguage;

    public SupportedLanguage(Lang lang, String identifier, boolean isDefaultLanguage) {
        this.lang = lang;
        this.identifier = identifier;
        this.isDefaultLanguage = isDefaultLanguage;
    }
}
