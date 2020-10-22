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

Copy the locally compiled wovnjava to the lib directory of the Hello project.
Compile the Hello project together with wovnjava.

```
mkdir -p docker/java6/hello/src/main/webapp/WEB-INF/lib/
cp target/wovnjava-1.0.1-jdk6-jar-with-dependencies.jar docker/java6/hello/src/main/webapp/WEB-INF/lib/

cd docker/java6
./maven-clean-package.sh
```

Or

```
cd docker/java6
make USE_LOCAL_BUILD=yes WOVN_VERSION=1.0.2-jdk6
```

See docker/README.md for the rest.
