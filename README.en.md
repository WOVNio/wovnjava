# WOVN.io Java Library

The WOVN.io Java library is a backend library that uses WOVN.io in order to provide translations. The WOVN.io Java library is packaged as a Servlet Filter.

This document explains the WOVN.io Java Library's install process and parameter settings.

## 1. Install Procedure

### 1.1. Create a WOVN.io account

In order to use the WOVN.io Java Library, you need a WOVN.io account.
If you do not have a WOVN.io account, first please sign up at [WOVN.io](https://wovn.io).

1.2. Adding a page to translate

Sign into [WOVN.io](https://wovn.io), and add a page you would like translated.

### 1.3. Java Application Settings

#### 1.3.1. If you're using Maven

* If you're not using Maven, please refer to https://jitpack.io/#wovnio/wovnjava.

##### 1.3.1.1. To use this library within your application, you must add the JitPack repository to your application's pom.xml file.

```XML
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
    </snapshots>
  </repository>
</repositories>
```

##### 1.3.1.2. Add the WOVN.io library as a dependency to your project's pom.xml.

```XML
<dependency>
  <groupId>com.github.wovnio</groupId>
  <artifactId>wovnjava</artifactId>
  <version>x.x.x</version>
</dependency>
```
Contact Wovn's support for more information about the latest version of wovnjava.
You can see all available versions of wovnjava [here](https://jitpack.io/#wovnio/wovnjava).

##### 1.3.1.3. Add the wovnjava library's settings to your servlet's web.xml.

In your `web.xml`, configure parameters for the WovnServletFilter.

Four parameters are required:
1) projectToken
2) defaultLang
3) supportedLangs
4) urlPattern

It is important that these parameters match your Wovn project. Details about the parameters are found in Section 2.

An example minimum WovnServletFilter configuration looks as follows. This is for a project with token "123abc", original language Japanese, and target translated language English.
```XML
<filter>
  <filter-name>wovn</filter-name>
  <filter-class>com.github.wovnio.wovnjava.WovnServletFilter</filter-class>
  <init-param>
    <param-name>projectToken</param-name>
    <param-value>123abc</param-value>
  </init-param>
  <init-param>
    <param-name>defaultLang</param-name>
    <param-value>ja</param-value>
  </init-param>
  <init-param>
    <param-name>supportedLangs</param-name>
    <param-value>ja,en</param-value>
  </init-param>
  <init-param>
    <param-name>urlPattern</param-name>
    <param-value>path</param-value>
  </init-param>
</filter>

<filter-mapping>
  <filter-name>wovn</filter-name>
  <url-pattern>/*</url-pattern>
  <dispatcher>REQUEST</dispatcher>
  <dispatcher>FORWARD</dispatcher>
</filter-mapping>
```

The recommended `filter-mapping` is to allow `REQUEST` and `FORWARD` for dispatchers, and allow all paths in `url-pattern`. If only a specific directory on your web server should be intercepted by WovnServletFilter, configure `url-pattern` accordingly.

_Note If you have set up your own filters in your web.xml to handle URL rewriting and internal redirects, add the WovnJava settings under your own settings._

## 2. Parameter Settings

The following parameters can be set within the WOVN.io Java Library.

Parameter Name            | Required | Default Value
------------------------- | -------- | ------------
projectToken              | yes      |
defaultLang               | yes      |
supportedLangs            | yes      |
urlPattern                | yes      |
useProxy                  |          | false
outboundProxyPort         |          |
outboundProxyHost         |          |
originalUrlHeader         |          |
originalQueryStringHeader |          |
ignoreClasses             |          |
enableFlushBuffer         |          | false
sitePrefixPath            |          |
langCodeAliases           |          |
customDomainLangs         |          |
debugMode                 |          | false
encoding                  |          | 

### 2.1. projectToken (required)

Your WOVN.io account's project token.

### 2.2. defaultLang (required)

