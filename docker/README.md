# Example of Docker

This is a simple example from Docker using the WOVN.java library.
Steps of run to Hello WOVN.java.

1. Compile
2. Start docker-compose
3. Create your wovn project
4. Change project token in web.xml
5. Re-Compile
6. Restart Tomcat

## 1. Compile

```
mave-clean-package.sh
```

## 2. Start docker-compose

This is using ngrok, ngrok allows you to expose a web server running on your local machine to the internet.

```
docker-compose up -d
```

Access http://127.0.0.1:4040/ , and check published public URL.

## 3. Create your wovn project

Go to https://wovn.io/ , and create a project with the issued URL.

## 4. Change project token in web.xml

Change your project token in `hello/src/main/webapp/WEB-INF/web.xml`.

## 5. Re-Compile

```
mave-clean-package.sh
```

## 6. Restart Tomcat

Restart and access issued URL.

```
docker-compose restart tomcat-jdk8
```

## Remove this docker containers

```
docker-compose down -v
docker volume rm wovnjava-maven_repo
```
