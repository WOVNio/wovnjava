# For developers

## Compile

To compile with jdk6, use the script `maven3-jdk6.sh`.
The script `maven3-jdk6.sh` is a wrapper for the mvn command.

```
./maven3-jdk6.sh clean package
```

The artifacts will be generated in the target directory.

```
target/wovnjava-1.0.1-jdk6.jar
target/wovnjava-1.0.1-jdk6-jar-with-dependencies.jar
```

## Test

```
./maven3-jdk6.sh test
```

## How to use it in docker

If you want to use a locally compiled version of wovnjava, please use the following method.


Remove or comment out the repository configuration in jitpack.io and enable the dependency systemPath instead.

docker/java6/hello/pom.xml:

```
  <repositories>

      <!-- Remove or comment out
      <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
          <snapshots>
              <enabled>true</enabled>
              <updatePolicy>always</updatePolicy>
          </snapshots>
      </repository>
      -->

      <!-- If you use local repository -->
      <!-- <repository>
           <id>local-maven-repository</id>
           <url>file:/root/.m2/repository</url>
           </repository> -->

  </repositories>
```

Make sure you have the correct version number enabled in dependency.

docker/java6/hello/pom.xml:

```
    <dependency>
      <groupId>com.github.wovnio</groupId>
      <artifactId>wovnjava</artifactId>
      <version>1.0.1-jdk6</version>
      <!-- If you use local compiled version. -->
      <scope>system</scope>
      <systemPath>${basedir}/lib/wovnjava-1.0.1-jdk6-jar-with-dependencies.jar</systemPath>
    </dependency>
```

Copy the locally compiled wovnjava to the lib directory of the Hello project.
Compile the Hello project together with wovnjava.

```
mkdir -p docker/java6/hello/src/main/webapp/WEB-INF/lib/
cp target/wovnjava-1.0.1-jdk6-jar-with-dependencies.jar docker/java6/hello/src/main/webapp/WEB-INF/lib/

cd docker/java6
./maven-clean-package.sh
```

See docker/README.md for the rest.
