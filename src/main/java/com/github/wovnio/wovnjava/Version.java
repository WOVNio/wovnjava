package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.util.Properties;

class Version {
    private static final String fallbackVersion = "projectPropertiesErrorFallback";

    public static String readProjectVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(Version.class.getClassLoader().getResourceAsStream("project.properties"));
            String projectVersion = properties.getProperty("version");
            if (projectVersion.length() > 0) {
                return projectVersion;
            } else {
                return fallbackVersion;
            }
        } catch (IOException e) {
            return fallbackVersion;
        }
    }
}
