package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

class HtmlReplaceMarker {
    private final String WOVN_MARKER_PREFIX = "wovn-marker-";
    private final LinkedHashMap<String, String> mappedValues;
    private int keyCount;

    HtmlReplaceMarker() {
        this.mappedValues = new LinkedHashMap<String, String>();
        this.keyCount = 0;
    }

    public String addCommentValue(String original) {
        String key = generateKey();
        String commentHtml = String.format("<!--%s-->", key);
        mappedValues.put(commentHtml, original);
        return key;
    }

    public void addValue(String key, String original) {
        mappedValues.put(key, original);
    }

    public String revert(String markedHTML) {
        ArrayList<String> reversedKeys = new ArrayList<String>(mappedValues.keySet());

        // Reverse the order so longer keys don't get replaced
        // e.g. `wovn-marker-11` doesn't get replaced by `wovn-marker-1`
        Collections.reverse(reversedKeys);

        for (String key: reversedKeys) {
            String original = mappedValues.get(key);
            markedHTML = markedHTML.replace(key, original);
        }
        return markedHTML;
    }

    public String generateKey() {
        String key = WOVN_MARKER_PREFIX + String.valueOf(keyCount);
        keyCount++;
        return key;
    }
}
