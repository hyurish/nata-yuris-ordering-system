package ua.yuris.restaurant.web.cartstate;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 26.07.14
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
public class ConfirmedItemsCartState extends CartState {
    public ConfirmedItemsCartState(int numberOfNewItems, int numberOfConfirmedItems,
                                   double totalOfNewItems, double totalOfConfirmedItems) {
        super(numberOfNewItems, numberOfConfirmedItems, totalOfNewItems, totalOfConfirmedItems);
    }

    @Override
    public String getLabel() {
        return "Your Order";
    }

    @Override
    public String getCartHeader() {
        return "Your Order";
    }

    @Override
    public String getSummary() {
        return numberOfConfirmedItems == 1 ? "" + numberOfConfirmedItems + " Item Ordered" :
                "" + numberOfConfirmedItems + " Items Ordered";
    }

    @Override
    public double getTotal() {
        return totalOfConfirmedItems;
    }
}
