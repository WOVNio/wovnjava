# リリース
## リリース手順
1. ルートディレクトリ内のpom.xmlの`version`を更新します。
2. GitHubのReleaseを作成します。
3. JitPackがwovnjavaをビルドします。 (https://jitpack.io/#wovnio/wovnjava)

## JitPack
wovnjavaのリポジトリはJitPackに登録されており、リポジトリはJitPackに監視されています。  
新しいReleaseが作成されると、JitPackは自動で以下のようにwovnjavaをビルドします。  
- https://jitpack.io/com/github/wovnio/wovnjava/1.8.0/wovnjava-1.8.0.jar  
- https://jitpack.io/com/github/wovnio/wovnjava/1.8.0/wovnjava-1.8.0-jar-with-dependencies.jar

`wovnjava-XXX.jar`は依存関係を含んでいません。  
`wovnjava-XXX-jar-with-dependencies.jar`は依存関係を含んでいます。

## 別バージョンのブランチ
Java6とJava7用のブランチが存在します。  
- https://github.com/WOVNio/wovnjava/tree/java6_support
- https://github.com/WOVNio/wovnjava/tree/java7_support

大きな違いは`source`と`target`のバージョンです。
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.5.1</version>
    <configuration>
        <source>1.6</source>
        <target>1.6</target>
        <encoding>UTF-8</encoding>
    </configuration>
</plugin>
```

# ローカル環境の使用
ローカルDocker環境を構築するために、このリポジトリでは以下の３つのDockerイメージを使用しています。
1. andreptb/tomcat: TomcatはJava Servletを実行するソフトウェアです
2. maven: MavenはJavaServletを管理・ビルドするためのソフトウェアです
3. ngrok: ローカルウェブサイトを外部からアクセス可能にする便利ツールです

Makefile内に存在するmakeコマンドを使用することで、ローカル環境を使用できます。  
もしJavaのバージョンを変更したい場合は、Makefileの`VERSION`を変更してください。

## 1. 設定
`docker/java8/hello/src/main/webapp/WEB-INF/web.xml` はwovnjavaの設定ファイルです。  
トークンなど設定を変更してください。  

もしローカルの次の用に設定を追加すると、ローカルのtranslationAPIとWidgetを使用します。  
```
<init-param>
    <param-name>debugMode</param-name>
    <param-value>true</param-value>
</init-param>
```

## 2. ローカルwovnjavaのコンパイル
```
make build_webisite_with_loacal_wovn_java
```
このコマンドは次の事を実行します。  
- ローカルwovnjavaをビルドする
- wovnjavaをローカルウェブサイトにコピーする
- ローカルウェブサイトをビルドする

`docker/java8/hello/target`ディレクトリに、`docker/java8/hello/src/main/webapp`を元にしてファイルが作成されるのが確認できるはずです。

## 3. start tomcat
```
make start
```
このコマンドは、コンパイルされたファイルをTomcatで配信開始します。
http://localhost:8080 にアクセスすると、あなたのウェブサイトが見れるはずです。

## ウェブサイトの変更
もしあなたのウェブサイトを変更したい場合、`docker/java8/hello/src/main/webapp`を変更してください。    
次のコマンドは、あなたの変更を再ビルドし、Tomcatに適用します。
```
make build_webisite && make restart
```

## ローカルwovnjavaの変更
ローカルのwovnjavaを変更した後、次のコマンドでwovnjavaをビルドしTomcatを再起動します。  
```
make build_webisite_with_loacal_wovn_java && make restart
```

## ローカル環境を停止する
```
make stop
```
ローカル環境を停止して、Dockerを削除します。

## Expose your website with Ngrok
もし外部にあなたのウェブサイトを公開したい場合に、Ngrokを使用してください。  
http://127.0.0.1:4040/ にアクセスし、公開されたURLを確認してください。

## Log
Tomcatのログは`docker/java8/logs`で確認可能です。

## Docker内でコマンドを実行したい
```
docker exec -it wovnjava-tomcat-jdk8 sh
```

## Use published wovnjava in your local
`docker/java8/hello/pom_jitpack.xml`は公開されたwovnjavaをウェブサイトで使用するための設定ファイルです。  
- Makefileの設定を`WEBSITE_CONFIG_FILE = pom_jitpack.xml`に変更する
- `make build_webisite`コマンドでサイトをビルドする
- `make restart`コマンドでTomcatを起動する