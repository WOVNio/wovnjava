#!/bin/bash

echo "Cleaning docker containers.."
docker stop wovnjava-maven-proxy \
    && docker rm wovnjava-maven-proxy \
    && docker volume rm wovnjava6-maven_repo \
    && docker network rm wovnjava6-maven_default
