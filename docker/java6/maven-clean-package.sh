#!/bin/bash

cd `dirname $0`

# when if local compiled library exists, that library use with compile.
if [ -e ../../target/wovnjava-1.0.1-jdk6-jar-with-dependencies.jar ]; then
    mkdir -p hello/lib
    cp ../../target/wovnjava-1.0.1-jdk6-jar-with-dependencies.jar hello/lib/
fi

docker network create wovnjava6-maven_default


proxy_running=$(docker ps --filter "name=wovnjava-maven-proxy" -q)
if [ -z "$proxy_running" ]; then
    docker run -d --net wovnjava6-maven_default \
           --name wovnjava-maven-proxy \
           wiwai/cci-maven-proxy:3-jdk6
fi

sleep 1

docker run -it --rm --net wovnjava6-maven_default \
       -e "PROXY_HOST=wovnjava-maven-proxy" \
       -v wovnjava6-maven_repo:/root/.m2 \
       -v $(pwd)/hello:/project \
       -w /project wiwai/cci-maven:3-jdk6 mvn -e package
