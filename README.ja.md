
For English users: [English](README.en.md)


# WOVN.io Java ライブラリ

WOVN.io Java ライブラリは Java アプリケーションで WOVN.io ライブラリ方式の翻訳を実現するライブラリです。WOVN.io Java ライブラリは Servlet Filter として実装されています。

本ドキュメントは WOVN.io Java ライブラリのインストール手順と、設定パラメータについて説明します。

## 1. インストール手順

### 1.1. WOVN.io のアカウント作成

WOVN.io Java ライブラリを使用するためには、WOVN.io のアカウントが必要です。
アカウントをお持ちでない場合は、まず [WOVN.io](https://wovn.io) にてサインアップをしてください。

### 1.2. 翻訳ページの追加

[WOVN.io](https://wovn.io) にサインインをして、翻訳したいページを追加してください

### 1.3. Java アプリケーションの設定

#### 1.3.1. Maven の場合

※ Maven 以外をお使いの場合は、こちらの設定方法をご覧ください。(https://jitpack.io/#wovnio/wovnjava)

##### 1.3.1.1. 本ライブラリを組み込むアプリケーションの pom.xml に、JitPack のリポジトリを追加してください。

```XML
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
    <!-- SNAPSHOT バージョンを使用しない場合は、以下の行は必要ありません。 -->
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
    </snapshots>
    <!-- end -->
  </repository>
</repositories>
```

##### 1.3.1.2. アプリケーションの pom.xml の依存関係に、本ライブラリを追加してください。

```XML
<dependency>
  <groupId>com.github.wovnio</groupId>
  <artifactId>wovnjava</artifactId>
  <!-- 使用するライブラリのバージョンを指定してください。 -->
  <!-- 開発中のバージョンを使用する場合は、「-SNAPSHOT」 を設定してください。 -->
  <version>0.1.0</version>
</dependency>
```

使用可能なライブラリのバージョンはこちらのページで確認できます。(https://jitpack.io/#wovnio/wovnjava)

##### 1.3.1.3. ライブラリの設定をアプリケーションの web.xml に記述してください。

`web.xml` で、WovnServletFilterのパラメータを設定します。

4つの必須パラメータ:
1) projectToken
2) defaultLang
3) supportedLangs
4) urlPattern

これらのパラメータがあなたのWovnプロジェクトと一致していることが重要です。パラメータの詳細はセクション2に記載されています。