The web server content's original language.

defaultLang is declared as a two-character language code.
See a list of language codes at the bottom of this document.

### 2.3. supportedLangs (required)

A list of all languages to use for this project. (defaultLang will automatically be included in this list.)

supportedLangs is declared as a comma-separated list of language codes.
See a list of language codes at the bottom of this document.

### 2.4. urlPattern (required)

The pattern of URLs that point to pages in translated languages.

WovnServletFilter accepts request URLs with a declared translated language.
This setting specifies how those URLs should look.

Three basic URL pattern types are available, plus a highly customizable Custom Domain option.

The examples below are for the original URL `https://wovn.io/contact`, when visiting the page in korean (as a translated language).

url pattern type | Translated page's URL           | Description
---------------- | ------------------------------- | ------
"path"           | https://wovn.io/ko/contact      | Language code is inserted as the first section of the path
"subdomain"      | https://ko.wovn.io/contact      | Language code is inserted as the first subdomain. (The server's DNS settings must be configured for this option.)
"query"          | https://wovn.io/contact?wovn=ko | Language code is inserted as a query parameter. (This option requires the least amount of changes to the application.)
"customDomain"   | (see section 2.10 below)        | The custom domain option lets you define domain and path for each language independently.

### 2.5. useProxy

A flag to set if the Java web server is behind a reverse proxy.

If the request received by WovnServletFilter does not have a host name that matches the Wovn project, the page's translation data may not be accessible. This may happen if the request is routed through a reverse proxy.

If you set useProxy to true, WovnServletFilter will use HTTP Request Headers `X-Forwarded-Host` and `X-Forwarded-Port` for determining hostname and port of the request.

Set useProxy to true as follows
```xml
<init-param>
  <param-name>useProxy</param-name>
  <param-value>true</param-value>
</init-param>
```

Note that if the reverse proxy may also rewrite the request path or query, configuring the originalUrlHeader and/or originalQueryStringHeader may also be necessary.

### 2.6. outboundProxyHost, outboundProxyPort

WovnServletFilter needs to send requests to Wovn's translation API server. This setting should be used if your server requires all outbound requests to be routed through a proxy. Both ``outboundProxyHost`` (host or IP address of the proxy) and ``outboundProxyPort`` (port number of the proxy) are required to be set. 

```xml
<init-param>
  <param-name>outboundProxyHost</param-name>
  <param-value>proxy.company.com</param-value>
</init-param>
<init-param>
  <param-name>outboundProxyPort</param-name>
  <param-value>8080</param-value>
</init-param>
```


### 2.7. originalUrlHeader, originalQueryStringHeader

Name of HTTP headers for declaring the original request path and query.

If the incoming request has been rewritten, for example using the Apache HTTP Server's mod\_rewrite module, WovnServletFilter may not be able to see the original request URL. In this case, it may be unable to retrieve the correct translation data from the API server.

If originalUrlHeader and originalQueryStringHeader are set, WovnServletFilter will inspect these HTTP header names to determine the original path and query. originalUrlHeader should declare the HTTP header name for original path. originalQueryStringHeader should declare the HTTP header name for original query string.

If these parameters are set, useProxy should probably be set to `true`.

#### Example configuration
This example uses HTTP header names `X-Request-Uri` and `X-Query-String` to declare path and query.

Configure Apache to store the incoming request path and query in HTTP headers.
```apache
SetEnvIf Request_URI "^(.*)$" REQUEST_URI=$1
RequestHeader set X-Request-Uri "%{REQUEST_URI}e"
RewriteRule .* - [E=REQUEST_QUERY_STRING:%{QUERY_STRING}]
RequestHeader set X-Query-String "%{REQUEST_QUERY_STRING}e"
```

