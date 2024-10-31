package ch.hearc.ig.orderresto.exceptions;

public class CustomerPersistenceException extends Exception {
    public CustomerPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerPersistenceException(String message) {
    }
}
