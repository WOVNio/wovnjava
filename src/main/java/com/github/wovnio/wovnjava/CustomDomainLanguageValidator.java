package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.MalformedURLException;
import java.net.URL;

class CustomDomainLanguageValidator {
    private CustomDomainLanguageValidator() {}

    public static ValidationResult validate(ArrayList<CustomDomainLanguage> customDomainLanguageList, ArrayList<Lang> supportedLangs) {
        ArrayList<Lang> includedLangs = new ArrayList<Lang>();
        ArrayList<String> includedUrls = new ArrayList<String>();

        for (CustomDomainLanguage customDomainLanguage : customDomainLanguageList) {
            Lang lang = customDomainLanguage.lang;
            if (includedLangs.contains(lang)) {
                return ValidationResult.failure("Each language can only be specified once for \"customDomainLangs\". \"" + lang.code + "\" is specified more than once.");
            } else {
                includedLangs.add(lang);
            }

            String url = customDomainLanguage.host + customDomainLanguage.path;
            if (includedUrls.contains(url)) {
                return ValidationResult.failure("Each language must be mapped to a unique domain and path combination for \"customDomainLangs\". \"" + url + "\" is used twice.");
            } else {
                includedUrls.add(url);
            }
        }

        if (customDomainLanguageList.size() != supportedLangs.size()) {
            return ValidationResult.failure("Languages in \"customDomainLangs\" does not match \"supportedLangs\". A custom domain must be declared for each supported language.");
        }
        for (CustomDomainLanguage customDomainLanguage : customDomainLanguageList) {
            if (!supportedLangs.contains(customDomainLanguage.lang)) {
                return ValidationResult.failure("Custom domain language is declared for a language not declared in \"supportedLangs\".");
            }
        }
        return ValidationResult.success();
    }
}
