package ua.yuris.restaurant.web.cartstate;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 26.07.14
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
public class NewItemsCartState extends CartState {
    public NewItemsCartState(int numberOfNewItems, int numberOfConfirmedItems,
                                   double totalOfNewItems, double totalOfConfirmedItems) {
        super(numberOfNewItems, numberOfConfirmedItems, totalOfNewItems, totalOfConfirmedItems);
    }

    @Override
    public String getLabel() {
        return "Cart";
    }

    @Override
    public String getCartHeader() {
        return "Cart";
    }

    @Override
    public String getSummary() {
        return numberOfNewItems == 1 ? "" + numberOfNewItems + " Item in Cart" :
                "" + numberOfNewItems + " Items in Cart";
    }

    @Override
    public double getTotal() {
        return totalOfNewItems;
    }
}
