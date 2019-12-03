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
    <!-- These lines are not needed if you're using a SNAPSHOT version. -->
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
    </snapshots>
    <!-- end -->
  </repository>
  
</repositories>
```

##### 1.3.1.2. Add the WOVN.io library as a dependency to your project's pom.xml.

```XML
<dependency>
  <groupId>com.github.wovnio</groupId>
  <artifactId>wovnjava</artifactId>
  <!-- set the wovnjava version you're using here -->
  <!-- if you want to use a development version of wovnjava, set the version to "-SNAPSHOT" -->
  <version>0.1.0</version>
</dependency>
```

You can see all available versions of wovnjava [here](https://jitpack.io/#wovnio/wovnjava).

##### 1.3.1.3. Add the wovnjava library's settings to your servlet's web.xml.

```XML
<filter>
  <filter-name>wovn</filter-name>
  <filter-class>com.github.wovnio.wovnjava.WovnServletFilter</filter-class>
  <init-param>
    <param-name>projectToken</param-name>
    <param-value>2Wle3</param-value><!-- set your project token -->
  </init-param>
</filter>

<filter-mapping>
  <filter-name>wovn</filter-name>
  <url-pattern>/*</url-pattern><!-- set the URL pattern the wovnjava library (Servlet Filter) will be applicable to -->
</filter-mapping>
```

## 2. Parameter Settings

The following parameters can be set within the WOVN.io Java Library.

Parameter Name            | Required | Default Setting
------------------------- | -------- | ------------
projectToken              | yes      | ''
urlPattern                | yes      | 'path'
defaultLang               | yes      | 'en'
useProxy                  |          | 'false'
debugMode                 |          | 'false'
originalUrlHeader         |          | ''
originalQueryStringHeader |          | ''
ignoreClasses             |          | ''

* A required parameter with a default setting does not need to be set within the web.xml. (Only the projectToken parameter must be set in order for the library to work)

### 2.1. projectToken

Set your WOVN.io Account's project token. This parameter is required.

### 2.3. urlPattern

Within the Java Application, the library works by adding new URL's for translation. You can set the URL type by using the urlPattern Parameter.
Three basic URL pattern types are available, plus a highly customizable Custom Domain option.

The examples below are for the original URL `https://wovn.io/contact`, when visiting the page in korean (as a translated language).

url pattern type | Translated page's URL           | Description
---------------- | ------------------------------- | ------
"path"           | https://wovn.io/ko/contact      | Language code is inserted as the first section of the path
"subdomain"      | https://ko.wovn.io/contact      | Language code is inserted as the first subdomain. (The server's DNS settings must be configured for this option.)
"query"          | https://wovn.io/contact?wovn=ko | Language code is inserted as a query parameter. (This option requires the least amount of changes to the application.)
"customDomain"   | (see section 2.12 below)        | The custom domain option lets you define domain and path for each language independently.

### 2.5. defaultLang

This sets the Java application's default language. The default value is english ('en').

If a request is made with the default language inserted as a parameter in the URL, before the library begins translating the URL is redirected. The defaultLang parameter is used for this purpose.

If the default_lang is set to 'en', when receiving a request for the following URL,

    https://wovn.io/en/contact

The library will redirect to the following URL.

    https://wovn.io/contact

### 2.6. useProxy

When using a reverse proxy, if the WOVN.io Java Library is not given an appropriate host name, the page's translation data may not be accessible. If you set useProxy to true, during the WOVN.io Java Library's processing, it will use the HTTP Request Header's X-Forwarded-Host in order to receive the translation data.

### 2.7. debugMode

By turning on debugMode, you will enable extra debugging features for wovnjava.

Turn on debugMode like this:
```XML
  <init-param>
    <param-name>debugMode</param-name>
    <param-value>true</param-value>
  </init-param>
```

Two extra query parameters become available to change wovnjava's behavior:

#### wovnCacheDisable
Example request: `http://example.com/page/top.html?wovnCacheDisable`

Using `wovnCacheDisable` as a query parameter will make wovnjava bypass the translation API cache, such that translation is always re-processed.
This will make the request slower, but it is sometimes useful in order to force updated behavior.

#### wovnDebugMode
Example request: `http://example.com/page/top.html?wovnDebugMode`

Using `wovnDebugMode` as a query parameter will activate embedded debug information in the response HTML comming from the server.
This is intended to better understand what the problem is if something is not working correctly with wovnjava on your server.

_Note that `wovnCacheDisable` and `wovnDebugMode` is only available when debugMode is turned on in your wovnjava configuration._

### 2.8. originalUrlHeader, originalQueryStringHeader

When you're using the Apache HTTP Server's mod_rewrite module, wovnjava is given the URL after rewriting. In this case, wovnjava is sometimes unable to retreive the correct translation data from the API server.

If you've configured originalUrlHeader and originalQueryStringHeader in your Application's settings file, wovnjava will use these request headers's values to retreive translation data.

Using the following Apache HTTP Server settings, if the URL (prior to rewriting) is set within the request headers,

```
SetEnvIf Request_URI "^(.*)$" REQUEST_URI=$1
RequestHeader set X-Request-Uri "%{REQUEST_URI}e"
RewriteRule .* - [E=REQUEST_QUERY_STRING:%{QUERY_STRING}]
RequestHeader set X-Query-String "%{REQUEST_QUERY_STRING}e"
```

wovnjava will use the following settings along with the correct URL (prior to rewriting) to retreive the correct translation data from the API server.

```XML
<filter>
  ...
  <init-param>
    <param-name>originalUrlHeader</param-name>
    <param-value>X-Request-Uri</param-value>
  </init-param>
  <init-param>
    <param-name>originalQueryStringHeader</param-name>
    <param-value>X-Query-String</param-value>
  </init-param>
  ...
</filter>
```
* The sample request header shown above was referenced from the following site.

https://coderwall.com/p/jhkw7w/passing-request-uri-into-request-header

### 2.9. ignoreClasses

This parameter is a comma-separated list of HTML classes for which you would like WOVN to skip the elements of.

For example, if you include `my-secret-class` in this parameter and you have an element as follows
```HTML
  <div>
    <p class="my-secret-class">Some information WOVN does not touch</p>
  </div>
```
WOVN will treat it as
```HTML
  <div></div>
```

Including three classes, `email-address-element`, `my-secret-class`, and `noshow`, in your ignoreClasses parameter would look as follows

```XML
<filter>
  ...
  <init-param>
    <param-name>ignoreClasses</param-name>
    <param-value>email-address-element,my-secret-class,noshow</param-value>
  </init-param>
  ...
</filter>
```

### 2.10. enableFlushBuffer
This parameter is set to `false` by default.

When `enableFlushBuffer` is set to `false`, the wovnjava servlet filter will capture calls to `response.flushBuffer()` without
immediately writing content to the client. Only when the complete HTML response is ready will the filter translate the content
and send it to the client. This is necessary in order to translate the content properly.

### 2.11. sitePrefixPath

This parameter lets you set a prefix path to use as an anchor for which WOVN will translate pages. With this setting, WOVN will only translate pages that match the prefix path, and the path language code will be added _after_ the prefix path.

If, for example, you set your sitePrefix path to `city` as follows
```
<filter>
  ...
  <init-param>
    <param-name>sitePrefixPath</param-name>
    <param-value>city</param-value>
  </init-param>
  ...
</filter>
```
WOVN will only translate pages that match `http://www.mysite.com/city/*`.

`http://www.mysite.com/city/tokyo/map.html` would be translated, and it would be possible to access that page with language code (in english) like this: `http://www.mysite.com/city/en/tokyo.map.html`.

By default, WOVN will translate all pages for your domain and process path language codes at the beginning of the path.

#### Requirements

This setting may only be used together with the `urlPattern = path` setting.

Furthermore, it is highly recommended to also configure your `web.xml` with a corresponding filter-mapping for the WovnServletFilter. If prefix path is set to `city` as in the example above, the corresponding filter-mapping would look as follows.
```
<filter-mapping>
  <filter-name>wovn</filter-name>
  <url-pattern>/city/*</url-pattern>
  ...
</filter-mapping>
```

### 2.12. customDomainLangs

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

The corresponding `web.xml` configuration will look as follows
```xml
<filter>
  ...
  <init-param>
    <param-name>urlPattern</param-name>
    <param-value>customDomain</param-value>
  </init-param>
  <init-param>
    <param-name>customDomainLangs</param-name>
    <param-value>www.site.co.jp:ja,www.site.com/english:en</param-value>
  </init-param>
  ...
</filter>
```

#### Requirements

This setting may only be used together with the `urlPattern = customDomain` setting.

If this setting is used, each language declared in `supportedLangs` must be given a custom domain. Vice versa, each custom domain language must also be represented in `supportedLangs`.

Lastly, the path declared for your original language must match the structure of the underlying web server.
In other words, you cannot use this setting to change the request path of your content in original language.

### 2.13. supportedLangs

This parameter lists the set of languages for which the library performs translations.

Example settings for English, Japanese, and Korean:
```xml
<filter>
    ...
    <init-param>
      <param-name>supportedLangs</param-name>
      <param-value>en,ja,ko</param-value>
    </init-param>
    ...
</filter>
```
Note: `defaultLang` will automatically be included in the set of supported languages.

Find a list of languages that WOVN supports below.

## Supported Langauges

Language code | Language name | Name in English
---|---|---
ar | ﺎﻠﻋﺮﺒﻳﺓ | Arabic
bg | Български | Bulgarian
zh-CHS | 简体中文 | Simp Chinese
zh-CHT | 繁體中文 | Trad Chinese
da | Dansk | Danish
nl | Nederlands | Dutch
en | English | English
fi | Suomi | Finnish
fr | Français | French
de | Deutsch | German
el | Ελληνικά | Greek
he | עברית | Hebrew
id | Bahasa | Indonesian
it | Italiano | Italian
ja | 日本語 | Japanese
ko | 한국어 | Korean
ms | Bahasa | Malay
my | ဗမာစာ | Burmese
ne | नेपाली भाषा | Nepali
no | Norsk | Norwegian
pl | Polski | Polish
pt | Português | Portuguese
ru | Русский | Russian
es | Español | Spanish
sv | Svensk | Swedish
th | ภาษาไทย | Thai
hi | हिन्दी | Hindi
tr | Türkçe | Turkish
uk | Українська | Ukrainian
vi | Tiếng | Vietnamese
tl | Tagalog | Tagalog
