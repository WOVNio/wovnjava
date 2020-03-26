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
            return null;
        } else {
            return param.trim();
        }
    }

    int getIntParameter(String paramName) throws ConfigurationError {
        String param = this.config.getInitParameter(paramName);
        if (param == null) {
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

    boolean getBoolParameterDefaultFalse(String paramName) {
        String param = this.config.getInitParameter(paramName);
        if (param == null) {
            return false;
        }
        param = param.trim().toLowerCase();
        return param.equals("on") || param.equals("true") || param.equals("1");
    }

    boolean getBoolParameterDefaultTrue(String paramName) {
        String param = this.config.getInitParameter(paramName);
        if (param == null) {
            return true;
        }
        param = param.trim().toLowerCase();
        return !(param.equals("off") || param.equals("false") || param.equals("0"));
    }

    ArrayList<String> getArrayParameter(String paramName) {
        String param = this.getStringParameter(paramName);
        ArrayList<String> al = new ArrayList<String>();
        if (param != null && !param.isEmpty()) {
            String[] params = param.split("\\s*,\\s*");
            Collections.addAll(al, params);
        }
        return al;
    }
}
