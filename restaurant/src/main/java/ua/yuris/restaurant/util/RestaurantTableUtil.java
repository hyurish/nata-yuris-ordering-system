package ua.yuris.restaurant.util;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 26.07.14
 * Time: 23:35
 * To change this template use File | Settings | File Templates.
 */
public class RestaurantTableUtil {
    private static final long DEFAULT_RESTAURANT_TABLE_ID = 5L;

    // Temporal implementation. In restaurant has to be obtained from pre configured Map with
    // gadget ID as keys and RestaurantTable ID as values
    public static long getDefaultTableId() {
        return DEFAULT_RESTAURANT_TABLE_ID;
    }
}
