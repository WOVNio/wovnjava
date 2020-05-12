package com.github.wovnio.wovnjava;

import java.net.URL;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

class UrlContext {
    private final URL context;

    UrlContext(URL currentLocation) {
        this.context = currentLocation;
    }

    public URL getURL() {
        return this.context;
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

    public boolean isPrefixMatchPath(ArrayList<String> paths) {
        if (paths.isEmpty()) {
            return false;
        }
        String contextPath = this.context.getPath().toLowerCase();

        for (String path : paths) {
            if (path.isEmpty()) {
                continue;
            }

            String prefixPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
            String prefixPathTrailingSlash = path.endsWith("/") ? path : (path + "/");

            if (contextPath.equalsIgnoreCase(prefixPath)) {
                return true;
            }
            if (contextPath.startsWith(prefixPathTrailingSlash.toLowerCase())) {
                return true;
            }
        }
        return false;
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
