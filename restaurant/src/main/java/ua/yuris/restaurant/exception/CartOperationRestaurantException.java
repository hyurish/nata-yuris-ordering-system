package ua.yuris.restaurant.exception;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 23.07.14
 * Time: 2:21
 * To change this template use File | Settings | File Templates.
 */
public class CartOperationRestaurantException extends RestaurantException{

    public CartOperationRestaurantException() {
    }

    public CartOperationRestaurantException(Throwable cause) {
        super(cause);
    }

    public CartOperationRestaurantException(String message) {
        super(message);
    }

    public CartOperationRestaurantException(String message, Throwable cause) {
        super(message, cause);
    }

    public CartOperationRestaurantException(String message, Throwable cause,
                                            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
