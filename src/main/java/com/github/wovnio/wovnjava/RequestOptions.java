package com.github.wovnio.wovnjava;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

class RequestOptions {
    /*
     * disableMode:
     *      - do nothing to the request
     */
    private boolean disableMode;
    /*
     * cacheDisableMode:
     *      - bypass cache for request to translation API
     * Only available if debugMode is also turned on server side.
     */
    private boolean cacheDisableMode;
    /*
     * debugMode:
     *      - activate extra debugging information.
     *      - send "debug_mode=true" to translation API
     *      - bypass cache for request to translation API
     * Only available if debugMode is also turned on server side.
     */
    private boolean debugMode;

    private String overrideEncoding;

    RequestOptions(Settings settings, ServletRequest request) {
        this.disableMode = false;
        this.cacheDisableMode = false;
        this.debugMode = false;
        this.overrideEncoding = null;

        String query = ((HttpServletRequest)request).getQueryString();
        if (query != null) {
            this.disableMode = query.matches("(.*)wovnDisable(.*)");
            if (settings.debugMode) {
                this.cacheDisableMode = query.matches("(.*)wovnCacheDisable(.*)");
                this.debugMode = query.matches("(.*)wovnDebugMode(.*)");

                Pattern pattern = Pattern.compile("(.*)wovnEncodingOverride=([^&]*)");
                Matcher m = pattern.matcher(query);
                if (m.matches()) {
                    String encoding = m.group(2);
                    this.overrideEncoding = encoding;
                }
            }
        }
    }

    public boolean getDisableMode() {
        return this.disableMode;
    }

    public boolean getCacheDisableMode() {
        return this.cacheDisableMode;
    }

    public boolean getDebugMode() {
        return this.debugMode;
    }

    public String getOverrideEncoding() {
        return this.overrideEncoding;
    }
}
