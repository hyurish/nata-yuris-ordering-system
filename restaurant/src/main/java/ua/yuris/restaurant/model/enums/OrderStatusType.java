package ua.yuris.restaurant.model.enums;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 24.05.14
 * Time: 19:55
 * To change this template use File | Settings | File Templates.
 */
public enum OrderStatusType {
    SUBMITTED("submitted"),
    PAID("paid"),
    UNPAID("unpaid");

    private String label;

    private OrderStatusType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
