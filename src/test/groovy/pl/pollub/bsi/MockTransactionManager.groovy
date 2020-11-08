package pl.pollub.bsi

import edu.umd.cs.findbugs.annotations.NonNull
import io.micronaut.transaction.SynchronousTransactionManager
import io.micronaut.transaction.TransactionCallback
import io.micronaut.transaction.TransactionDefinition
import io.micronaut.transaction.TransactionStatus
import io.micronaut.transaction.exceptions.TransactionException
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.jooq.tools.jdbc.MockConnection

import java.sql.Connection

class MockTransactionManager implements SynchronousTransactionManager<Connection> {
    @Override
    TransactionStatus<Connection> getTransaction(@Nullable @edu.umd.cs.findbugs.annotations.Nullable TransactionDefinition definition) throws TransactionException {
        return new MockTransactionStatus()
    }

    @Override
    void commit(TransactionStatus<Connection> status) throws TransactionException {

    }

    @Override
    void rollback(TransactionStatus<Connection> status) throws TransactionException {

    }

    @Override
    Connection getConnection() {
        return new MockConnection()
    }

    @Override
    <R> R execute(@NotNull @NonNull TransactionDefinition definition, @NotNull @NonNull TransactionCallback<Connection, R> callback) {
        return callback.call(getTransaction(TransactionDefinition.DEFAULT))
    }

    @Override
    <R> R executeRead(@NotNull @NonNull TransactionCallback<Connection, R> callback) {
        return callback.call(getTransaction(TransactionDefinition.DEFAULT))
    }

    @Override
    <R> R executeWrite(@NotNull @NonNull TransactionCallback<Connection, R> callback) {
        return callback.call(getTransaction(TransactionDefinition.DEFAULT))
    }
}
