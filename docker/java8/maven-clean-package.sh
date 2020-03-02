#!/bin/bash

cd `dirname $0`/hello

docker run -it --rm -v "$(pwd)":/project -v wovnjava-maven_repo:/root/.m2 -w /project maven:3-jdk-8 mvn clean package
