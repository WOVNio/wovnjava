#!/bin/bash

cd `dirname $0`

docker network create wovnjava6-maven_default

proxy_running=$(docker ps --filter "name=wovnjava6-maven-proxy" -q)
if [ -z "$proxy_running" ]; then
    docker run -d --net wovnjava6-maven_default \
           --name wovnjava6-maven-proxy \
           wiwai/cci-maven-proxy:3-jdk6
fi

sleep 1

docker run -it --rm --net wovnjava6-maven_default \
       -e "PROXY_HOST=wovnjava6-maven-proxy" \
       -v wovnjava6-maven_repo:/root/.m2 \
       -v $(pwd):/project \
       -w /project wiwai/cci-maven:3-jdk6 mvn $@

artifact=wovnjava-1.0.1-jdk6-jar-with-dependencies.jar

if [ -e "target/$artifact" ]; then
    # Install to maven local repository
    docker run -it --rm --net wovnjava6-maven_default \
           -e "PROXY_HOST=wovnjava6-maven-proxy" \
           -v wovnjava6-maven_repo:/root/.m2 \
           -v $(pwd):/project \
           -w /project wiwai/cci-maven:3-jdk6 mvn install:install-file -Dfile=target/$artifact -DpomFile=pom.xml
fi
