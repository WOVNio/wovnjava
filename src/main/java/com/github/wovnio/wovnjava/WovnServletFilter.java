package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WovnServletFilter implements Filter {
    private Settings settings;
    private UrlLanguagePatternHandler urlLanguagePatternHandler;
    private final HtmlChecker htmlChecker = new HtmlChecker();

    public static final String VERSION = Settings.VERSION;  // for backward compatibility

    @Override
    public void init(FilterConfig config) throws ServletException {
        try {
            this.settings = new Settings(config);
            this.urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
        } catch (ConfigurationError e) {
            throw new ServletException("WovnServletFilter ConfigurationError: " + e.getMessage());
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        boolean isRequestAlreadyProcessed = false;
        if (((HttpServletResponse)response).containsHeader("X-Wovn-Handler")) {
            isRequestAlreadyProcessed = true;
        } else {
            ((HttpServletResponse)response).setHeader("X-Wovn-Handler", "wovnjava_" + Settings.VERSION);
        }

        RequestOptions requestOptions = new RequestOptions(this.settings, request);
        Headers headers = new Headers((HttpServletRequest)request, this.settings, this.urlLanguagePatternHandler);

        boolean canProcessRequest = !isRequestAlreadyProcessed &&
                                    !requestOptions.getDisableMode() &&
                                    headers.getIsValidPath() &&
                                    htmlChecker.canTranslatePath(headers.pathName);

        if (headers.getShouldRedirectToDefaultLang()) {
            /* Send 302 redirect to equivalent URL without default language code */
            ((HttpServletResponse) response).sendRedirect(headers.getClientRequestUrlWithoutLangCode());
        } else if (canProcessRequest) {
            /* Process the request */
            tryTranslate(headers, requestOptions, (HttpServletRequest)request, (HttpServletResponse)response, chain);
        } else {
            /* Strip language code and pass through the request and response untouched */
            WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest((HttpServletRequest)request, headers);
            chain.doFilter(wovnRequest, response);
        }
    }

    @Override
    public void destroy() {
    }

    private void tryTranslate(Headers headers, RequestOptions requestOptions, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(request, headers);
        WovnHttpServletResponse wovnResponse = new WovnHttpServletResponse(response, headers);

        ResponseHeaders responseHeaders = new ResponseHeaders(response);
        responseHeaders.setApiStatus("Unused");

        if (settings.urlPattern.equals("path") && headers.getRequestLang().length() > 0) {
            wovnRequest.getRequestDispatcher(headers.pathNameKeepTrailingSlash).forward(wovnRequest, wovnResponse);
        } else {
            chain.doFilter(wovnRequest, wovnResponse);
        }

        String originalBody = wovnResponse.toString();
        if (originalBody != null) {
            // text
            String body = null;
            if (htmlChecker.canTranslate(response.getContentType(), headers.pathName, originalBody)) {
                // html
                Api api = new Api(settings, headers, requestOptions, responseHeaders);
                Interceptor interceptor = new Interceptor(headers, settings, api, responseHeaders);
                body = interceptor.translate(originalBody);
            } else {
                // css, javascript or others
                body = originalBody;
            }
            wovnResponse.setContentLength(body.getBytes().length);
            wovnResponse.setCharacterEncoding("utf-8");
            PrintWriter out = response.getWriter();
            out.write(body);
            out.close();
        } else {
            // binary
            ServletOutputStream out = response.getOutputStream();
            out.write(wovnResponse.getData());
            out.close();
        }
    }
}
