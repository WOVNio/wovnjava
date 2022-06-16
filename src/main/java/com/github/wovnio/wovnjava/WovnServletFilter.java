package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WovnServletFilter implements Filter {
    private Settings settings;
    private UrlLanguagePatternHandler urlLanguagePatternHandler;
    private FileExtensionMatcher fileExtensionMatcher;
    private final HtmlChecker htmlChecker = new HtmlChecker();

    public static final String VERSION = Settings.VERSION;  // for backward compatibility

    @Override
    public void init(FilterConfig config) throws ServletException {
        try {
            this.settings = new Settings(config);
            this.urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);
            this.fileExtensionMatcher = new FileExtensionMatcher();
        } catch (ConfigurationError e) {
            throw new ServletException("WovnServletFilter ConfigurationError: " + e.getMessage() + " (See WovnServletFilter instructions at https://github.com/WOVNio/wovnjava)");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        WovnLogger.setUUID(UUID.randomUUID().toString());
        boolean isRequestAlreadyProcessed = false;
        if (((HttpServletResponse)response).containsHeader("X-Wovn-Handler")) {
            isRequestAlreadyProcessed = true;
            WovnLogger.log("Request is already processed by WOVN.");
        } else {
            ((HttpServletResponse)response).setHeader("X-Wovn-Handler", "wovnjava_" + Settings.VERSION);
        }

        RequestOptions requestOptions = new RequestOptions(this.settings, request);
        Headers headers = new Headers((HttpServletRequest)request, this.settings, this.urlLanguagePatternHandler);

        boolean canTranslateRequest = !requestOptions.getDisableMode() &&
                                      !this.fileExtensionMatcher.isFile(headers.getCurrentContextUrlInDefaultLanguage().getPath());

        if (isRequestAlreadyProcessed || !headers.getIsValidRequest()) {
            /* Do nothing */
            chain.doFilter(request, response);
        } else if (headers.getShouldRedirectExplicitDefaultLangUrl()) {
            /* Send HTTP 302 redirect to equivalent URL without default language code */
            ((HttpServletResponse) response).sendRedirect(headers.getClientRequestUrlInDefaultLanguage());
        } else if (canTranslateRequest) {
            /* Strip language code, pass on request, and attempt to translate the resulting response */
            WovnLogger.log("Content can be translated.");
            tryTranslate(headers, requestOptions, (HttpServletRequest)request, (HttpServletResponse)response, chain);
        } else {
            /* Strip language code and pass through the request and response untouched */
            WovnLogger.log("Content cannot be translated.");
            WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest((HttpServletRequest)request, headers);
            if (headers.getIsPathInDefaultLanguage()) {
                chain.doFilter(wovnRequest, response);
            } else {
                wovnRequest.getRequestDispatcher(headers.getCurrentContextUrlInDefaultLanguage().getPath()).forward(wovnRequest, response);
            }
        }
    }

    @Override
    public void destroy() {
    }

    private void tryTranslate(Headers headers, RequestOptions requestOptions, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(request, headers);
        WovnHttpServletResponse wovnResponse = new WovnHttpServletResponse(response, headers, new Utf8(this.settings.encoding));

        ResponseHeaders responseHeaders = new ResponseHeaders(response);
        responseHeaders.setApiStatus("Unused");

        if (headers.getIsPathInDefaultLanguage()) {
            chain.doFilter(wovnRequest, wovnResponse);
        } else {
            wovnRequest.getRequestDispatcher(headers.getCurrentContextUrlInDefaultLanguage().getPath()).forward(wovnRequest, wovnResponse);
        }

        if (htmlChecker.isTextFileContentType(response.getContentType())) {
            // text
            String originalBody = wovnResponse.toString();
            String body = null;
            if (htmlChecker.canTranslate(response, originalBody)) {
                // html
                Api api = new Api(settings, headers, requestOptions, responseHeaders);
                Interceptor interceptor = new Interceptor(headers, settings, api, responseHeaders);
                body = interceptor.translate(originalBody);
            } else {
                // css, javascript or others
                body = originalBody;
            }
            // wovnResponse.setContentLength(body.getBytes().length);
            WovnLogger.log("Response content-length:" + String.valueOf(body.getBytes().length));
            if (body.length() >= 50) {
                WovnLogger.log("Last 50 characters of response:" + body.substring(body.length() - 50));
            }
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
