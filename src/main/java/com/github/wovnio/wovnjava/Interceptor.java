package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.IDN;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import nu.validator.htmlparser.dom.*;

class Interceptor {
    private Store store;

    Interceptor(FilterConfig config) {
        store = new Store(config);
        Logger.debugMode = store.settings.debugMode;
    }

    void call(HttpServletRequest request, ServletResponse response, FilterChain chain) {

        if (!store.settings.isValid()) {
            try {
                chain.doFilter(request, response);
            } catch (ServletException e) {
                Logger.log.error("ServletException in chain.doFilter (invalid settings)", e);
            } catch (IOException e) {
                Logger.log.error("IOException in chain.doFilter (invalid settings)", e);
            }
            return;
        }

        Headers h = new Headers(request, store.settings);
        if (store.settings.testMode && !store.settings.testUrl.equals(h.url)) {

            if (Logger.isDebug()) {
                Logger.log.info("test mode: " + h.url);
            }

            try {
                chain.doFilter(request, response);
            } catch (ServletException e) {
                Logger.log.error("ServletException in chain.doFilter (test mode)", e);
            } catch (IOException e) {
                Logger.log.error("IOException in chain.doFilter (test mode)", e);
            }
            return;
        }

        if (h.getPathLang().equals(store.settings.defaultLang)) {
            String redirectLocation = h.redirectLocation(store.settings.defaultLang);

            if (Logger.isDebug()) {
                Logger.log.info("Redirect to \"" + redirectLocation + "\"");
            }

            try {
                ((HttpServletResponse) response).sendRedirect(redirectLocation);
            } catch (IOException e) {
                Logger.log.error("IOException in response.sendRedirect", e);

            }
            return;
        }

        WovnHttpServletRequest wovnRequest = new WovnHttpServletRequest(
                request, h
        );

        WovnHttpServletResponse wovnResponse = new WovnHttpServletResponse(
                (HttpServletResponse)response, h
        );

        try {
            chain.doFilter(wovnRequest, wovnResponse);
        } catch (ServletException e) {
            Logger.log.error("ServletExecption in chain.doFilter", e);
        } catch (IOException e) {
            Logger.log.error("ServletException in chain.doFilter", e);
        }

        String body = null;
        // There is a possibility that response.getContentType() is null when the response is an image.
        if (response.getContentType() != null && Pattern.compile("html").matcher(response.getContentType()).find()) {

            String status = String.valueOf(wovnResponse.status);

            if (!Pattern.compile("^1|302").matcher(status).find()) {

                body = wovnResponse.toString();

                if (!this.store.settings.strictHtmlCheck || isHtml(body)) {

                    if (Logger.isDebug()) {
                        if (wovnRequest.getQueryString() == null || wovnRequest.getQueryString().isEmpty()) {
                            Logger.log.info("Translating HTML: " + wovnRequest.getRequestURL());
                        } else {
                            Logger.log.info("Translating HTML: " + wovnRequest.getRequestURL() + "?" + wovnRequest.getQueryString());
                        }
                    }

                    String lang = h.langCode();
                    HashMap<String,String> url = new HashMap<String,String>();
                    url.put("protocol", h.protocol);
                    url.put("host", h.host);
                    url.put("pathname", h.pathName);

                    Values values = store.getValues(h.pageUrl);

                    body = this.switchLang(body, values, url, lang, h);
                }
                wovnResponse.setContentLength(body.getBytes().length);
            }
        }

        if (body != null) {
            // text
            wovnResponse.setContentType("text/html; charset=utf-8");
            try {
                PrintWriter out = response.getWriter();
                out.write(body);
                out.close();
            } catch (IOException e) {
                Logger.log.error("IOException while writing text data", e);
            }
        } else {
            // binary
            try {
                ServletOutputStream out = response.getOutputStream();
                out.write(wovnResponse.getData());
                out.close();
            } catch (IOException e) {
                Logger.log.error("IOException while writing binary data", e);
            }
        }

        h.out(request, wovnResponse);
    }

    static boolean isHtml(String body) {
        if (Logger.isDebug()) {
            Logger.log.info("Checking HTML strictly.");

            if (Logger.debugMode > 1) {
                Logger.log.info("original HTML:\n" + body);
            }
        }

        // Remove comments.
        body = Pattern.compile("(?m)\\A(\\s*<!--[\\s\\S]*?-->\\s*)+").matcher(body).replaceAll("");

        // Remove spaces.
        body = Pattern.compile("(?m)\\A\\s+").matcher(body).replaceAll("");

        if (Logger.debugMode > 1) {
            Logger.log.info("HTML after removing comment tags and spaces:\n" + body);
        }

        if (Pattern.compile("(?m)\\A<\\?xml\\b", Pattern.CASE_INSENSITIVE).matcher(body).find()             // <?xml
                || Pattern.compile("(?m)\\A<!DOCTYPE\\b", Pattern.CASE_INSENSITIVE).matcher(body).find()    // <!DOCTYPE
                || Pattern.compile("(?m)\\A<html\\b", Pattern.CASE_INSENSITIVE).matcher(body).find()        // <html
                ) {
            if (Logger.isDebug()) {
                Logger.log.info("This data is HTML.");
            }
            return true;
        } else {
            if (Logger.isDebug()) {
                Logger.log.info("This data is not HTML.");
            }
            return false;
        }
    }

