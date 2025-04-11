FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

COPY pom.xml pom.xml
COPY domain domain
COPY application application
COPY coverage coverage
COPY order-api order-api
COPY order-worker order-worker
COPY authorization-api authorization-api

RUN mvn package -Dmaven.test.skip=true --batch-mode

FROM eclipse-temurin:21-jre-alpine-3.21 AS release

WORKDIR /app

COPY --from=build /build/order-api/target/*.jar order-api.jar
COPY --from=build /build/order-worker/target/*.jar order-worker.jar
COPY --from=build /build/authorization-api/target/*.jar authorization-api.jar
COPY .otel-dev/otel.jar .
COPY docker-entrypoint.sh .
COPY healthcheck.sh .

RUN addgroup -S ecommerce && \
    adduser -S -G ecommerce ecommerce && \
    chmod +x docker-entrypoint.sh && \
    chmod +x healthcheck.sh && \
    apk add --no-cache curl jq && \
    rm -rf /var/cache/apk/* /tmp/* /var/tmp/*

USER ecommerce:ecommerce

ENTRYPOINT ["/app/docker-entrypoint.sh"]