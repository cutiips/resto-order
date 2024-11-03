package ch.hearc.ig.orderresto.service.exceptions;

/**
 * Exception spécifique à la couche de service pour les opérations relatives aux clients.
 */
public class CustomerServiceException extends Exception {

    public CustomerServiceException(String message) {
        super(message);
    }

    public CustomerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}