MAKEFLAGS    += --warn-undefined-variables
SHELL        := /bin/bash
.SHELLFLAGS  := -eu -o pipefail -c


VERSION := 8
MAVEN    = docker run -it --rm -v ${PWD}:/project -v wovnjava-maven_repo:/root/.m2 -w /project maven:3-jdk-$(VERSION) mvn

.PHONY: all
all: clean build

.PHONY: clean
clean:
	$(MAVEN) clean -f pom.jdk$(VERSION).xml

.PHONY: build
build:
	$(MAVEN) package -f pom.jdk$(VERSION).xml

.PHONY: test
test:
	$(MAVEN) test -f pom.jdk$(VERSION).xml
