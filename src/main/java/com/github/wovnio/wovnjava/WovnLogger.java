package com.github.wovnio.wovnjava;


import java.util.logging.Level;
import java.util.logging.Logger;

class WovnLogger {
    private static Logger logger = Logger.getLogger("wovnLogger");
    private static boolean enabled = false;
    private static String uuid = "NO_UUID";
    private final static String prefix = "WOVN";

    public static void enbale(String uuid) {
        WovnLogger.uuid = uuid;
        WovnLogger.enabled = true;
    }

    public static void disable() {
        WovnLogger.enabled = false;
    }

    public static void log(String message, Exception e) {
        if (!WovnLogger.enabled) {
            return;
        }
        WovnLogger.logger.log(Level.INFO, String.format("[%s][%s] %s", WovnLogger.prefix, WovnLogger.uuid, message), e);
    }

    public static void log(String message) {
        if (!WovnLogger.enabled) {
            return;
        }
        WovnLogger.logger.log(Level.INFO, String.format("[%s][%s] %s", WovnLogger.prefix, WovnLogger.uuid, message));
    }
}