Configure WovnServletFilter to determine request path and query from the same HTTP headers.
```XML
<init-param>
  <param-name>originalUrlHeader</param-name>
  <param-value>X-Request-Uri</param-value>
</init-param>
<init-param>
  <param-name>originalQueryStringHeader</param-name>
  <param-value>X-Query-String</param-value>
</init-param>
```
_The sample request header shown above was referenced from the following site:_
https://coderwall.com/p/jhkw7w/passing-request-uri-into-request-header

### 2.8. ignorePaths

A comma-separated list of URL path for which you would like WOVN to not translation content withing given directories.

Ignored paths and their contents will not be processed by WovnServletFilter, and will not be sent to Wovn.io for translation. 

For instance, if you want to not translation the admin directory of your website, you should configure as below.

```XML
<init-param>
  <param-name>ignorePaths</param-name>
  <param-value>/admin,/wp-admin</param-value>
</init-param>
```
With this configuration, WOVN.java will ignore the following URLs
```Text
https://my-wesite.com/admin
https://my-wesite.com/admin/
https://my-website.com/admin/plugin.html
https://my-wesite.com/wp-admin
https://my-wesite.com/wp-admin/
https://my-website.com/wp-admin/anypages
```
but allow the following
```Text
https://my-website.com/index.html
https://my-website.com/user/admin
https://my-website.com/adminpage
```

### 2.9. ignoreClasses

A comma-separated list of HTML classes for which you would like WOVN to skip the elements of.
(This setting is used to prevent confidential data contained in the page to be translated from being sent to WOVN)

Ignored elements and their contents will not be processed by WovnServletFilter, and will not be sent to Wovn.io for translation. (The elements will still be present in the resulting web page, however.)

For example, if you include `my-secret-class` in this parameter and you have an element as follows
```HTML
  <div>
    <p class="my-secret-class">Some information WOVN does not touch</p>
  </div>
```
For the purpose of translation, WOVN will treat it as
```HTML
  <div></div>
```

Including three classes, `email-address-element`, `my-secret-class`, and `noshow`, in your ignoreClasses parameter would look as follows

```XML
<init-param>
  <param-name>ignoreClasses</param-name>
  <param-value>email-address-element,my-secret-class,noshow</param-value>
</init-param>
```

### 2.10. enableFlushBuffer
A flag to adjust the behavior of `ServletResponse.flushBuffer()`.

This parameter is set to `false` by default (recommended).

When `enableFlushBuffer` is set to `false`, WovnServletFilter will capture calls to `response.flushBuffer()` without
immediately writing content to the client. Only when the complete HTML response is ready will the filter translate the content
and send it to the client. This is necessary in order to translate the content properly.

### 2.11. sitePrefixPath

This parameter lets you set a prefix path to use as an anchor for which WOVN will translate pages. With this setting, WOVN will only translate pages that match the prefix path, and the path language code will be added _after_ the prefix path.

If, for example, you set your sitePrefix path to `city` as follows
```xml
<init-param>
  <param-name>sitePrefixPath</param-name>
  <param-value>city</param-value>
</init-param>
```
WOVN will only translate pages that match `http://www.mysite.com/city/*`.

`http://www.mysite.com/city/tokyo/map.html` would be translated, and it would be possible to access that page with language code (in english) like this: `http://www.mysite.com/city/en/tokyo.map.html`.

By default, WOVN will translate all pages for your domain and process path language codes at the beginning of the path.

#### Requirements

This setting may only be used together with the `urlPattern = path` setting.

Furthermore, it is highly recommended to also configure your `web.xml` with a corresponding filter-mapping for the WovnServletFilter. If prefix path is set to `city` as in the example above, the corresponding filter-mapping would look as follows.
```xml
<filter-mapping>
  <filter-name>wovn</filter-name>
  <url-pattern>/city/*</url-pattern>
  ...
</filter-mapping>
```

### 2.12. langCodeAliases

This setting lets you specify the language identifier for your supported languages.