    private String addLangCode(String href, String pattern, String lang, Headers headers) {
        if (href != null && href.length() > 0 && Pattern.compile("^(#.*)?$").matcher(href).find()) {
            return href;
        }

        String newHref = href;

        if ( href != null
                && href.length() > 0
                && Pattern.compile("^(https?:)?//", Pattern.CASE_INSENSITIVE).matcher(href).find()
                ) {

            URL uri;
            try {
                uri = new URL(href);
            } catch (MalformedURLException e) {
                Logger.log.error("MalformedURLException in addLangCode", e);
                return newHref;
            }
            if (uri.getHost().toLowerCase().equals(headers.host.toLowerCase())) {
                if (pattern.equals("subdomain")) {
                    Matcher m = Pattern.compile("//([^\\.]*)\\.").matcher(href);
                    String subCode = Lang.getCode(m.group(1));
                    if (subCode != null
                            && subCode.length() > 0
                            && subCode.toLowerCase().equals(lang.toLowerCase())
                            ) {
                        newHref = Pattern.compile(lang, Pattern.CASE_INSENSITIVE)
                                .matcher(href)
                                .replaceFirst(lang.toLowerCase());
                    } else {
                        newHref = href.replaceFirst("(//)([^\\.]*)", "$1" + lang.toLowerCase() + ".$2");
                    }
                } else if (pattern.equals("query")) {
                    if (Pattern.compile("\\?").matcher(href).find()) {
                        newHref = href + "&wovn=" + lang;
                    } else {
                        newHref = href + "?wovn=" + lang;
                    }
                } else {
                    newHref = addLangToPath(href, lang, headers.getPathLang());
                }
            }
        } else if (href != null && href.length() > 0) {
            String currentDir;
            if (pattern.equals("subdomain")) {
                String langUrl = headers.protocol + "://" + lang.toLowerCase() + headers.host;
                currentDir = headers.pathName.replaceFirst("[^/]*\\.[^\\.]{2,6}$", "");
                if (Pattern.compile("^\\.\\..*$").matcher(href).find()) {
                    newHref = langUrl + "/" + href.replaceAll("^\\.\\./", "");
                } else if (Pattern.compile("^\\..*$").matcher(href).find()) {
                    newHref = langUrl + currentDir + "/" + href.replaceAll("^\\./", "");
                } else if (Pattern.compile("^/.*$").matcher(href).find()) {
                    newHref = langUrl + href;
                } else {
                    newHref = langUrl + currentDir + "/" + href;
                }
            } else if (pattern.equals("query")) {
                if (Pattern.compile("\\?").matcher(href).find()) {
                    newHref = href + "&wovn=" + lang;
                } else {
                    newHref = href + "?wovn=" + lang;
                }
            } else {
                if (Pattern.compile("^/").matcher(href).find()) {
                    newHref = addLangToPath(href, lang, headers.getPathLang());
                } else {
                    currentDir = headers.pathNameKeepTrailingSlash.replaceFirst("[^/]+$", "");
                    newHref = addLangToPath(currentDir + href, lang, headers.getPathLang());
                }
            }
        }

        return newHref;
    }

    private String addLangToPath(String path, String lang, String pathLang) {
        String prefix = this.store.settings.sitePrefixPathWithSlash;
        String newPath = prefix + lang + "/";
        return path.replaceFirst("^" + prefix, newPath);
    }

    private boolean checkWovnIgnore(Node node) {
        if (node.getAttributes() != null && node.getAttributes().getNamedItem("wovn-ignore") != null) {
            return true;
        } else if (node.getParentNode() == null) {
            return false;
        }
        return this.checkWovnIgnore(node.getParentNode());
    }

