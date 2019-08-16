# WOVN.io Java ライブラリ

WOVN.io Java ライブラリは Java アプリケーションで WOVN.io ライブラリ方式の翻訳を実現するライブラリです。WOVN.io Java ライブラリは Servlet Filter として実装されています。

本ドキュメントは WOVN.io Java ライブラリのインストール手順と、設定パラメータについて説明します。

## 1. インストール手順

### 1.1. WOVN.io のアカウント作成

WOVN.io Java ライブラリを使用するためには、WOVN.io のアカウントが必要です。 アカウントをお持ちでない場合は、まず [WOVN.io](https://wovn.io) にてサインアップをしてください。

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

```XML
<filter>
  <filter-name>wovn</filter-name>
  <filter-class>com.github.wovnio.wovnjava.WovnServletFilter</filter-class>
  <init-param>
    <param-name>projectToken</param-name>
    <param-value>2Wle3</param-value><!-- ユーザートークンを設定してください。 -->
  </init-param>
</filter>

<filter-mapping>
  <filter-name>wovn</filter-name>
  <url-pattern>/*</url-pattern><!-- ライブラリ (Servlet Filter) を適用する URL パターンを設定してください。 -->
</filter-mapping>
```

## 2. 設定パラメータ

WOVN.io Java ライブラリに設定可能なパラメータは以下の通りです。

パラメータ名              | 必須かどうか | 初期値
------------------------- | ------------ | ------------
projectToken              | yes          | ''
urlPattern                | yes          | 'path'
defaultLang               | yes          | 'en'
useProxy                  |              | 'false'
debugMode                 |              | '0'
originalUrlHeader         |              | ''
originalQueryStringHeader |              | ''

※ 初期値が設定されている必須パラメータは、web.xml で設定しなくても大丈夫です。（projectToken だけ指定すればライブラリを動作させることができます）

### 2.1. projectToken

あなたの WOVN.io アカウントのユーザートークンを設定してください。このパラメータは必須です。

### 2.3. urlPattern

ライブラリは Java アプリケーションに対し、翻訳ページ用の新しい URL を追加します。urlPattern パラメータでは、この URL のタイプを設定できます。URL のタイプには下記の3種類があります。

パラメータ  | 翻訳ページの URL                | 備考
----------- | ------------------------------- | ------
'path'      | https://wovn.io/ja/contact      | 初期値。設定しない場合はこれになります。
'subdomain' | https://ja.wovn.io/contact      | DNS の設定が必要です。
'query'     | https://wovn.io/contact?wovn=ja | アプリケーションの変更が一番少なく済みます。

※ 上記翻訳ページの URL は、下記のページをライブラリで翻訳した場合の例です。

    https://wovn.io/contact

### 2.4. defaultLang

Java アプリケーションの言語を設定してください。初期値は英語 ('en') です。

デフォルト言語のページへパラメータ付きでアクセスがあった場合、ライブラリは翻訳前の URL にリダイレクトします。defaultLang はこの処理に使用されます。

defaultLang が 'en' で下記 URL にリクエストがあった場合、

    https://wovn.io/en/contact

ライブラリは、下記 URL にリダイレクトします。

    https://wovn.io/contact

### 2.5. useProxy

リバースプロキシ使用時は wovnjava に適切なホスト名が渡されず、翻訳ページのデータを取得できない場合があります。
useProxy に true を設定すると、wovnjava の処理に HTTP リクエストヘッダの X-Forwarded-Host を使用します。

### 2.6. debugMode

debugMode に 1 を設定すると、wovnjava はデバッグログを出力します。これは開発用の機能です。

### 2.7. originalUrlHeader, originalQueryStringHeader

Apache HTTP Server の mod_rewrite モジュールなどを使用して URL を書き換えている場合、wovnjava には書き換え前の URL が渡されず、適切な翻訳データを取得できない場合があります。

originalUrlHeader、originalQueryStringHeader を設定した場合、wovnjava はこれらに設定されたリクエストヘッダの値を翻訳データの取得に利用します。

下記の Apache HTTP Server の設定で、書き換え前の URL をリクエストヘッダに設定した場合、

```
SetEnvIf Request_URI "^(.*)$" REQUEST_URI=$1
RequestHeader set X-Request-Uri "%{REQUEST_URI}e"
RewriteRule .* - [E=REQUEST_QUERY_STRING:%{QUERY_STRING}]
RequestHeader set X-Query-String "%{REQUEST_QUERY_STRING}e"
```

wovnjava は下記の設定で書き換え前の URL を使って、正しい翻訳データを取得できます。

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

※ 上記のリクエストヘッダ設定のサンプルは、下記ページから引用しています。

https://coderwall.com/p/jhkw7w/passing-request-uri-into-request-header
