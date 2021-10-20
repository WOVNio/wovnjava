package com.github.wovnio.wovnjava;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;


public class ApiTest extends TestCase {

    public void testTranslateWithGzipResponse() throws ApiException, IOException, ProtocolException, ConfigurationError, ApiNoPageDataException {
        byte[] apiServerResponse = gzip("{\"body\": \"<html><body>response html</body></html>\"}".getBytes());
        String encoding = "gzip";
        String resultingHtml = testTranslate(apiServerResponse, encoding, 200);
        String expectedHtml = "<html><body>response html</body></html>";
        assertEquals(expectedHtml, resultingHtml);
    }

    public void testTranslateWithPlainTextResponse() throws ApiException, IOException, ProtocolException, ConfigurationError, ApiNoPageDataException {
        byte[] apiServerResponse = "{\"body\": \"<html><body>response html</body></html>\"}".getBytes();
        String encoding = "";
        String resultingHtml = testTranslate(apiServerResponse, encoding, 200);
        String expectedHtml = "<html><body>response html</body></html>";
        assertEquals(expectedHtml, resultingHtml);
    }

    private static String testTranslate(byte[] apiServerResponse, String encoding, int statusCode) throws ApiException, IOException, ProtocolException, ConfigurationError, ApiNoPageDataException {
        String html = "<html>much content</html>";

        Settings settings = TestUtil.makeSettings(new HashMap<String, String>() {{
            put("projectToken", "token0");
            put("defaultLang", "en");
            put("supportedLangs", "en,ja,fr");
            put("urlPattern", "path");
        }});
        UrlLanguagePatternHandler urlLanguagePatternHandler = UrlLanguagePatternHandlerFactory.create(settings);

        HttpServletRequest request = MockHttpServletRequest.create("https://example.com/ja/somepage/");
        ResponseHeaders responseHeaders = mockResponseHeaders();

        Headers headers = new Headers(request, settings, urlLanguagePatternHandler);
        RequestOptions requestOptions = new RequestOptions(settings, request);

        Api api = new Api(settings, headers, requestOptions, responseHeaders);

        ByteArrayOutputStream requestStream = new ByteArrayOutputStream();
        ByteArrayInputStream responseStream = new ByteArrayInputStream(apiServerResponse);

        HttpURLConnection con = mockHttpURLConnection(requestStream, responseStream, statusCode, encoding);

        String result = api.translate("ja", html, con);

        String encodedApiRequestBody = decompress(requestStream.toByteArray());
        String apiRequestBody = URLDecoder.decode(encodedApiRequestBody, "UTF-8");
        String expectedRequestBody = "url=https://example.com/somepage/" +
                                     "&token=token0" +
                                     "&lang_code=ja" +
                                     "&url_pattern=path" +
                                     "&site_prefix_path=" +
                                     "&lang_param_name=wovn" +
                                     "&custom_lang_aliases={}" +
                                     "&custom_domain_langs=" +
                                     "&product=wovnjava" +
                                     "&version=" + Settings.VERSION +
                                     "&debug_mode=false" +
                                     "&body=" + html;
        assertEquals(expectedRequestBody, apiRequestBody);

        return result;
    }

    private byte[] gzip(byte[] input) throws IOException, ProtocolException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(input.length);
        GZIPOutputStream gz = new GZIPOutputStream(buffer);
        try {
            gz.write(input);
        } finally {
            gz.close();
        }
        return buffer.toByteArray();
    }

    private static HttpURLConnection mockHttpURLConnection(ByteArrayOutputStream requestStream, ByteArrayInputStream responseStream, int statusCode, String encoding) throws IOException, ProtocolException {
        HttpURLConnection mock = EasyMock.createMock(HttpURLConnection.class);
        mock.setDoOutput(true);
        mock.setRequestProperty(EasyMock.anyString(), EasyMock.anyString());
        EasyMock.expectLastCall().atLeastOnce();
        mock.setRequestMethod("POST");
        EasyMock.expect(mock.getResponseCode()).andReturn(statusCode);
        EasyMock.expect(mock.getContentEncoding()).andReturn(encoding);
        EasyMock.expect(mock.getOutputStream()).andReturn(requestStream);
        EasyMock.expect(mock.getInputStream()).andReturn(responseStream);
        EasyMock.replay(mock);
        return mock;
    }

    private static String decompress(byte[] compressed) throws IOException {
        StringBuilder sb = new StringBuilder();
        ByteArrayInputStream bis = null;
        GZIPInputStream gis = null;
        BufferedReader br = null;
        try {
            bis = new ByteArrayInputStream(compressed);
            gis = new GZIPInputStream(bis);
            br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            gis.close();
            bis.close();
            br.close();
        }
        return sb.toString();
    }

    private static ResponseHeaders mockResponseHeaders() {
        ResponseHeaders mock = EasyMock.createMock(ResponseHeaders.class);
        mock.forwardFastlyHeaders(EasyMock.anyObject(HttpURLConnection.class));
        EasyMock.expectLastCall().times(1);
        mock.setApiStatusCode("200");
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(mock);
        return mock;
    }
}
