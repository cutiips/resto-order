package ch.hearc.ig.orderresto.persistence.exceptions;

public class ProductPersistenceException extends Exception {
    public ProductPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductPersistenceException(String message) {
        super(message);
    }
}
