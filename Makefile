.PHONY: clean install test version up down restart

clean:
	@mvn clean

install:
	@mvn clean install

test:
	@mvn clean test

version:
	@mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$1

up:
	@docker-compose up -d

down:
	@docker-compose down --remove-orphans --volumes

restart:
	@docker-compose restart
