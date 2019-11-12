package com.github.wovnio.wovnjava;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

class UrlContext {
    private final URL context;

    UrlContext(URL currentLocation) {
        this.context = currentLocation;
    }

    public URL resolve(String location) {
        try {
            URL url = new URL(this.context, location);
            url = new URI(url.toString()).normalize().toURL();
            return this.stripSlashDotDot(url);
        } catch (MalformedURLException e) {
            return null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public boolean isSameHost(URL url) {
        return this.context.getHost().equalsIgnoreCase(url.getHost());
    }

    /*
     * Remove sections of the URL path that starts with "/.."
     */
    private URL stripSlashDotDot(URL url) throws MalformedURLException {
        String path = url.getPath();
        if (!path.startsWith("/..")) return url;

        String normalizedPath = path.replaceAll("^(\\/\\.\\.)+", "");
        String query = url.getQuery();
        query = (query == null || query.isEmpty()) ? "" : "?" + query;
        return new URL(url, normalizedPath + query);
    }
}
