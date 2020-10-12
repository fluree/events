
VERSION := $(shell mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null)
VERSION ?= SNAPSHOT

MAJOR_VERSION := $(shell echo $(VERSION) | cut -d '.' -f1)
MINOR_VERSION := $(shell echo $(VERSION) | cut -d '.' -f2)

.PHONY: deps test jar uberjar stage-release prep-release release release-stable release-latest release-version-latest docker-image clean

target/fluree-events.standalone.jar: pom.xml src/**/* resources/**/*
	clojure -A:uberjar

build/fluree-events-$(VERSION).zip: stage-release
	cd build && zip -r fluree-$(VERSION).zip *

stage-release: build/release-staging build/fluree-events.standalone.jar

build/release-staging:
	mkdir -p build/release-staging

build/fluree-events.standalone.jar: target/fluree-events.standalone.jar
	cp $< build/

target/fluree-events.jar: pom.xml src/**/* resources/**/*
	clojure -A:jar

uberjar: target/fluree-events.standalone.jar

jar: target/fluree-events.jar

docker-image: target/fluree-events.standalone.jar
	docker build -t fluree/events:$(VERSION) .

docker-push: docker-image
	docker push fluree/events:$(VERSION)

docker-push-latest: docker-push
	docker tag fluree/events:$(VERSION) fluree/events:latest
	docker push fluree/events:latest

clean:
	rm -rf build
	rm -rf target

deps: deps.edn
	clojure -Stree

test:
	clojure -A:test
