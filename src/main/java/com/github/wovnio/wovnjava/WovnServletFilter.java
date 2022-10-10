package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        RequestOptions requestOptions = new RequestOptions(this.settings, request);

        if (((HttpServletResponse)response).containsHeader("X-Wovn-Handler")) {
            isRequestAlreadyProcessed = true;
            WovnLogger.log("Request is already processed by WOVN.");
        } else {
            WovnLogger.clear();
            if (requestOptions.getDebugMode()) {
                WovnLogger.log(Diagnostics.getDiagnosticInfo());
            }
            ((HttpServletResponse)response).setHeader("X-Wovn-Handler", "wovnjava_" + Settings.VERSION);
        }
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
                String newPath = headers.getCurrentContextUrlInDefaultLanguage().getPath();
                WovnLogger.log("Forwarding to " + newPath);
                wovnRequest.getRequestDispatcher(newPath).forward(wovnRequest, response);
            }
        }
    }

    @Override
    public void destroy() {
    }

    private void tryTranslate(Headers headers, RequestOptions requestOptions, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(request, headers);

        String overrideEncoding = requestOptions.getEncodingOverride();
        String responseEncoding = overrideEncoding != null ? overrideEncoding : this.settings.encoding;
        WovnHttpServletResponse wovnResponse = new WovnHttpServletResponse(response, headers, new Utf8(responseEncoding));

        ResponseHeaders responseHeaders = new ResponseHeaders(response);
        responseHeaders.setApiStatus("Unused");

        if (headers.getIsPathInDefaultLanguage()) {
            chain.doFilter(wovnRequest, wovnResponse);
        } else {
            String newPath = headers.getCurrentContextUrlInDefaultLanguage().getPath();
            WovnLogger.log("Forwarding to" + newPath);
            wovnRequest.getRequestDispatcher(newPath).forward(wovnRequest, wovnResponse);
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


                if (requestOptions.getDebugMode()) {
                    body += WovnLogger.getRequestLogsHtmlComment();
                }
            } else {
                // css, javascript or others
                body = originalBody;
            }

            wovnResponse.setCharacterEncoding("UTF-8");
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
