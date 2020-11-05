package com.github.wovnio.wovnjava;

import java.util.HashMap;

class HtmlReplaceMarker {
    private final String WOVN_MARKER_PREFIX = "wovn-marker-";
    private final HashMap<String, String> mappedValues;
    private int keyCount;

    HtmlReplaceMarker() {
        this.mappedValues = new HashMap<String, String>();
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
        for (String key: mappedValues.keySet()) {
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
