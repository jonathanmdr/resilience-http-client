package com.resilience.auditworker.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.resilience.auditworker.configuration.annotation.DefaultMongo;
import com.resilience.auditworker.configuration.annotation.PrimaryMongo;
import com.resilience.auditworker.configuration.annotation.RoutingMongo;
import com.resilience.auditworker.configuration.transaction.RoutingMongoDatabaseFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class RoutingMongoDatabaseConfiguration {

    private final String databaseName;
    private final String primaryUri;
    private final String defaultUri;

    public RoutingMongoDatabaseConfiguration(
        @Value("${database.schema-name}") final String databaseName,
        @Value("${database.configuration.primary.uri}") final String primaryUri,
        @Value("${database.configuration.default.uri}") final String defaultUri
    ) {
        this.databaseName = databaseName;
        this.primaryUri = primaryUri;
        this.defaultUri = defaultUri;
    }

    @PrimaryMongo
    @Bean
    public MongoClient primaryMongoClient() {
        return MongoClients.create(this.primaryUri);
    }

    @DefaultMongo
    @Bean
    public MongoClient defaultMongoClient() {
        return MongoClients.create(this.defaultUri);
    }

    @PrimaryMongo
    @Bean
    public MongoDatabaseFactory primaryMongoDatabaseFactory(@PrimaryMongo final MongoClient primaryMongoClient) {
        return new SimpleMongoClientDatabaseFactory(primaryMongoClient, this.databaseName);
    }

    @DefaultMongo
    @Bean
    public MongoDatabaseFactory defaultMongoDatabaseFactory(@DefaultMongo final MongoClient defaultMongoClient) {
        return new SimpleMongoClientDatabaseFactory(defaultMongoClient, this.databaseName);
    }

    @Primary
    @RoutingMongo
    @Bean
    public MongoDatabaseFactory routingMongoDatabaseFactory(
        @PrimaryMongo final MongoDatabaseFactory primaryMongoDatabaseFactory,
        @DefaultMongo final MongoDatabaseFactory defaultMongoDatabaseFactory
    ) {
        return new RoutingMongoDatabaseFactory(primaryMongoDatabaseFactory, defaultMongoDatabaseFactory);
    }

    @Bean
    public MongoTemplate mongoTemplate(
        @RoutingMongo final MongoDatabaseFactory routingMongoDatabaseFactory,
        final MappingMongoConverter mappingMongoConverter
    ) {
        return new MongoTemplate(routingMongoDatabaseFactory, mappingMongoConverter);
    }
}
