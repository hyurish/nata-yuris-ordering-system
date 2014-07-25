package ua.yuris.restaurant.exception;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 23.07.14
 * Time: 2:21
 * To change this template use File | Settings | File Templates.
 */
public class IllegalArgumentRestaurantException extends RestaurantException{

    public IllegalArgumentRestaurantException() {
    }

    public IllegalArgumentRestaurantException(Throwable cause) {
        super(cause);
    }

    public IllegalArgumentRestaurantException(String message) {
        super(message);
    }

    public IllegalArgumentRestaurantException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalArgumentRestaurantException(String message, Throwable cause,
                                              boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
