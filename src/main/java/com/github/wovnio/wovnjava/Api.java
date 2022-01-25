package com.github.wovnio.wovnjava;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;
import com.github.cliftonlabs.json_simple.Jsoner;

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

    String translate(String lang, String html) throws ApiException, ApiNoPageDataException {
        this.responseHeaders.setApiStatus("Requested");
        HttpURLConnection con = null;
        try {
            URL url = getApiUrl(lang, html);
            WovnLogger.log(String.format("API url: %s", url.toString()));
            if (this.settings.outboundProxyHost== null) {
              con = (HttpURLConnection) url.openConnection();
            } else{
              Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.settings.outboundProxyHost, this.settings.outboundProxyPort));
              con = (HttpURLConnection) url.openConnection(proxy);
            }
            con.setConnectTimeout(settings.connectTimeout);
            con.setReadTimeout(settings.readTimeout);
            return translate(lang, html, con);
        } catch (UnsupportedEncodingException e) {
            WovnLogger.log("API error: unsupported encoding.");
            throw new ApiException("UnsupportedEncodingException", e.getMessage());
        } catch (MalformedURLException e) {
            WovnLogger.log("API error: malformed URL.");
            throw new ApiException("MalformedURLException", e.getMessage());
        } catch (IOException e) {
            WovnLogger.log("API error: IO exception.");
            throw new ApiException("IOException", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            WovnLogger.log("API error: no such algorithm exception.");
            throw new ApiException("NoSuchAlgorithmException", e.getMessage());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    String translate(String lang, String html, HttpURLConnection con) throws ApiException, ApiNoPageDataException {
        OutputStream out = null;
        try {
            con.setDoOutput(true);
            con.setRequestProperty("X-Request-Id", WovnLogger.getUUID());
            con.setRequestProperty("Accept-Encoding", "gzip");
            con.setRequestMethod("POST");

            Map<String, String> apiParams = getApiParameters(lang, html);
            String jsonEncodedBody = Jsoner.serialize(apiParams);
            byte[] apiBodyBytes = jsonEncodedBody.getBytes();

            if (this.settings.compressApiRequests) {
                ByteArrayOutputStream compressedBody = gzipStream(apiBodyBytes);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Content-Encoding", "gzip");
                con.setRequestProperty("Content-Length", String.valueOf(compressedBody.size()));
                out = con.getOutputStream();
                compressedBody.writeTo(out);
            } else {
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Content-Length", String.valueOf(apiBodyBytes.length));
                out = con.getOutputStream();
                out.write(apiBodyBytes);
            }

            out.close();
            out = null;
            this.responseHeaders.forwardFastlyHeaders(con);
            int status = con.getResponseCode();
            this.responseHeaders.setApiStatusCode(String.valueOf(status));
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream input = con.getInputStream();
                if ("gzip".equals(con.getContentEncoding())) {
                    input = new GZIPInputStream(input);
                }
                return extractHtml(input);
            } else if (status == 422) {
                throw new ApiNoPageDataException("ApiDataNotAvailable");
            }
            else {
                throw new ApiException("Failure", "Status code " + String.valueOf(status));
            }
        } catch (UnsupportedOperationException e) {
            throw new ApiException("UnsupportedOperationException", e.getMessage());
        } catch (SocketTimeoutException e) {
            throw new ApiException("SocketTimeoutException", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException("IOException:", e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    WovnLogger.log("Api close buffer error", e);
                }
            }
        }
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

    private Map<String, String> getApiParameters(String lang, String body) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("url", headers.getClientRequestUrlInDefaultLanguage());
        result.put("token", settings.projectToken);
        result.put("lang_code", lang);
        result.put("url_pattern", settings.urlPattern);
        result.put("site_prefix_path", settings.sitePrefixPath);
        result.put("lang_param_name", "wovn"); // TODO: add setting
        result.put("custom_lang_aliases", LanguageAliasSerializer.serializeToJson(settings.langCodeAliases));
        result.put("custom_domain_langs", CustomDomainLanguageSerializer.serializeToJson(settings.customDomainLanguages));
        result.put("product", "wovnjava");
        result.put("version", Settings.VERSION);
        result.put("debug_mode", String.valueOf(this.requestOptions.getDebugMode()));
        result.put("body", body);
        return result;
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
        return new URL(sb.toString());
    }

    private String hash(byte[] item) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(item);
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    private void appendValue(StringBuilder sb, String value) throws UnsupportedOperationException {
        sb.append(FormUrlEncoding.encodeValue(value));
    }
}
