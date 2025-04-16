# Sample of Resilience Patterns for Services Integration
> Explanations about patterns applied on API, HTTP Client, and Pub/Sub integrations.

---

## Project Structure
![example of structure](.docs/img.png)

## Profiles
| Profile    | Description                                                                  |
|------------|------------------------------------------------------------------------------|
| `stack`    | Contains the stack of services that are used in the development environment. |
| `services` | Contains the APIs and Workers that are used in the development environment.  |

## Upping The Development Stack and Services Environment
```shell
# With docker-compose
docker-compose --profile stack --profile services up -d
```
```shell
# With Make
make up
```

## Upping The Development Stack Environment Only
```shell
# With docker-compose
docker-compose --profile stack up -d
```
```shell
# With Make
make up-stack
```

## Restarting The Development Stack and Services Environment
```shell
# With docker-compose
docker-compose --profile stack --profile services restart
```
```shell
# With Make
make restart
```

## Restarting The Development Stack Environment Only
```shell
# With docker-compose
docker-compose --profile stack restart
```
```shell
# With Make
make restart-stack
```

## Downing The Development Stack and Services Environment
```shell
# With docker-compose
docker-compose --profile stack --profile services down --remove-orphans --volumes
```
```shell
# With Make
make down
```

## Downing The Development Stack Environment Only
```shell
# With docker-compose
docker-compose --profile stack down --remove-orphans --volumes
```
```shell
# With Make
make down-stack
```

## Register CDC Source Connectors
> :information_source: _The connectors are not registered automatically when the stack is up. You should register them manually by running the following command bellow_
```shell
# With bash
./connectors.sh
```
```shell
# With Make
make connectors
```

## Downloading The OpenTelemetry Agent
```shell
make otel-agent
```
> :information_source: _Use the OTEL agent passing the java agent to the VM arguments: `-javaagent:.otel-dev/otel.jar`_
> 
> :information_source: _To enable custom metrics using OTEL registry via Micrometer, pass the following value using the VM arguments: `-Dotel.instrumentation.micrometer.enabled=true`_

## Open Telemetry Environment Variables Configuration

### order-api
```
OTEL_EXPORTER_OTLP_METRICS_COMPRESSION=gzip
OTEL_EXPORTER_OTLP_METRICS_ENDPOINT=http://localhost:4318/v1/metrics
OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE=cumulative
OTEL_EXPORTER_OTLP_TRACES_COMPRESSION=gzip
OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://localhost:4318/v1/traces
OTEL_METRICS_EXPORTER=otlp
OTEL_TRACES_EXPORTER=otlp
OTEL_LOGS_EXPORTER=none
OTEL_METRIC_EXPORT_INTERVAL=15000
OTEL_SERVICE_NAME=order-api
```

### authorization-api
```
OTEL_EXPORTER_OTLP_METRICS_COMPRESSION=gzip
OTEL_EXPORTER_OTLP_METRICS_ENDPOINT=http://localhost:4318/v1/metrics
OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE=cumulative
OTEL_EXPORTER_OTLP_TRACES_COMPRESSION=gzip
OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://localhost:4318/v1/traces
OTEL_METRICS_EXPORTER=otlp
OTEL_TRACES_EXPORTER=otlp
OTEL_LOGS_EXPORTER=none
OTEL_METRIC_EXPORT_INTERVAL=15000
OTEL_SERVICE_NAME=authorization-api
```

### order-worker
```
OTEL_EXPORTER_OTLP_METRICS_COMPRESSION=gzip
OTEL_EXPORTER_OTLP_METRICS_ENDPOINT=http://localhost:4318/v1/metrics
OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE=cumulative
OTEL_EXPORTER_OTLP_TRACES_COMPRESSION=gzip
OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://localhost:4318/v1/traces
OTEL_METRICS_EXPORTER=otlp
OTEL_TRACES_EXPORTER=otlp
OTEL_LOGS_EXPORTER=none
OTEL_METRIC_EXPORT_INTERVAL=15000
OTEL_SERVICE_NAME=order-worker
```

### audit-worker
```
OTEL_EXPORTER_OTLP_METRICS_COMPRESSION=gzip
OTEL_EXPORTER_OTLP_METRICS_ENDPOINT=http://localhost:4318/v1/metrics
OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE=cumulative
OTEL_EXPORTER_OTLP_TRACES_COMPRESSION=gzip
OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://localhost:4318/v1/traces
OTEL_METRICS_EXPORTER=otlp
OTEL_TRACES_EXPORTER=otlp
OTEL_LOGS_EXPORTER=none
OTEL_METRIC_EXPORT_INTERVAL=15000
OTEL_SERVICE_NAME=audit-worker
```

## Development Environment URLs
| Host                                         | Description                       | Profiles   |
|----------------------------------------------|-----------------------------------|------------|
| http://localhost:8080                        | OpenAPI Order API                 | `services` |
| http://localhost:8081                        | OpenAPI Authorization API         | `services` |
| `noop`                                       | Order Worker                      | `services` |
| `noop`                                       | Audit Worker                      | `services` |
| `jdbc:mysql://localhost:3306/orders`         | Order Database                    | `stack`    |
| `mongodb://root:root@localhost:27017/audit`  | Authorization CDC Database        | `stack`    |
| `jdbc:mysql://localhost:3307/authorizations` | Authorization Database            | `stack`    |
| http://localhost:8580                        | Kafka UI                          | `stack`    |
| http://localhost:8083                        | Kafka Connect API                 | `stack`    |
| http://localhost:4317                        | OpenTelemetry GRPC Collector Port | `stack`    |
| http://localhost:4318                        | OpenTelemetry HTTP Collector Port | `stack`    |
| http://localhost:16666                       | Jaeger                            | `stack`    |
| http://localhost:9090                        | Prometheus                        | `stack`    |
| http://localhost:3000                        | Grafana                           | `stack`    |


## Register the CDC Authorizations Source Connector
```shell
curl --request POST \
  --url http://localhost:8083/connectors \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{
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
```

## Register the CDC Orders Source Connector
```shell
curl --request POST \
  --url http://localhost:8083/connectors \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{
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
```