package com.github.wovnio.wovnjava;

import java.util.HashMap;

import org.jsoup.nodes.Element;

class HtmlMarkerManager {
    private final HashMap<String, Element> commentHistory = new HashMap<String, Element>();

    HtmlMarkerManager() {}
    
    public int addComment(Element original, String commentMarker) {
        commentHistory.put(commentMarker, original);
        return this.commentHistory.size() - 1;
    }

    public Element getOriginal(String commentMarker) {
        return this.commentHistory.get(commentMarker);
    }

    public int getCommentId() {
        return this.commentHistory.size();
    }
}
