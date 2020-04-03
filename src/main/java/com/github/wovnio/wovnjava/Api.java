package com.github.wovnio.wovnjava;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.LinkedHashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import net.arnx.jsonic.JSON;

class Api {
    private final int READ_BUFFER_SIZE = 8196;
    private final Settings settings;
    private final Headers headers;
    private final RequestOptions requestOptions;
    private final ResponseHeaders responseHeaders;
    private final String responseEncoding = "UTF-8"; // always response is UTF8

    Api(Settings settings, Headers headers, RequestOptions requestOptions, ResponseHeaders responseHeaders) {
        this.settings = settings;
        this.headers = headers;
        this.requestOptions = requestOptions;
        this.responseHeaders = responseHeaders;
    }

    String translate(String lang, String html) throws ApiException {
        this.responseHeaders.setApiStatus("Requested");
        HttpURLConnection con = null;
        try {
            this.responseHeaders.setApiStatus("ConnectStart");
            URL url = getApiUrl(lang, html);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(settings.connectTimeout);
            con.setReadTimeout(settings.readTimeout);
            this.responseHeaders.setApiStatus("Connected");
            return translate(lang, html, con);
        } catch (UnsupportedEncodingException e) {
            throw new ApiException("UnsupportedEncodingException", e.getMessage());
        } catch (MalformedURLException e) {
            throw new ApiException("MalformedURLException", e.getMessage());
        } catch (IOException e) {
            throw new ApiException("IOException", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException", e.getMessage());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    String translate(String lang, String html, HttpURLConnection con) throws ApiException {
        OutputStream out = null;
        try {
            this.responseHeaders.setApiStatus("RequestBodyPrepare");
            ByteArrayOutputStream body = gzipStream(getApiBody(lang, html).getBytes());
            con.setDoOutput(true);
            con.setRequestProperty("Accept-Encoding", "gzip");
            con.setRequestProperty("Content-Type", "application/octet-stream");
            con.setRequestProperty("Content-Length", String.valueOf(body.size()));
            con.setRequestMethod("POST");
            this.responseHeaders.setApiStatus("RequestBodyOutput");
            out = con.getOutputStream();
            body.writeTo(out);
            out.close();
            out = null;
            this.responseHeaders.setApiStatus("ResponseWaiting");
            this.responseHeaders.forwardFastlyHeaders(con);
            int status = con.getResponseCode();
            this.responseHeaders.setApiStatusCode(String.valueOf(status));
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream input = con.getInputStream();
                if ("gzip".equals(con.getContentEncoding())) {
                    input = new GZIPInputStream(input);
                }
                return extractHtml(input);
            } else {
                throw new ApiException("Failure", "Status code " + String.valueOf(status));
            }
        } catch (UnsupportedEncodingException e) {
            throw new ApiException("UnsupportedEncodingException", e.getMessage());
        } catch (SocketTimeoutException e) {
            throw new ApiException("SocketTimeoutException", e.getMessage());
        } catch (IOException e) {

            String body = null;
            try {
                InputStream errorStream = con.getErrorStream();
                if (errorStream != null) {
                    if ("gzip".equals(con.getContentEncoding())) {
                        errorStream = new GZIPInputStream(errorStream);
                    }
                    body = readToString(errorStream);
                }
            } catch (IOException errException) {
                System.err.println(errException);
            }
            if (body != null) {
                throw new ApiException("IOException", e.getMessage(), body);
            } else {
                throw new ApiException("IOException", e.getMessage());
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Logger.log.error("Api close buffer error", e);
                }
            }
        }
    }

    Boolean getDebugMode() {
        return this.requestOptions.getDebugMode();
    }

    private ByteArrayOutputStream gzipStream(byte[] input) throws IOException, UnsupportedEncodingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        GZIPOutputStream gz = new GZIPOutputStream(buffer);
        try {
            gz.write(input);
        } finally {
            gz.close();
        }
        return buffer;
    }

    private String readToString(InputStream input) throws IOException, UnsupportedEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        int len = 0;
        while ((len = input.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        input.close();

        return out.toString(responseEncoding);
    }

    private String extractHtml(InputStream input) throws ApiException, IOException, UnsupportedEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        int len = 0;
        while ((len = input.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        input.close();

        String json = out.toString(responseEncoding);
        LinkedHashMap<String, String> dict = JSON.decode(json);
        String html = dict.get("body");
        if (html == null) {
            throw new ApiException("ResponseFormatError", "Unknown JSON format");
        }
        return html;
    }

    private String getApiBody(String lang, String body) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        appendKeyValue(sb, "url=", headers.getClientRequestUrlInDefaultLanguage());
        appendKeyValue(sb, "&token=", settings.projectToken);
        appendKeyValue(sb, "&lang_code=", lang);
        appendKeyValue(sb, "&url_pattern=", settings.urlPattern);
        appendKeyValue(sb, "&site_prefix_path=", settings.sitePrefixPath);
        appendKeyValue(sb, "&custom_domain_langs=", CustomDomainLanguageSerializer.serializeToJson(settings.customDomainLanguages));
        appendKeyValue(sb, "&product=", "wovnjava");
        appendKeyValue(sb, "&version=", Settings.VERSION);
        appendKeyValue(sb, "&debug_mode=", String.valueOf(this.requestOptions.getDebugMode()));
        appendKeyValue(sb, "&body=", body);

        if (this.requestOptions.getDebugMode()) {
            Logger.log.info("ApiBody: " + sb.toString());
        }

        return sb.toString();
    }

    private URL getApiUrl(String lang, String body) throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException {
        StringBuilder sb = new StringBuilder();
        sb.append(settings.apiUrl);
        sb.append("translation?cache_key=");
        appendValue(sb, "(token=");
        appendValue(sb, settings.projectToken);
        appendValue(sb, "&settings_hash=");
        appendValue(sb, settings.hash());
        appendValue(sb, "&body_hash=");
        appendValue(sb, hash(body.getBytes()));
        appendValue(sb, "&path=");
        appendValue(sb, headers.getClientRequestPathInDefaultLanguage());
        appendValue(sb, "&lang=");
        appendValue(sb, lang);
        appendValue(sb, "&version=wovnjava_");
        appendValue(sb, Settings.VERSION);
        if (this.requestOptions.getCacheDisableMode() || this.requestOptions.getDebugMode()) {
            appendValue(sb, "&timestamp=");
            appendValue(sb, String.valueOf(System.currentTimeMillis()));
        }
        appendValue(sb, ")");

        if (this.requestOptions.getDebugMode()) {
            Logger.log.info("ApiUrl: " + sb.toString());
        }

        return new URL(sb.toString());
    }

    private String hash(byte[] item) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(item);
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    private void appendKeyValue(StringBuilder sb, String key, String value) throws UnsupportedEncodingException {
        sb.append(key);
        appendValue(sb, value);
    }

    private void appendValue(StringBuilder sb, String value) throws UnsupportedEncodingException {
        sb.append(URLEncoder.encode(value, "UTF-8"));
    }
}
