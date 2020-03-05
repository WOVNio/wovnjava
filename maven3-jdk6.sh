#!/bin/bash

cd `dirname $0`

docker network create wovnjava6-maven_default

docker run -d --net wovnjava6-maven_default \
       --name wovnjava-maven-proxy \
       wiwai/cci-maven-proxy:3-jdk6

sleep 1

docker run -it --rm --net wovnjava6-maven_default \
       -e "PROXY_HOST=wovnjava-maven-proxy" \
       -v wovnjava6-maven_repo:/root/.m2 \
       -v $(pwd):/project \
       -w /project wiwai/cci-maven:3-jdk6 mvn $@

echo "Cleaning docker containers.."
docker stop wovnjava-maven-proxy \
    && docker rm wovnjava-maven-proxy \
    && docker volume rm wovnjava6-maven_repo \
    && docker network rm wovnjava6-maven_default
