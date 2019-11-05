package com.github.wovnio.wovnjava;

import java.net.URL;
import java.net.MalformedURLException;

class UrlContext {
    private final URL context;

    UrlContext(URL currentLocation) {
        this.context = currentLocation;
    }

    public URL createAbsoluteUrl(String location) {
        try {
            return new URL(this.context, location);
        } catch (MalformedURLException e) {
            return null; // return `this.context` instead?
        }
    }
}