    private static String getStringFromDocument(String body, Document doc, Settings settings)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            transformer.transform(domSource, result);
            String html = writer.toString();
            html = DoctypeDetectation.addDoctypeIfNotExists(body, html);
            if (settings.deleteInvalidClosingTag) {
                html = FixHtml.deleteClosingTagIfNeed(html);
            }
            return html;
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    private String switchLang(String body, Values values, HashMap<String, String> url, String lang, Headers headers) {

        lang = Lang.getCode(lang);

        HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
        StringReader reader = new StringReader(body);

        Document doc;
        try {
            doc = builder.parse(new InputSource(reader));
        } catch (org.xml.sax.SAXException e) {
            Logger.log.error("SAXException while parsing HTML", e);
            return body;
        } catch (IOException e) {
            Logger.log.error("IOException while parsing HTML", e);
            return body;
        }

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        if (doc.getDocumentElement().hasAttribute("wovn-ignore")) {
            return getStringFromDocument(body, doc, this.store.settings);
        }

        changeUrlToPunyCode(doc);

        if (!lang.equals(this.store.settings.defaultLang)) {
            NodeList anchors = null;
            try {
                anchors = (NodeList)xpath.evaluate("//*[local-name()='a']", doc, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                // No error occurs.
            }
            for (int i = 0; i < anchors.getLength(); i++) {
                Node anchor = anchors.item(i);
                if (this.checkWovnIgnore(anchor)) {
                    continue;
                }
                Node href = anchor.getAttributes().getNamedItem("href");
                if (href != null) {
                    String newHref = this.addLangCode(href.getNodeValue(), this.store.settings.urlPattern, lang, headers);
                    href.setNodeValue(newHref);
                }
            }
        }

        NodeList texts = null;
        try {
            texts = (NodeList)xpath.evaluate("//text()", doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            // No error occurs.
        }
        for (int i = 0; i < texts.getLength(); i++) {
            Node text = texts.item(i);
            if (this.checkWovnIgnore(text)) {
                continue;
            }
            String nodeText = text.getTextContent();
            nodeText = Pattern.compile("^\\s+|\\s+$").matcher(nodeText).replaceAll("");
            String destText = values.getText(nodeText, lang);
            if (destText != null) {
                String newText = Pattern.compile("^(\\s*)[\\S\\s]*(\\s*)$")
                        .matcher(text.getTextContent())
                        .replaceAll("$1" + destText + "$2");
                text.setTextContent(newText);
            }
        }

        Pattern p = Pattern.compile("^(description|title|og:title|og:description|twitter:title|twitter:description)$");
        NodeList metas = null;
        try {
            metas = (NodeList)xpath.evaluate("//*[local-name()='meta']", doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            // No error occurs.
        }
        for (int i = 0; i < metas.getLength(); i++) {
            Node meta = metas.item(i);
            if (this.checkWovnIgnore(meta)) {
                continue;
            }

            Node name = meta.getAttributes().getNamedItem("name");
            Node property = meta.getAttributes().getNamedItem("property");
            if (!(name != null && p.matcher(name.getNodeValue()).find())
                    && !(property != null && p.matcher(property.getNodeValue()).find())
                    ) {
                continue;
            }

            Node content = meta.getAttributes().getNamedItem("content");
            if (content == null || content.getNodeValue().length() == 0) {
                continue;
            }
            String nodeContent = Pattern.compile("^\\s+|\\s+$").matcher(content.getNodeValue()).replaceAll("");
            if (nodeContent.length() == 0) {
                continue;
            }
            String destContent = values.getText(nodeContent, lang);
            if (destContent != null) {
                String newContent = Pattern.compile("^(\\s*)[\\S\\s]*(\\s*)$")
                        .matcher(nodeContent)
                        .replaceAll("$1" + destContent + "$2");
                content.setNodeValue(newContent);
            }
        }

        NodeList imgs = null;
        try {
            imgs = (NodeList)xpath.evaluate("//*[local-name()='img']", doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            // No erorr occurs.
        }
        for (int i = 0; i < imgs.getLength(); i++) {
            Node img = imgs.item(i);
            if (img == null || this.checkWovnIgnore(img)) {
                continue;
            }
            Element imgElement = (Element)img;
            String src = imgElement.getAttribute("src");
            if (!Pattern.compile("://").matcher(src).find()) {
                if (Pattern.compile("^/").matcher(src).find()) {
                    src = url.get("protocol") + "://" + url.get("host") + src;
                } else {
                    src = url.get("protocol") + "://" + url.get("host") + url.get("path") + src;
                }
            }
            String destSrc = values.getImage(src, lang);
            if (destSrc != null) {
                img.getAttributes().getNamedItem("src").setNodeValue(destSrc);
            }

            if (img.getAttributes().getNamedItem("alt") != null) {
                String alt = img.getAttributes().getNamedItem("alt").getNodeValue();
                alt = Pattern.compile("^\\s+|\\s+$").matcher(alt).replaceAll("");
                String destAlt = values.getText(alt, lang);
                if (destAlt != null) {
                    String newAlt = Pattern.compile("^(\\s*)[\\S\\s]*(\\s*)$")
                            .matcher(alt)
                            .replaceAll("$1" + destAlt + "$2");
                    img.getAttributes().getNamedItem("alt").setNodeValue(newAlt);
                }
            }
        }

        NodeList scripts = null;
        try {
            scripts = (NodeList)xpath.evaluate("//*[local-name()='script']", doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            // No error occurs.
        }
        for (int i = 0; i < scripts.getLength(); i++) {
            Element src = (Element)scripts.item(i);
            if (src != null
                    && Pattern.compile("//j\\.(dev-)?wovn\\.io(:3000)?/").matcher(src.getAttribute("src")).find()) {
                src.getParentNode().removeChild(src);
            }
        }

        Node parentNode = null;
        try {
            NodeList heads = doc.getElementsByTagName("head");
            if (heads != null && heads.getLength() > 0) {
                parentNode = heads.item(0);
            }
        } catch (NullPointerException e) {
            Logger.log.error("NullPointerException while searching <head> tag", e);
        }
        if (parentNode == null) {
            try {
                NodeList bodies = doc.getElementsByTagName("body");
                if (bodies != null && bodies.getLength() > 0) {
                    parentNode = bodies.item(0);
                }
            } catch (NullPointerException e) {
                Logger.log.error("NullPointerException while searching <body> tag", e);
            }
        }
        if (parentNode == null) {
            parentNode = doc;
        }

        Element insertNode = doc.createElement("script");
        insertNode.setAttribute("src", "//j.wovn.io/1");
        insertNode.setAttribute("async", "true");
        String version = WovnServletFilter.VERSION;
        String sitePrefixPath = "";
        if (this.store.settings.hasSitePrefixPath) {
            sitePrefixPath = "&site_prefix_path=" + this.store.settings.sitePrefixPathWithoutSlash;
        }
        insertNode.setAttribute(
                "data-wovnio",
                "key=" + this.store.settings.projectToken + "&backend=true&currentLang=" + lang
                        + "&defaultLang=" + this.store.settings.defaultLang
                        + "&urlPattern=" + this.store.settings.urlPattern + "&version=" + version
                        + sitePrefixPath
        );
        insertNode.setTextContent(" ");
        parentNode.insertBefore(insertNode, parentNode.getFirstChild());

        for (String l : values.getLangs()) {
            insertNode = doc.createElement("link");
            insertNode.setAttribute("ref", "altername");
            insertNode.setAttribute("hreflang", Lang.normalizeIso639_1(l));
            insertNode.setAttribute("href", headers.redirectLocation(l));
            parentNode.appendChild(insertNode);
        }

        doc.getDocumentElement().setAttribute("lang", lang);

        return getStringFromDocument(body, doc, this.store.settings);
    }

    private final Pattern extractDomain = Pattern.compile("(?:https?:)?//([^/]+)");

    private void changeUrlToPunyCode(Document doc) {
        changeUrlToPunyCodeAttr(doc, "link", "href");
        changeUrlToPunyCodeAttr(doc, "a", "href");
        changeUrlToPunyCodeAttr(doc, "script", "src");
        changeUrlToPunyCodeAttr(doc, "img", "src");
        changeUrlToPunyCodeAttr(doc, "input", "src");
        changeUrlToPunyCodeAttr(doc, "iframe", "src");
        changeUrlToPunyCodeAttr(doc, "meta", "content");
    }

    private void changeUrlToPunyCodeAttr(Document doc, String tag, String attribute) {
        NodeList nodes = doc.getElementsByTagName(tag);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node == null) {
                continue;
            }
            NamedNodeMap attrs = node.getAttributes();
            if (attrs == null) {
                continue;
            }
            Node attr = attrs.getNamedItem(attribute);
            if (attr == null) {
                continue;
            }
            String value = attr.getNodeValue();
            if (value == null) {
                continue;
            }
            if (!canReplace(attrs, tag, attribute)) {
                continue;
            }
            Matcher matcher = extractDomain.matcher(value);
            if (matcher.find()) {
                String host = matcher.group(1);
                String punyCode = IDN.toASCII(host);
                attr.setNodeValue(value.replaceFirst(host, punyCode));
            }
        }
    }

    private boolean canReplace(NamedNodeMap attrs, String tag, String attribute) {
        if (tag == "meta" && attribute == "content") {
            Node attr = attrs.getNamedItem("property"); // property for Open Graph data
            if (attr == null) {
                attr = attrs.getNamedItem("name"); // name for Twitter Card data
                if (attr == null) {
                    return false;
                }
            }
            String value = attr.getNodeValue();
            if (value == null) {
                return false;
            }
            return value.indexOf(":url") >= 0 ||
                value.indexOf(":image") >= 0;
        }
        return true;
    }
}
