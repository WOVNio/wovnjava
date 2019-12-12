package com.github.wovnio.wovnjava;

import java.net.URL;
import java.util.ArrayList;

class CustomDomainLanguage {
    public final String host;
    public final String path;
    public final Lang lang;

    public CustomDomainLanguage(String host, String path, Lang lang) {
        this.host = host;
        this.path = path;
        this.lang = lang;
    }

    public boolean isMatch(URL url) {
        if (url == null) return false;

        return this.host.equalsIgnoreCase(url.getHost()) && this.pathIsEqualOrSubsetOf(this.path, url.getPath());
    }

    private boolean pathIsEqualOrSubsetOf(String basePath, String testPath) {
        String[] basePathList = removeEmptyStrings(basePath.split("/"));
        String[] testPathList = removeEmptyStrings(testPath.split("/"));

        if (basePathList.length > testPathList.length) {
            return false;
        }

        for (int i = 0; i < basePathList.length; i++) {
            if (!basePathList[i].equalsIgnoreCase(testPathList[i])) {
                return false;
            }
        }

        return true;
    }

    private String[] removeEmptyStrings(String[] arr) {
        ArrayList<String> nonEmptyElements = new ArrayList<String>();
        for (String element : arr) {
            if (!element.isEmpty()) nonEmptyElements.add(element);
        }
        return nonEmptyElements.toArray(new String[0]);
    }
}
