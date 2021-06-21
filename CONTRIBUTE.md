# For developers

## Compile

To compile, just run the make command. (default is jdk8)

```
make
```

If you want to change the version of the jdk, just pass the VERSION argument to the make command.
The following is an example of compiling with jdk7.

```
make VERSION=7
```

Note: If you are compiling for jdk6, please check the java6_support branch.

## Test

```
make test
```

## More

If you want to use the `mvn` command directly, you can register what you are doing in Makefile to Bash's function as follows and do what you want.

```bash
wovnjava-mvn () {
  docker run -it --rm -v ${PWD}:/project -v wovnjava-maven_repo:/root/.m2 -w /project maven:3-jdk-8 mvn $@
}


wovnjava-mvn build -f pom.xml

wovnjava-mvn test -f pom.xml
```

## How to use it in docker

### Using a development branch

This is the recommended way of evaluating a development copy of wovnjava. You need to have your development branch pushed to Github. Then, specify the branch you want to use in the `pom.xml` file by using the following format:

```
<version>branchname-SNAPSHOT</version>
```

Replace `branchname` with the actual branch name (`/` in branch names should be replaced with `~`.)

Then build the docker envrionment as usual.

### Using a locally compiled version

If you want to use a locally compiled version of wovnjava, please use the following method.

Remove or comment out the repository configuration in jitpack.io and enable the dependency systemPath instead.

docker/java8/hello/pom.xml:

```
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
      </snapshots>
      <!-- end -->
    </repository>
  </repositories>
```

Make sure you have the correct version number enabled in dependency.

docker/java8/hello/pom.xml:

```
  <dependencies>
      <dependency>
          <groupId>com.github.wovnio</groupId>
          <artifactId>wovnjava</artifactId>
          <version>1.4.0</version>
          <!-- If you use local compiled version. -->
          <scope>system</scope>
          <systemPath>${basedir}/lib/wovnjava-1.4.0-jar-with-dependencies.jar</systemPath>
      </dependency>
  </dependencies>
```

Copy the locally compiled wovnjava to the lib directory of the Hello project.
Compile the Hello project together with wovnjava.

```
mkdir -p docker/java8/hello/src/main/webapp/WEB-INF/lib/
cp target/wovnjava-1.4.0-jar-with-dependencies.jar docker/java8/hello/src/main/webapp/WEB-INF/lib/

cd docker/java8
./maven-clean-package.sh
```

For more information on how to use the docker environment, please refer to the following

[docker/README.md](docker/README.md)