For example, the default language identifier for English is `en`, such that a URL for your page in English language may look like `http://site.com/en/page`.
With `langCodeAliases`, you can change the language identifier to `us`, for example. The resulting URL would look like `http://site.com/us/page` instead.

This setting is only valid for url patterns `path`, `query`, and `subdomain`.

The format is as follows
```
FORMAT: <langCode>:<alias>,<langCode>:<alias>,...

EXAMPLE: ja:japan,en:us
```
In `web.xml`, the configuration will look like this
```xml
<init-param>
  <param-name>langCodeAliases</param-name>
  <param-value>ja:japan,en:us</param-value>
</init-param>
```

#### Alias for default language

If your original content exists at a location that already includes a form of language code, you can make the WovnServletFilter treat this path or subdomain as a language code by configuring a language alias for your default language.

To illustrate, here is an example:

> Your content already exists at `http://site.com/jp/*`, and your default language is Japanese.
>
> You want the URLs for translated content in English to change the `/jp/` to `/en/`, such that `http://site.com/jp/home.html` becomes `http://site.com/en/home.html`.

Achieve this result by configuring `jp` as an alias for Japanese.

### 2.13. customDomainLangs

This setting lets you define the domain and path that corresponds to each of your supported languages.

The format of the setting is as follows
```
FORMAT: <baseURL>:<langCode>,<baseURL>:<langCode>,...

EXAMPLE: www.site.co.jp:ja,www.site.com/english:en
```
Note that `<baseURL>` has only host and path prefix.
Anything before the host, like `http://` should not be included. Port numbers should also not be included. However, all applicable subdomains must be included.

For the example above, all request URLs that match `www.site.co.jp` will be considered as requests in Japanese langauge.
All request URLs that match `www.site.com/english/*` will be considered as requests in English language.
Requests that do not match a domain language, like for example `www.site.com/admin`, will not be processed by WovnServletFilter.

With the above example configuration, the page `http://www.site.co.jp/about.html` in english language will
have the URL `http://www.site.com/english/about.html`.

The corresponding `web.xml` parameters will look as follows
```xml
<init-param>
  <param-name>urlPattern</param-name>
  <param-value>customDomain</param-value>
</init-param>
<init-param>
  <param-name>customDomainLangs</param-name>
  <param-value>www.site.co.jp:ja,www.site.com/english:en</param-value>
</init-param>
```

#### Requirements

This setting may only be used together with the `urlPattern = customDomain` setting.

If this setting is used, each language declared in `supportedLangs` must be given a custom domain. Vice versa, each custom domain language must also be represented in `supportedLangs`.

Lastly, the path declared for your original language must match the structure of the underlying web server.
In other words, you cannot use this setting to change the request path of your content in original language.

### 2.14. debugMode

A flag to enable extra debugging features.

Turn on debugMode by setting the parameter to true.
```XML
<init-param>
  <param-name>debugMode</param-name>
  <param-value>true</param-value>
</init-param>
```

With debugMode on, two extra query parameters become available to change wovnjava's behavior.

#### wovnCacheDisable
Example request: `http://example.com/page/top.html?wovnCacheDisable`

Using `wovnCacheDisable` as a query parameter will make wovnjava bypass the translation API cache, such that translation is always re-processed.
This will make the request slower, but it is sometimes useful in order to inspect updated behavior.
(Note that this does not clear cache or change behavior for the same page request without `wovnCacheDisable` query parameter.)

#### wovnDebugMode
Example request: `http://example.com/page/top.html?wovnDebugMode`

Using `wovnDebugMode` as a query parameter will activate embedded debug information in the response HTML comming from the server.
This is intended to better understand what the problem is if something is not working correctly with wovnjava on your server.

_Note that `wovnCacheDisable` and `wovnDebugMode` is only available when debugMode is turned on in your wovnjava configuration._

### 2.15. encoding

You can choose to optionally explicitly specify the encoding used by your HTML. If `encoding` is not set, wovnjava will attempt to automatically detect the encoding.

