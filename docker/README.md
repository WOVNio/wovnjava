# Release
## Release steps
1. Update `version` in pom.xml on root directory.
2. Make release in GitHub.
3. JitPack builds wovnjava library. (https://jitpack.io/#wovnio/wovnjava)  

## JitPack
Our wovnjava repogitory is registered in JitPack, and JitPack is monitoring our repogitory.  
When new release is created, JitPack will automatically build wovnjava like the followings.  
https://jitpack.io/com/github/wovnio/wovnjava/1.8.0/wovnjava-1.8.0.jar
https://jitpack.io/com/github/wovnio/wovnjava/1.8.0/wovnjava-1.8.0-jar-with-dependencies.jar

`wovnjava-XXX.jar` doesn't include depencencies.  
`wovnjava-XXX-jar-with-dependencies.jar` includes devepdencies.

## Branch for other versions
There are branches for Java6 and Java7.  
https://github.com/WOVNio/wovnjava/tree/java6_support
https://github.com/WOVNio/wovnjava/tree/java7_support

The big difference is version for `source` and `target`.
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

# Start using your local environment
To build local docker environemnt, this repogitory is using the following three docker images.
1. andreptb/tomcat: Tomcat is software to run Java Servlet
2. maven: Maven is software to manage and build Java Servlet
3. ngrok: This is utility tool to make your local website accessible from the outside

You can start using local environment with make command in Makefile.  
If you want to change the version of Java, you can change `VERSION` in Makefile.

## 1. Set your configuration
`docker/java8/hello/src/main/webapp/WEB-INF/web.xml` is configuration file for your wovnjava.  
Change configuration like project token.

Add the following configuration, If you want to use local translation API and local widget.
```
<init-param>
    <param-name>devMode</param-name>
    <param-value>true</param-value>
</init-param>
```

## 2. Compile your local wovnjava
```
make build_wovn_java_and_website
```
This command run the followings.
- Build your local wovnjava will be created
- Copy it to your website directory
- Build your local website

You will see that `docker/java8/hello/target` is created depends on `docker/java8/hello/src/main/webapp`.  

## 3. Start Tomcat
```
make start
```
This command start tomcat to serve your website.  
Go to http://localhost:8080 , then you can see your website.

## Change your website
When you want to change your website, change `docker/java8/hello/src/main/webapp`.  
The following command rebuilds your website, and restart tomcat to apply them.
```
make build_webisite && make restart
```

## Change local wovnjava
After you change local wovnjava, the following command rebuilds wovnjava, and restart tomcat.
```
make build_wovn_java_and_website && make restart
```

## Stop your local environment
```
make stop
```
Stop local environment and remove docker.

## Expose your website with Ngrok
If you want to expose your website, you can use Ngrok.
Access http://127.0.0.1:4040/ , and check published public URL.

## Log
You can see Tomcat logs at `docker/java8/logs`.

## Run command inside docker
```
docker exec -it wovnjava-tomcat-jdk8 sh
```

## Use published wovnjava in your local
`docker/java8/hello/pom_jitpack.xml` is the configuration file to use published wovnjava with your local website.
- Change makefile to `WEBSITE_CONFIG_FILE = pom_jitpack.xml`
- Build website with command `make build_webisite`
- Start tomcat with command `make restart`