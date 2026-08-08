package com.resilience.auditworker.configuration.transaction;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import com.resilience.auditworker.transaction.OperationType;
import com.resilience.auditworker.transaction.TransactionContext;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import java.util.Optional;

public final class RoutingMongoDatabaseFactory implements MongoDatabaseFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RoutingMongoDatabaseFactory.class);

    private final MongoDatabaseFactory primaryDatabaseFactory;
    private final MongoDatabaseFactory defaultDatabaseFactory;

    public RoutingMongoDatabaseFactory(
        final MongoDatabaseFactory primaryDatabaseFactory,
        final MongoDatabaseFactory defaultDatabaseFactory
    ) {
        this.primaryDatabaseFactory = primaryDatabaseFactory;
        this.defaultDatabaseFactory = defaultDatabaseFactory;
    }

    @Override
    public MongoDatabase getMongoDatabase() throws DataAccessException {
        return defineMongoDatabaseFactory()
            .getMongoDatabase();
    }

    @Override
    public MongoDatabase getMongoDatabase(final String dbName) throws DataAccessException {
        return defineMongoDatabaseFactory()
            .getMongoDatabase(dbName);
    }

    @Override
    public PersistenceExceptionTranslator getExceptionTranslator() {
        return defineMongoDatabaseFactory()
            .getExceptionTranslator();
    }

    @Override
    public ClientSession getSession(final ClientSessionOptions options) {
        return defineMongoDatabaseFactory()
            .getSession(options);
    }

    @Override
    public MongoDatabaseFactory withSession(final ClientSession session) {
        return defineMongoDatabaseFactory()
            .withSession(session);
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return defineMongoDatabaseFactory()
            .getCodecRegistry();
    }

    @Override
    public MongoDatabaseFactory withSession(final ClientSessionOptions options) {
        return defineMongoDatabaseFactory()
            .withSession(options);
    }

    @Override
    public boolean isTransactionActive() {
        return defineMongoDatabaseFactory()
            .isTransactionActive();
    }

    @Override
    public boolean hasCodecFor(final Class<?> type) {
        return defineMongoDatabaseFactory()
            .hasCodecFor(type);
    }

    @Override
    public <T> Optional<Codec<T>> getCodecFor(final Class<T> type) {
        return defineMongoDatabaseFactory()
            .getCodecFor(type);
    }

    private MongoDatabaseFactory defineMongoDatabaseFactory() {
        final OperationType operationType = TransactionContext.currentOperationType();
        final MongoDatabaseFactory factory = switch (operationType) {
            case DEFAULT -> this.defaultDatabaseFactory;
            case TRANSACTIONAL -> this.primaryDatabaseFactory;
        };

        LOG.info("Using '{}' database factory for '{}' transaction type", factory == this.primaryDatabaseFactory ? "primary" : "default", operationType);

        return factory;
    }
}
