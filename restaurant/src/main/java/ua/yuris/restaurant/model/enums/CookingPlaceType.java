package ua.yuris.restaurant.model.enums;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 24.05.14
 * Time: 16:38
 * To change this template use File | Settings | File Templates.
 */
public enum CookingPlaceType {
    KITCHEN("kitchen"),
    BAR("bar");

    private String label;

    private CookingPlaceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
