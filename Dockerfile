FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /build

COPY pom.xml pom.xml
COPY domain domain
COPY application application
COPY coverage coverage
COPY order-api order-api
COPY order-worker order-worker
COPY authorization-api authorization-api

RUN mvn package -DskipTests --batch-mode

FROM azul/zulu-openjdk:21 AS release

WORKDIR /app

COPY --from=build /build/order-api/target/*.jar order-api.jar
COPY --from=build /build/order-worker/target/*.jar order-worker.jar
COPY --from=build /build/authorization-api/target/*.jar authorization-api.jar
COPY .otel-dev/otel.jar .
COPY docker-entrypoint.sh .

RUN useradd ecommerce && \
    chmod +x docker-entrypoint.sh

USER ecommerce

ENTRYPOINT ["/app/docker-entrypoint.sh"]