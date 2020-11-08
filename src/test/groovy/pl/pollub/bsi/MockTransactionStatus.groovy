package pl.pollub.bsi

import io.micronaut.transaction.TransactionStatus
import io.micronaut.transaction.exceptions.TransactionException
import org.jooq.tools.jdbc.MockConnection

import java.sql.Connection

class MockTransactionStatus implements TransactionStatus<Connection> {
    @Override
    boolean hasSavepoint() {
        return false
    }

    @Override
    void flush() {

    }

    @Override
    Object getTransaction() {
        return new Object()
    }

    @Override
    Connection getConnection() {
        return new MockConnection()
    }

    @Override
    Object createSavepoint() throws TransactionException {
        return new Object()
    }

    @Override
    void rollbackToSavepoint(Object savepoint) throws TransactionException {

    }

    @Override
    void releaseSavepoint(Object savepoint) throws TransactionException {

    }

    @Override
    boolean isNewTransaction() {
        return false
    }

    @Override
    void setRollbackOnly() {

    }

    @Override
    boolean isRollbackOnly() {
        return false
    }

    @Override
    boolean isCompleted() {
        return false
    }
}
