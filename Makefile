.PHONY: test
test: 
	./maven3-jdk6.sh test

.PHONY: clean
clean: 
	./maven3-jdk6.sh clean

.PHONY: build
build:
	./maven3-jdk6.sh package

.PHONY: start_local
start_local:
	cd docker/java6; make USE_LOCAL_BUILD=yes WOVN_VERSION=1.0.8-jdk6 && docker-compose up; cd -;