WovnServletFilterの最小構成の例は以下のようになります。これは、トークンが "123abc"、翻訳元が日本語、翻訳先が英語のプロジェクトの場合です。

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
  <!-- ライブラリ (Servlet Filter) を適用する URL パターンを設定してください。 -->
  <url-pattern>/*</url-pattern>
  <dispatcher>REQUEST</dispatcher>
  <dispatcher>FORWARD</dispatcher>
</filter-mapping>
```

推奨される `filter-mapping` は、ディスパッチャに `REQUEST` と `FORWARD` を許可し、`url-pattern` にあるすべてのパスを許可することです。ウェブサーバ上の特定のディレクトリだけを WovnServletFilter が対象としたい場合は、それに応じて `url-pattern` を設定してください。

## 2. 設定パラメータ

WOVN.io Java ライブラリに設定可能なパラメータは以下の通りです。

パラメータ名              | 必須 | 初期値
------------------------- | ---- | ------------
projectToken              | yes  |
defaultLang               | yes  |
supportedLangs            | yes  |
urlPattern                | yes  |
useProxy                  |      | false
originalUrlHeader         |      |
originalQueryStringHeader |      |
ignoreClasses             |      |
enableFlushBuffer         |      | false
sitePrefixPath            |      |
langCodeAliases           |      |
customDomainLangs         |      |
debugMode                 |      | false

※ 初期値が設定されている必須パラメータは、web.xml で設定しなくても大丈夫です。（projectToken だけ指定すればライブラリを動作させることができます）

### 2.1. projectToken (必須)

あなたの WOVN.io アカウントのユーザートークンを設定してください。このパラメータは必須です。

### 2.2. defaultLang (必須)

ウェブサーバのコンテンツの翻訳元の言語。

defaultLang は 2 文字の言語コードとして宣言されています。
このドキュメントの下部にある言語コードのリストを参照してください。

### 2.3. supportedLangs (必須)

このプロジェクトで使用するすべての言語のリスト。(defaultLangは自動的にこのリストに含まれます)。

supportedLangs は、言語コードのカンマ区切りのリストとして宣言されています。
このドキュメントの一番下にある言語コードのリストを参照してください。

### 2.4. urlPattern (必須)

翻訳された言語のページを指すURLの形式。

WovnServletFilter は、翻訳された言語で宣言されたリクエストURLを受け入れます。
この設定は、それらのURLがどのように見えるかを指定します。

3つの基本的なURLパターンが利用可能で、高度にカスタマイズ可能なカスタムドメインオプションもあります。

以下の例は、韓国語(翻訳された言語)のページを訪問したときのオリジナルURL `https://wovn.io/contact` のためのものです。

URL形式      | 翻訳されたページのURL           | 説明
path         | https://wovn.io/ko/contact      | 言語コードはパスの最初の部分に挿入されます
subdomain    | https://ko.wovn.io/contact      | 言語コードが最初のサブドメインとして挿入されます。(このオプションにはDNS設定が設定されている必要があります)
query        | https://wovn.io/contact?wovn=ko | 言語コードがクエリパラメータとして挿入されます。(このオプションはアプリケーションへの変更を最小限に抑えます)
customDomain | (下記2.10項参照)                | カスタムドメインオプションでは、各言語のドメインとパスを独立して定義することができます。

### 2.5. useProxy

Javaウェブサーバがリバースプロキシの後ろにある場合に設定するフラグ。

WovnServletFilter が受信したリクエストが Wovn プロジェクトにマッチするホスト名を持っていない場合、ページの翻訳データにアクセスできない可能性があります。これは、リクエストがリバースプロキシを介してルーティングされている場合に発生する可能性があります。

useProxy を true に設定すると、WovnServletFilter は HTTP リクエストヘッダ `X-Forwarded-Host` と `X-Forwarded-Port` を使用してリクエストのホスト名とポートを決定します。

次のように useProxy を true に設定します。
```xml
<init-param>
  <param-name>useProxy</param-name>
  <param-value>true</param-value>
</init-param>
```

リバースプロキシがリクエストパスやクエリを書き換える場合、 originalUrlHeaderやoriginalQueryStringHeaderの設定も必要になるかもしれないことに注意して下さい。

### 2.6. originalUrlHeader, originalQueryStringHeader

元のリクエストパスとクエリを宣言するためのHTTPヘッダの名前。

例えば、Apache HTTP Serverのmod_rewriteモジュールを使用して、着信リクエストが書き換えられている場合、WovnServletFilterは元のリクエストURLを見ることができないかもしれません。この場合、API サーバから正しい翻訳データを取得できないかもしれません。

`originalUrlHeader` と `originalQueryStringHeader` が設定されている場合、WovnServletFilter はこれらの HTTP ヘッダ名を検査して元のパスとクエリを決定します。 `originalUrlHeader` は元のパスの HTTP ヘッダ名を宣言しなければなりません。

これらのパラメータが設定されている場合、useProxy は `true` に設定されるべきです。

#### 設定例

この例では、HTTPヘッダ名 `X-Request-Uri` と `X-Query-String` を用いてパスとクエリを宣言します。

受信したリクエストのパスとクエリを HTTP ヘッダに保存するように Apache を設定します。

```apache
SetEnvIf Request_URI "^(.*)$" REQUEST_URI=$1
RequestHeader set X-Request-Uri "%{REQUEST_URI}e"
RewriteRule .* - [E=REQUEST_QUERY_STRING:%{QUERY_STRING}]
RequestHeader set X-Query-String "%{REQUEST_QUERY_STRING}e"
```

WovnServletFilter を設定して、同じ HTTP ヘッダからのリクエストパスとクエリを決定します。

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

_上記のサンプルリクエストヘッダーは、以下のサイトから参照:_
https://coderwall.com/p/jhkw7w/passing-request-uri-into-request-header

### 2.7. ignoreClasses

WOVNに翻訳をスキップさせたいHTMLクラスのカンマ区切りのリスト。
（翻訳対象のページに含まれる機密データを、WOVNへ送信しないようにするための設定です）

無視された要素とその内容はWovnServletFilterによって処理されず、翻訳のためにWovn.ioに送られません。(しかし、要素は結果としてのウェブページにはまだ存在します)。

例えば、このパラメータに `my-secret-class` を含め、以下のような要素を持つとします。

```HTML
  <div>
    <p class="my-secret-class">Some information WOVN does not touch</p>
  </div>
```

翻訳の目的上、WOVNはそれを空であるかのように扱います

```HTML
  <div></div>
```

ignoreClasses パラメータに `email-address-element`, `my-secret-class`, `noshow` の3つのクラスを含めると、以下のようになります。

```XML
<init-param>
  <param-name>ignoreClasses</param-name>
  <param-value>email-address-element,my-secret-class,noshow</param-value>
</init-param>
```

### 2.8. enableFlushBuffer

ServletResponse.flushBuffer()`の振る舞いを調整するフラグ。

このパラメータはデフォルトで `false` に設定されています(推奨)。

`enableFlushBuffer` が `false` に設定されている場合、WovnServletFilter はクライアントにコンテンツをすぐに書き込まずに `response.flushBuffer()` の呼び出しを捕捉します。
完全な HTML レスポンスの準備ができて初めて、フィルタはコンテンツを翻訳してクライアントに送信します。これは適切に翻訳するために必要なことです。

### 2.9. sitePrefixPath

このパラメータでは、WOVN がページを翻訳する際のアンカーとして使用するプレフィックスパスを設定します。この設定では、WOVN はプレフィックスパスにマッチするページのみを翻訳し、 パスの言語コードはプレフィックスパスの後に追加されます。


例えば、以下のように sitePrefix のパスを `city` に設定したとします。

```xml
<init-param>
  <param-name>sitePrefixPath</param-name>
  <param-value>city</param-value>
</init-param>
```

WOVNは`http://www.mysite.com/city/*`にマッチするページのみを翻訳します。
`http://www.mysite.com/city/tokyo/map.html` が翻訳され、以下のように言語コード(英語)で、そのページにアクセスできるようになります。
`http://www.mysite.com/city/en/tokyo.map.html` のような言語コード(英語)でそのページにアクセスできるようになります。

デフォルトでは、WOVNはあなたのドメインのすべてのページを翻訳し、パスの言語コードをパスの最初に処理します。

#### Requirements

この設定は `urlPattern = path` の設定と併用することができます。

さらに、WovnServletFilterに対応するフィルタマッピングを `web.xml` に設定することを強く推奨します。上の例のようにプレフィックスパスを `city` に設定すると、対応するフィルタマッピングは以下のようになります。

```xml
<filter-mapping>
  <filter-name>wovn</filter-name>
  <url-pattern>/city/*</url-pattern>
  ...
</filter-mapping>
```

### 2.10. langCodeAliases

この設定では、サポートされている言語の言語識別子を指定することができます。

例えば、英語のデフォルトの言語識別子は `en` であり、英語のページのURLは `http://site.com/en/page` のように見えるかもしれません。
`langCodeAliases` を使うと、言語識別子を例えば `us` に変更することができます。結果として得られるURLは `http://site.com/us/page` のようになります。

この設定は、URLパターン `path`, `query`, `subdomain` に対してのみ有効です。

形式は以下の通りです。

```
FORMAT: <langCode>:<alias>,<langCode>:<alias>,...

EXAMPLE: ja:japan,en:us
```

`web.xml` では、設定は以下のようになります。

```xml
<init-param>
  <param-name>langCodeAliases</param-name>
  <param-value>ja:japan,en:us</param-value>
</init-param>
```

#### Alias for default language

元のコンテンツが既に言語コードを含む場所に存在する場合、デフォルト言語の言語エイリアスを設定することで、WovnServletFilter がこのパスやサブドメインを言語コードとして扱うようにすることができます。

説明のために、ここでは一例を示します:

> あなたのコンテンツはすでに `http://site.com/jp/*` に存在し、デフォルトの言語は日本語です。
>
> 英語の翻訳コンテンツのURLは、 `/jp/` を `/en/` に変更して、 `http://site.com/jp/home.html` が `http://site.com/en/home.html` になるようにしたい。

日本語のエイリアスとして `jp` を設定することで、この結果を得ることができます。

### 2.11. customDomainLangs

この設定では、サポートされている各言語に対応するドメインとパスを定義できます。

設定の形式は以下の通りです

```
FORMAT: <baseURL>:<langCode>,<baseURL>:<langCode>,...

EXAMPLE: www.site.co.jp:ja,www.site.com/english:en
```

`<baseURL>` はホストとパスのプレフィックスのみを持つことに注意してください。
ホストの前には `http://` のようなものを含めてはならない。ポート番号も含めてはならない。ただし、該当するサブドメインはすべて含めなければならない。

上記の例では、`www.site.co.jp` にマッチするリクエストURLはすべて日本語のリクエストとして扱われます。
また、`www.site.com/english/*` にマッチするリクエストURLはすべて英語のリクエストとして扱われます。
例えば `www.site.com/admin` のようにドメイン言語にマッチしないリクエストは WovnServletFilter によって処理されません。

上記の例では、英語のページ `http://www.site.co.jp/about.html` は `http://www.site.com/english/about.html` というURLを持つことになります。

対応する `web.xml` パラメータは以下のようになります。

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

この設定は `urlPattern = customDomain` の設定と一緒にのみ使用することができます。

この設定を使用する場合、`supportedLangs` で宣言された各言語にカスタムドメインを与えなければなりません。逆に、各カスタムドメインの言語も `supportedLangs` で表現されなければなりません。

最後に、オリジナル言語のために宣言されたパスは、基礎となるウェブサーバーの構造と一致していなければなりません。
尚、この設定を使用してオリジナル言語のコンテンツのリクエストパスを変更することはできません。

### 2.12. debugMode

追加のデバッグ機能を有効にするフラグです。

パラメータを `true` に設定して `debugMode` をオンにします。

```XML
<init-param>
  <param-name>debugMode</param-name>
  <param-value>true</param-value>
</init-param>
```

debugModeをオンにすると、wovnjavaの動作を変更するために2つの追加のクエリパラメータが利用可能になります。

#### wovnCacheDisable

リクエスト例: `http://example.com/page/top.html?wovnCacheDisable`

クエリパラメータとして `wovnCacheDisable` を使うと、wovnjavaは翻訳APIキャッシュをバイパスし、翻訳が常に再処理されるようになります。
これによりリクエストは遅くなりますが、更新された動作を確認するのに便利な場合もあります。
(クエリパラメータ `wovnCacheDisable` がない同じページのリクエストに対して、キャッシュをクリアしたり、動作を変更したりすることはできないことに注意してください)。

#### wovnDebugMode

リクエスト例: `http://example.com/page/top.html?wovnDebugMode`

クエリパラメータとして `wovnDebugMode` を使用すると、サーバから送られてくるレスポンスHTMLに埋め込まれたデバッグ情報が有効になります。
これは、何かがサーバ上のwovnjavaで正しく動作していない場合、問題が何であるかをよりよく理解することを目的としています。

_`wovnCacheDisable` および `wovnDebugMode` は、wovnjava の設定で debugMode がオンになっている場合にのみ利用可能であることに注意してください。_

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
tl | Tagalog | Tagalog
th | ภาษาไทย | Thai
hi | हिन्दी | Hindi
tr | Türkçe | Turkish
uk | Українська | Ukrainian
ur | اردو | Urdu
vi | Tiếng Việt | Vietnamese
tl | Tagalog | Tagalog
km | ភាសាខ្មែរ | Khmer
