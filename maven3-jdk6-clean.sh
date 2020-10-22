#!/bin/bash

echo "Cleaning docker containers.."
docker stop wovnjava6-maven-proxy \
    && docker rm wovnjava6-maven-proxy
docker volume rm wovnjava6-maven_repo
docker network rm wovnjava6-maven_default
