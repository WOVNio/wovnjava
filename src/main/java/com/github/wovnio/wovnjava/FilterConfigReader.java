package com.github.wovnio.wovnjava;

import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.FilterConfig;

class FilterConfigReader {
    private final FilterConfig config;

    FilterConfigReader(FilterConfig config) {
        this.config = config;
    }

    String getStringParameter(String paramName) {
        String param = this.config.getInitParameter(paramName);
        if (param == null) {
            return "";
        } else {
            return param.trim();
        }
    }

    int getIntParameter(String paramName) throws ConfigurationError {
        String param = this.config.getInitParameter(paramName);
        if (param == null || param.isEmpty()) {
            return 0;
        }
        int n;
        try {
            n = Integer.parseInt(param);
        } catch (NumberFormatException e) {
            throw new ConfigurationError("NumberFormatException when parsing int parameter for " + paramName);
        }
        return n;
    }

    boolean getBoolParameter(String paramName) {
        String param = this.config.getInitParameter(paramName);
        if (param == null) {
            return false;
        }
        param = param.trim().toLowerCase();
        return param.equals("on") || param.equals("true") || param.equals("1");
    }

    ArrayList<String> getArrayParameter(String paramName) {
        ArrayList<String> al = new ArrayList<String>();
        String param = this.getStringParameter(paramName);
        if (!param.isEmpty()) {
            String[] params = param.split("\\s*,\\s*");
            Collections.addAll(al, params);
        }
        return al;
    }
}
