package com.github.wovnio.wovnjava;

import java.util.regex.Pattern;

final class UrlPath {
    private UrlPath() {}

    public static String getPath(String url) {
        String hostPathQuery = Pattern.compile("^https?://").matcher(url).replaceFirst("");
        String pathQuery = Pattern.compile("^[^/?]*").matcher(hostPathQuery).replaceFirst("");
        String path = Pattern.compile("\\?.*$").matcher(pathQuery).replaceFirst("");
        return path;
    }

    public static String removeFile(String path) {
        if (path.endsWith("/")) {
            return path;
        } else {
            int index = path.lastIndexOf("/");
            if (index > 0) {
                return path.substring(0, index + 1);
            } else {
                return "/";
            }
        }
    }

    public static String join(String left, String right) {
        boolean l = left.endsWith("/");
        boolean r = right.startsWith("/");
        if (l && r) {
            return left + right.substring(1);
        } else if (l || r) {
            return left + right;
        } else {
            return left + "/" + right;
        }
    }

    public static String normalize(String path) {
        path = replaceRepeat(path, "/./", "/");            // remove redundant relative path
        path = replaceRepeat(path, "/[^/]+/\\.\\./", "/"); // combine relative path.         eg. '/dir/../file' to '/file'
        path = path.replace("/../", "/");                  // remove nonsense relative path. eg. '/../dir/file' to '/dir/file'
        path = path.replaceFirst("\\.\\./", "");           // remove nonsense relative path. eg.'../dir/file' to '/dir/file'
        return path.length() == 0 ? "/" : path;
    }

    private static String replaceRepeat(String path, String pattern, String replacement) {
        while(true) {
            String newPath = path.replaceAll(pattern, replacement);
            if (path.length() == newPath.length()) {
                return path;
            } else {
                path = newPath;
            }
        }
    }
}
