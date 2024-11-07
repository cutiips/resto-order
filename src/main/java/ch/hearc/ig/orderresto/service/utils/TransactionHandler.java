package ch.hearc.ig.orderresto.service.utils;

import java.sql.Connection;

public class TransactionHandler {

    public interface TransactionCallable<T> {
        T execute(Connection connection) throws Exception;
    }

    public <T> T executeInTransaction(TransactionCallable<T> action) throws Exception {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = action.execute(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
