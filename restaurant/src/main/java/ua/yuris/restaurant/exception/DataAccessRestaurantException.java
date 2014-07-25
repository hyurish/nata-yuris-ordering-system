package ua.yuris.restaurant.exception;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 23.07.14
 * Time: 2:45
 * To change this template use File | Settings | File Templates.
 */
public class DataAccessRestaurantException extends RestaurantException {
    public DataAccessRestaurantException() {
    }

    public DataAccessRestaurantException(Throwable cause) {
        super(cause);
    }

    public DataAccessRestaurantException(String message) {
        super(message);
    }

    public DataAccessRestaurantException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessRestaurantException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
