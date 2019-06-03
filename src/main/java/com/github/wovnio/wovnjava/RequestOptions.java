package com.github.wovnio.wovnjava;

import javax.servlet.http.HttpServletRequest;

class RequestOptions {
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

    RequestOptions(Settings settings, HttpServletRequest request) {
        this.cacheDisableMode = false;
        this.debugMode = false;

        if (settings.debugMode) {
            String query = request.getQueryString();
            if (query != null) {
                this.cacheDisableMode = query.matches("(.*)wovnCacheDisable(.*)");
                this.debugMode = query.matches("(.*)wovnDebugMode(.*)");
            }
        }
    }

    public boolean getCacheDisableMode() {
        return this.cacheDisableMode;
    }

    public boolean getDebugMode() {
        return this.debugMode;
    }
}
