package ua.yuris.restaurant.exception;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 23.07.14
 * Time: 2:19
 * To change this template use File | Settings | File Templates.
 */
public class RestaurantException extends RuntimeException {
    public RestaurantException() {
    }

    public RestaurantException(Throwable cause) {
        super(cause);
    }

    public RestaurantException(String message) {
        super(message);
    }

    public RestaurantException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestaurantException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
