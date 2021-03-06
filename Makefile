MAKEFLAGS    += --warn-undefined-variables
SHELL        := /bin/bash
.SHELLFLAGS  := -eu -o pipefail -c

VERSION := 8
WOVN_VERSION := 1.9.1
TARGET_DIR = ${PWD}
MAVEN    = docker run -it --rm -v ${TARGET_DIR}:/project -v wovnjava-maven_repo:/root/.m2 -w /project maven:3-jdk-$(VERSION) mvn
WEBSITE_CONFIG_FILE = pom.xml
# WEBSITE_CONFIG_FILE = pom_jitpack.xml

.PHONY: all clean build test start stop build_website build_wovn_java build_wovn_java_and_website restart
all: clean build

clean:
	$(MAVEN) clean -f pom.jdk$(VERSION).xml

build:
	$(MAVEN) package -f pom.jdk$(VERSION).xml -Dmaven.test.skip

test:
	$(MAVEN) test -f pom.jdk$(VERSION).xml

start:
	docker-compose -f docker/java$(VERSION)/docker-compose.yml up

stop:
	docker-compose -f docker/java$(VERSION)/docker-compose.yml rm -sf

build_website:
	$(eval TARGET_DIR := ${PWD}/docker/java$(VERSION)/hello)
	$(MAVEN) clean package -f $(WEBSITE_CONFIG_FILE)

build_wovn_java:
	make clean
	make build
	mkdir -p ./docker/java$(VERSION)/hello/src/main/webapp/WEB-INF/lib
	cp ./target/wovnjava-$(WOVN_VERSION)*.jar ./docker/java$(VERSION)/hello/src/main/webapp/WEB-INF/lib

build_wovn_java_and_website:
	make build_wovn_java
	make build_website

restart:
	docker-compose -f docker/java$(VERSION)/docker-compose.yml restart tomcat-jdk8