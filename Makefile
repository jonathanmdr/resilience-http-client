.PHONY: clean
clean:
	@mvn clean

.PHONY: install
install:
	@mvn clean install -Dmaven.test.skip=true

.PHONY: test
test:
	@mvn test

.PHONY: version
version:
	@mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$1 -DprocessAllModules

.PHONY: build
build:
	@docker build -t order-manager:latest .

.PHONY: up
up:
	@docker-compose --profile stack services up -d

.PHONY: up-stack
up-stack:
	@docker-compose --profile stack up -d

.PHONY: up-services
up-services:
	@docker-compose --profile services up -d

.PHONY: down
down:
	@docker-compose --profile stack services down --remove-orphans --volumes

.PHONY: down-stack
down-stack:
	@docker-compose --profile stack down --remove-orphans --volumes

.PHONY: down-services
down-services:
	@docker-compose --profile services down --remove-orphans --volumes

.PHONY: restart
restart:
	@docker-compose --profile stack services restart

.PHONY: restart-stack
restart-stack:
	@docker-compose --profile stack restart

.PHONY: restart-services
restart-services:
	@docker-compose --profile services restart

.PHONY: otel-agent
otel-agent:
	@echo "Update local version for OTEL Java Agent..."
	@mkdir -p .otel-dev
	@curl -o .otel-dev/otel.jar -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
	@curl --progress-bar -sL https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases | grep -oE 'releases/tag/v[0-9]+\.[0-9]+\.[0-9]+' | cut -d'/' -f3 | sort -V | tail -n 1 > .otel-dev/version.txt
	@VERSION=$$(cat .otel-dev/version.txt) && echo "OTEL Java Agent local was updated to $$VERSION"
	@rm -rf .otel-dev/version.txt

.PHONY: bulkhead
bulkhead:
	@echo "\nStarting bulkhead with order_id=$(order_id) and concurrency=$(concurrency)\n\n"
	@seq $(concurrency) | xargs -n 1 -P $(concurrency) -I {} sh -c '\
		curl --progress-bar -i -X POST "http://localhost:8080/v1/orders/$(order_id)/authorize" -H "accept: application/json" -d "" 2>/dev/null \
		| awk -v RS="" -v OFS="\n\n" "/HTTP\\/1.1/ {print; next} {gsub(/^\\n|\\n$$/, \"\"); print}" >> output_$(order_id).txt \
	'
	@cat output_$(order_id).txt
	@rm output_$(order_id).txt
	@echo "\n\nBulkhead finished"

