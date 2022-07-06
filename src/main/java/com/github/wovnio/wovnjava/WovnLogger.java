package com.github.wovnio.wovnjava;


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class WovnLogger {
    private static Logger logger = Logger.getLogger("wovnLogger");
    private static boolean enabled = false;
    private static boolean isDebugMode = false;
    private static String uuid = "NO_UUID";
    private final static String prefix = "WOVN";

    private static ArrayList<String> requestLogs;

    public static void enable() {
        WovnLogger.enabled = true;
    }

    public static void disable() {
        WovnLogger.enabled = false;
    }

    public static void setDebugMode(boolean isDebugMode) {
        WovnLogger.isDebugMode = isDebugMode;
    }

    public static void setUUID(String uuid) {
        WovnLogger.uuid = uuid;
    }
    public static void clear() {
        WovnLogger.requestLogs = new ArrayList<>();
    }
    
    public static String getRequestLogsHtmlComment() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!--");
        sb.append("\n");
        for (int i = 0; i < WovnLogger.requestLogs.size(); i++) {
            sb.append(WovnLogger.requestLogs.get(i));
            sb.append("\n");
        }
        sb.append("-->");
        return sb.toString();
    }

    public static String getUUID() {
        return WovnLogger.uuid;
    }

    public static void log(String message, Exception e) {
        if (!WovnLogger.enabled) {
            return;
        }
        String formattedMessage = String.format("[%s][%s] %s", WovnLogger.prefix, WovnLogger.uuid, message);
        if (WovnLogger.isDebugMode) {
            WovnLogger.requestLogs.add(formattedMessage);
        }
        WovnLogger.logger.log(Level.INFO, formattedMessage, e);
    }

    public static void log(String message) {
        if (!WovnLogger.enabled) {
            return;
        }
        String formattedMessage = String.format("[%s][%s] %s", WovnLogger.prefix, WovnLogger.uuid, message);
        if (WovnLogger.isDebugMode) {
            WovnLogger.requestLogs.add(formattedMessage);
        }
        WovnLogger.logger.log(Level.INFO, formattedMessage);
    }
}
