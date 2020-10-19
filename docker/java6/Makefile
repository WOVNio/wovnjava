MAKEFLAGS    += --warn-undefined-variables
SHELL        := /bin/bash
.SHELLFLAGS  := -eu -o pipefail -c

USE_LOCAL_BUILD ?= no
WOVN_VERSION    ?= 1.0.1-jdk6
PROJECT_NAME     = hello
PROJECT_LIB_PATH = $(PROJECT_NAME)/src/main/webapp/WEB-INF/lib

POM_PATH = -f pom.xml
ifeq ($(USE_LOCAL_BUILD),yes)
	POM_PATH = -f pom.local.xml
endif

MAVEN_PROXY_NAME   = wovnjava6-maven-proxy
MAVEN_NETWORK_NAME = wovnjava6-maven_default
MAVEN_VOLUME_NAME  = wovnjava6-maven_repo

MAVEN_PROXY_STATE   = $(shell docker ps -a --filter "name=$(MAVEN_PROXY_NAME)" -q)
MAVEN_NETWORK_STATE = $(shell docker network ls --filter "name=$(MAVEN_NETWORK_NAME)" -q)
MAVEN_VOLUME_STATE  = $(shell docker volume ls --filter "name=$(MAVEN_VOLUME_NAME)" -q)

MAVEN = docker run -it --rm --net $(MAVEN_NETWORK_NAME) \
	--env "PROXY_HOST=$(MAVEN_PROXY_NAME)" \
	-v ${MAVEN_VOLUME_NAME}:/root/.m2 \
	-v ${PWD}/${PROJECT_NAME}:/project \
	-w /project wiwai/cci-maven:3-jdk6 mvn $(POM_PATH)

.PHONY: all
all: clean build

.PHONY: clean
clean:
	rm -rf ${PROJECT_NAME}/target
ifneq ($(MAVEN_PROXY_STATE),)
	docker rm -f ${MAVEN_PROXY_NAME}
endif
ifneq ($(MAVEN_NETWORK_STATE),)
	-docker network rm ${MAVEN_NETWORK_NAME}
endif
ifneq ($(MAVEN_VOLUME_STATE),)
	docker volume rm -f ${MAVEN_VOLUME_NAME}
endif

network:
	-docker network create $(MAVEN_NETWORK_NAME)

.PHONY: proxy
proxy: network
	-docker run -d --net $(MAVEN_NETWORK_NAME) \
		--name $(MAVEN_PROXY_NAME) \
		wiwai/cci-maven-proxy:3-jdk6

$(PROJECT_LIB_PATH)/wovnjava-$(WOVN_VERSION)-jar-with-dependencies.jar:
ifeq ($(USE_LOCAL_BUILD),yes)
	mkdir -p $(PROJECT_LIB_PATH)
	cp ../../target/wovnjava-$(WOVN_VERSION)-jar-with-dependencies.jar $(PROJECT_LIB_PATH)/wovnjava-$(WOVN_VERSION)-jar-with-dependencies.jar
endif

.PHONY: compile
compile: proxy $(PROJECT_LIB_PATH)/wovnjava-$(WOVN_VERSION)-jar-with-dependencies.jar
	$(MAVEN) compile

.PHONY: test
test: proxy $(PROJECT_LIB_PATH)/wovnjava-$(WOVN_VERSION)-jar-with-dependencies.jar
	$(MAVEN) test

.PHONY: build
build: proxy $(PROJECT_LIB_PATH)/wovnjava-$(WOVN_VERSION)-jar-with-dependencies.jar
	$(MAVEN) package

.PHONY: package
package: proxy $(PROJECT_LIB_PATH)/wovnjava-$(WOVN_VERSION)-jar-with-dependencies.jar
	$(MAVEN) package
