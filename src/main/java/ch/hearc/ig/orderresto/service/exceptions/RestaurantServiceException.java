package ch.hearc.ig.orderresto.service.exceptions;

public class RestaurantServiceException extends Exception {

    public RestaurantServiceException(String message) {
        super(message);
    }

    public RestaurantServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
