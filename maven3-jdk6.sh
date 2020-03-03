#!/bin/bash

cd `dirname $0`

BUILD_IMG=$(docker images -q --filter "reference=wovnjava6-maven")
if [ -z $BUILD_IMG ]; then
    docker build -t wovnjava6-maven ./docker/java6
fi

docker network create wovnjava6-maven_default

docker run -d --net wovnjava6-maven_default \
       -v $(pwd)/docker/java6/conf/squid.conf:/etc/squid/squid.conf \
       -v $(pwd)/docker/java6/conf/squid_myCA.pem:/etc/squid/squid_myCA.pem \
       --name wovnjava-maven-proxy \
       iwai/squid-with-openssl

sleep 1

docker run -it --rm --net wovnjava6-maven_default \
       -v $(pwd):/project \
       -v wovnjava6-maven_repo:/root/.m2 \
       -v $(pwd)/docker/java6/conf/settings.xml:/usr/share/maven/conf/settings.xml \
       -w /project wovnjava6-maven mvn $@

echo "Cleaning docker containers.."
docker stop wovnjava-maven-proxy \
    && docker rm wovnjava-maven-proxy \
    && docker volume rm wovnjava6-maven_repo \
    && docker network rm wovnjava6-maven_default
