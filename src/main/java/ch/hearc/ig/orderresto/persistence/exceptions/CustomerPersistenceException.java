package ch.hearc.ig.orderresto.persistence.exceptions;

public class CustomerPersistenceException extends Exception {
    public CustomerPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerPersistenceException(String message) {
    }
}
