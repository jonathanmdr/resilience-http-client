package com.resilience.auditworker.transaction;

public final class TransactionContext {

    private static final ScopedValue<OperationType> OPERATION_TYPE_CONTEXT;

    private TransactionContext() {
        // It's not an instantiable class
    }

    static {
        OPERATION_TYPE_CONTEXT = ScopedValue.newInstance();
    }

    public static void transactionContext(final Runnable runnable) {
        ScopedValue.where(OPERATION_TYPE_CONTEXT, OperationType.TRANSACTIONAL).run(runnable);
    }

    public static <R> R transactionContext(final ScopedValue.CallableOp<R, Throwable> callableOp) {
        try {
            return ScopedValue.where(OPERATION_TYPE_CONTEXT, OperationType.TRANSACTIONAL).call(callableOp);
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void defaultContext(final Runnable runnable) {
        ScopedValue.where(OPERATION_TYPE_CONTEXT, OperationType.DEFAULT).run(runnable);
    }

    public static <R> R defaultContext(final ScopedValue.CallableOp<R, Throwable> callableOp) {
        try {
            return ScopedValue.where(OPERATION_TYPE_CONTEXT, OperationType.DEFAULT).call(callableOp);
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static OperationType currentOperationType() {
        return OPERATION_TYPE_CONTEXT.orElse(OperationType.DEFAULT);
    }
}
