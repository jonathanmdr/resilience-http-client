package com.resilience.auditworker.transaction;

import java.util.function.Supplier;

public interface TransactionManager {

    void runInTransactionContext(final Runnable runnable);

    <T> T runInTransactionContext(final Supplier<T> supplier);

    void runInDefaultContext(final Runnable runnable);

    <T> T runInDefaultContext(final Supplier<T> supplier);
}
