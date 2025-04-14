#!/bin/bash

set -e

register_connector() {
  local name=$1
  local config=$2

  echo "Registering connector: $name"
  curl --request POST \
    --url http://localhost:8083/connectors \
    --header 'Accept: application/json' \
    --header 'Content-Type: application/json' \
    --data "$config"
  echo -e "\nConnector $name registered.\n"
}

AUTHORIZATIONS_CONNECTOR_CONFIG='{
  "name": "authorizations-source-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "mysql-authorizations",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "root",
    "database.server.id": "10000",
    "database.include.list": "authorizations",
    "table.ignore.broken.offsets": "true",
    "table.include.list": "authorizations.authorizations",
    "include.schema.changes": "false",
    "schema.history.internal.kafka.bootstrap.servers": "kafka-broker:19092",
    "schema.history.internal.kafka.topic": "connect-cdc-schema-history",
    "topic.prefix": "authorizations",
    "transforms": "RenameTopic",
    "transforms.RenameTopic.type": "org.apache.kafka.connect.transforms.RegexRouter",
    "transforms.RenameTopic.regex": ".*",
    "transforms.RenameTopic.replacement": "connect-cdc-authorization-events",
    "snapshot.mode": "initial",
    "max.batch.size": "2048",
    "max.queue.size": "8192",
    "poll.interval.ms": "1000",
    "connect.timeout.ms": "30000",
    "tombstones.on.delete": "false"
  }
}'

ORDERS_CONNECTOR_CONFIG='{
  "name": "orders-source-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "mysql-orders",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "root",
    "database.server.id": "10001",
    "database.include.list": "orders",
    "table.ignore.broken.offsets": "true",
    "table.include.list": "orders.orders",
    "include.schema.changes": "false",
    "schema.history.internal.kafka.bootstrap.servers": "kafka-broker:19092",
    "schema.history.internal.kafka.topic": "connect-cdc-schema-history",
    "topic.prefix": "orders",
    "transforms": "RenameTopic",
    "transforms.RenameTopic.type": "org.apache.kafka.connect.transforms.RegexRouter",
    "transforms.RenameTopic.regex": ".*",
    "transforms.RenameTopic.replacement": "connect-cdc-order-events",
    "snapshot.mode": "initial",
    "max.batch.size": "2048",
    "max.queue.size": "8192",
    "poll.interval.ms": "1000",
    "connect.timeout.ms": "30000",
    "tombstones.on.delete": "false"
  }
}'

register_connector "authorizations-source-connector" "$AUTHORIZATIONS_CONNECTOR_CONFIG"
register_connector "orders-source-connector" "$ORDERS_CONNECTOR_CONFIG"

echo "All connectors registered successfully."