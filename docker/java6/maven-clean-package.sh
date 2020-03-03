#!/bin/bash

cd `dirname $0`

docker network create wovnjava6-maven_default

docker run -d --net wovnjava6-maven_default \
       -v $(pwd)/conf/squid.conf:/etc/squid/squid.conf \
       -v $(pwd)/conf/squid_myCA.pem:/etc/squid/squid_myCA.pem \
       --name wovnjava-maven-proxy \
       iwai/squid-with-openssl

sleep 1

docker run -it --rm --net wovnjava6-maven_default \
       -v $(pwd):/project \
       -v wovnjava6-maven_repo:/root/.m2 \
       -v $(pwd)/conf/settings.xml:/usr/share/maven/conf/settings.xml \
       -w /project wovnjava6-maven mvn $@

docker stop wovnjava-maven-proxy \
    && docker rm wovnjava-maven-proxy \
    && docker volume rm wovnjava6-maven_repo \
    && docker network rm wovnjava6-maven_default
