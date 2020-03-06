# Example of Docker

これは、WOVN.javaライブラリを使用したDockerの簡単な例です。

動作のための手順は下記の通りです。

1. コンパイル
2. docker-compose起動
3. WOVNプロジェクトを作成
4. web.xmlのプロジェクトトークンを変更
5. 再コンパイル
6. Tomcat再起動

## 1. コンパイル

```
mave-clean-package.sh
```

## 2. docker-compose起動

これはngrokを使用しています。ngrokを使用すると、ローカルマシンで実行されているWebサーバーをインターネットに公開できます。

```
docker-compose up -d
```

http://127.0.0.1:4040/ にアクセスし、公開されている公開URLを確認します。

## 3. WOVNプロジェクトを作成

https://wovn.io/ にアクセスし、発行されたURLでプロジェクトを作成します。

## 4. web.xmlのプロジェクトトークンを変更

`hello/src/main/webapp/WEB-INF/web.xml` でプロジェクトトークンを変更します。

## 5. 再コンパイル

```
mave-clean-package.sh
```

## 6. Tomcat再起動

再起動して、発行されたURLにアクセスします。

```
docker-compose restart tomcat-jdk8
```

## Docker コンテナの削除

```
docker-compose down -v
docker volume rm wovnjava-maven_repo
```