Commonly used encodings are `utf-8`, `Shift_JIS` and `EUC-JP`. For a complete list of supported encodings, please refer to Java's official documentation [here](https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html).

## Supported Langauges

Language code | Language name | Name in English
---|---|---
ar | العربية | Arabic
eu | Euskara | Basque
bn | বাংলা ভাষা | Bengali
bg | Български | Bulgarian
ca | Català | Catalan
zh-CHS | 简体中文 | Simp Chinese
zh-CHT | 繁體中文 | Trad Chinese
zh-CN | 简体中文（中国） | Simp Chinese (China)
zh-Hant-HK | 繁體中文（香港） | Trad Chinese (Hong Kong)
zh-Hant-TW | 繁體中文（台湾） | Trad Chinese (Taiwan)
da | Dansk | Danish
nl | Nederlands | Dutch
en | English | English
en-AU | English (Australia) | English (Australia)
en-CA | English (Canada) | English (Canada)
en-IN | English (India) | English (India)
en-NZ | English (New Zealand) | English (New Zealand)
en-ZA | English (South Africa) | English (South Africa)
en-GB | English (United Kingdom) | English (United Kingdom)
en-SG | English (Singapore) | English (Singapore)
en-US | English (United States) | English (United States)
fi | Suomi | Finnish
fr | Français | French
fr-CA | Français (Canada) | French (Canada)
fr-FR | Français (France) | French (France)
fr-CH | Français (Suisse) | French (Switzerland)
gl | Galego | Galician
de | Deutsch | German
de-AT | Deutsch (Österreich) | German (Austria)
de-DE | Deutsch (Deutschland) | German (Germany)
de-LI | Deutsch (Liechtenstien) | German (Liechtenstien)
de-CH | Deutsch (Schweiz) | German (Switzerland)
el | Ελληνικά | Greek
he | עברית | Hebrew
hu | Magyar | Hungarian
id | Bahasa Indonesia | Indonesian
it | Italiano | Italian
it-IT | Italiano (Italia) | Italian (Italy)
it-CH | Italiano (Svizzera) | Italian (Switzerland)
ja | 日本語 | Japanese
ko | 한국어 | Korean
lv | Latviešu | Latvian
ms | Bahasa Melayu | Malay
my | ဗမာစာ | Burmese
ne | नेपाली भाषा | Nepali
no | Norsk | Norwegian
fa | زبان_فارسی | Persian
pl | Polski | Polish
pt | Português | Portuguese
pt-BR | Português (Brasil) | Portuguese (Brazil)
pt-PT | Português (Portugal) | Portuguese (Portugal)
ru | Русский | Russian
es | Español | Spanish
es-RA | Español (Argentina) | Spanish (Argentina)
es-CL | Español (Chile) | Spanish (Chile)
es-CO | Español (Colombia) | Spanish (Colombia)
es-CR | Español (Costa Rica) | Spanish (Costa Rica)
es-HN | Español (Honduras) | Spanish (Honduras)
es-419 | Español (Latinoamérica) | Spanish (Latin America)
es-MX | Español (México) | Spanish (Mexico)
es-PE | Español (Perú) | Spanish (Peru)
es-ES | Español (España) | Spanish (Spain)
es-US | Español (Estados Unidos) | Spanish (United States)
es-UY | Español (Uruguay) | Spanish (Uruguay)
es-VE | Español (Venezuela) | Spanish (Venezuela)
sw | Kiswahili | Swahili
sv | Svensk | Swedish
th | ภาษาไทย | Thai
hi | हिन्दी | Hindi
tr | Türkçe | Turkish
uk | Українська | Ukrainian
ur | اردو | Urdu
vi | Tiếng Việt | Vietnamese
tl | Tagalog | Tagalog
km | ភាសាខ្មែរ | Khmer
ta | தமிழ் | Tamil
si | සිංහල | Sinhala
