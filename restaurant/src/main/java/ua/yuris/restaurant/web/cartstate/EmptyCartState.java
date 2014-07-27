package ua.yuris.restaurant.web.cartstate;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 26.07.14
 * Time: 21:50
 * To change this template use File | Settings | File Templates.
 */
public class EmptyCartState extends CartState {
    public EmptyCartState(int numberOfNewItems, int numberOfConfirmedItems,
                          double totalOfNewItems, double totalOfConfirmedItems) {
        super(numberOfNewItems, numberOfConfirmedItems, totalOfNewItems, totalOfConfirmedItems);
    }

    @Override
    public String getLabel() {
        return "Cart";
    }

    @Override
    public String getCartHeader() {
        return "Ups Your Cart is Empty";
    }

    @Override
    public String getSummary() {
        return "Cart is Empty";
    }

    @Override
    public double getTotal() {
        return 0.0;
    }
}
