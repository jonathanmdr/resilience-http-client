package com.resilience.auditworker.transaction;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
public class DefaultTransactionManager implements TransactionManager {

    private final TransactionTemplate transactionTemplate;

    public DefaultTransactionManager(final TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void runInTransactionContext(final Runnable runnable) {
        TransactionContext.transactionContext(
            () -> this.transactionTemplate.executeWithoutResult(_ -> runnable.run())
        );
    }

    @Override
    public <T> T runInTransactionContext(final Supplier<T> supplier) {
        return TransactionContext.transactionContext(
            () -> this.transactionTemplate.execute(_ -> supplier.get())
        );
    }

    @Override
    public void runInDefaultContext(final Runnable runnable) {
        TransactionContext.defaultContext(runnable);
    }

    @Override
    public <T> T runInDefaultContext(final Supplier<T> supplier) {
        return TransactionContext.defaultContext(supplier::get);
    }
}
