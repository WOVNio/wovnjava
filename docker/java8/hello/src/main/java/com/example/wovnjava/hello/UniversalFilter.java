package com.example.wovnjava.hello;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class UniversalFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        BufferedReader requestReader = request.getReader();
        String requestBody = requestReader.lines().collect(Collectors.joining());
        JSONObject requestJson = new JSONObject(requestBody).getJSONObject("response");
        int status = requestJson.getInt("status");
        String responseBody = requestJson.getString("body");
        JSONObject headers = requestJson.getJSONObject("headers");
        String contentType = requestJson.getString("content-type");

        ((HttpServletResponse)response).setHeader("Content-Type", contentType);

        Iterator<String> headersIterator = headers.keys();
        while (headersIterator.hasNext()) {
            String headerName = headersIterator.next();
            ((HttpServletResponse)response).setHeader(headerName, headers.getString(headerName));
        }

        ((HttpServletResponse)response).setStatus(status);
        PrintWriter out = ((HttpServletResponse)response).getWriter();
        out.write(responseBody);
        out.close();
    }

    @Override
    public void destroy() {
    }
}