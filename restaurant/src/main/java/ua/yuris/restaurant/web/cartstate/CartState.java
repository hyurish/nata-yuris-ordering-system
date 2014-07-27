package ua.yuris.restaurant.web.cartstate;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 26.07.14
 * Time: 21:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class CartState {
    protected int numberOfNewItems;
    protected int numberOfConfirmedItems;
    protected double totalOfNewItems;
    protected double totalOfConfirmedItems;

    protected CartState(int numberOfNewItems, int numberOfConfirmedItems,
                        double totalOfNewItems, double totalOfConfirmedItems) {
        this.numberOfConfirmedItems = numberOfConfirmedItems;
        this.numberOfNewItems = numberOfNewItems;
        this.totalOfConfirmedItems = totalOfConfirmedItems;
        this.totalOfNewItems = totalOfNewItems;
    }

    public abstract String getLabel();
    public abstract String getCartHeader();
    public abstract String getSummary();
    public abstract double getTotal();

    public boolean isEmpty() {
        return numberOfNewItems == 0 && numberOfConfirmedItems == 0;
    }

    public boolean isNewItemsIn() {
        return numberOfNewItems > 0;
    }
}
