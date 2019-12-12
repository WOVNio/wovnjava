package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.net.URL;

class CustomDomainLanguages {
    public final ArrayList<CustomDomainLanguage> customDomainLanguageList;

    public CustomDomainLanguages(ArrayList<CustomDomainLanguage> customDomainLanguageList) {
        Collections.sort(customDomainLanguageList, new SortByDecendingPathLength()); 
        this.customDomainLanguageList = customDomainLanguageList;
    }

    public CustomDomainLanguage getCustomDomainLanguageByLang(Lang lang) {
        for (CustomDomainLanguage customDomainLanguage : this.customDomainLanguageList) {
            if (customDomainLanguage.lang == lang) {
                return customDomainLanguage;
            }
        }
        return null;
    }

    public CustomDomainLanguage getCustomDomainLanguageByUrl(URL url) {
        for (CustomDomainLanguage customDomainLanguage : this.customDomainLanguageList) {
            if (customDomainLanguage.isMatch(url)) {
                return customDomainLanguage;
            }
        }
        return null;
    }
}

class SortByDecendingPathLength implements Comparator<CustomDomainLanguage> { 
    public int compare(CustomDomainLanguage a, CustomDomainLanguage b) { 
        return b.path.length() - a.path.length(); 
    } 
} 
