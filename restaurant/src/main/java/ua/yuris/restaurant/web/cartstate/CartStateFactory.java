package ua.yuris.restaurant.web.cartstate;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 26.07.14
 * Time: 21:47
 * To change this template use File | Settings | File Templates.
 */
public class CartStateFactory {
    public static CartState getCartState(int numberOfNewItems, int numberOfConfirmedItems,
                                         double totalOfNewItems, double totalOfConfirmedItems) {
        if (numberOfNewItems > 0) {
            return new NewItemsCartState(numberOfNewItems, numberOfConfirmedItems,
                    totalOfNewItems, totalOfConfirmedItems);
        } else if (numberOfConfirmedItems > 0) {
            return new ConfirmedItemsCartState(numberOfNewItems, numberOfConfirmedItems,
                    totalOfNewItems, totalOfConfirmedItems);
        }
        return new EmptyCartState(numberOfNewItems, numberOfConfirmedItems,
                totalOfNewItems, totalOfConfirmedItems);
    }
}
