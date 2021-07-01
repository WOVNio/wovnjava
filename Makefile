MAKEFLAGS    += --warn-undefined-variables
SHELL        := /bin/bash
.SHELLFLAGS  := -eu -o pipefail -c

VERSION := 8
TARGET_DIR = ${PWD}
MAVEN    = docker run -it --rm -v ${TARGET_DIR}:/project -v wovnjava-maven_repo:/root/.m2 -w /project maven:3-jdk-$(VERSION) mvn
WEBSITE_CONFIG_FILE = pom.xml
# WEBSITE_CONFIG_FILE = pom_jitpack.xml

.PHONY: all clean build test start stop build_webisite build_wovn_java build_webisite_with_loacal_wovn_java restart
all: clean build

clean:
	$(MAVEN) clean -f pom.jdk$(VERSION).xml

build:
	$(MAVEN) package -f pom.jdk$(VERSION).xml

test:
	$(MAVEN) test -f pom.jdk$(VERSION).xml

start:
	docker-compose -f docker/java$(VERSION)/docker-compose.yml up -d

stop:
	docker-compose -f docker/java$(VERSION)/docker-compose.yml rm -sf

build_webisite:
	$(eval TARGET_DIR := ${PWD}/docker/java$(VERSION)/hello)
	$(MAVEN) clean package -f $(WEBSITE_CONFIG_FILE)

build_wovn_java:
	make clean
	make build
	mkdir -p ./docker/java$(VERSION)/hello/src/main/webapp/WEB-INF/lib
	cp ./target/wovnjava-1.$(VERSION).0*.jar ./docker/java$(VERSION)/hello/src/main/webapp/WEB-INF/lib

build_webisite_with_loacal_wovn_java:
	make build_wovn_java
	$(eval TARGET_DIR := ${PWD}/docker/java$(VERSION)/hello)
	$(MAVEN) clean package -f pom.xml

restart:
	docker-compose -f docker/java$(VERSION)/docker-compose.yml restart tomcat-jdk8