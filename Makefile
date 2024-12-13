.PHONY: clean install test version up down restart bulkhead

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

bulkhead:
	@echo "\nStarting bulkhead with order_id=$(order_id) and concurrency=$(concurrency)\n\n"
	@seq $(concurrency) | xargs -n 1 -P $(concurrency) -I {} sh -c '\
		curl -i -X POST "http://localhost:8080/v1/orders/$(order_id)/authorize" -H "accept: application/json" -d "" 2>/dev/null \
		| awk -v RS="" -v OFS="\n\n" "/HTTP\\/1.1/ {print; next} {gsub(/^\\n|\\n$$/, \"\"); print}" >> output_$(order_id).txt \
	'
	@cat output_$(order_id).txt
	@rm output_$(order_id).txt
	@echo "\n\nBulkhead finished"

