#!/bin/bash

cd `dirname $0`

make proxy

sleep 1

docker run -it --rm --net wovnjava6-maven_default \
       -e "PROXY_HOST=wovnjava6-maven-proxy" \
       -v wovnjava6-maven_repo:/root/.m2 \
       -v $(pwd)/hello:/project \
       -w /project wiwai/cci-maven:3-jdk6 mvn $@
