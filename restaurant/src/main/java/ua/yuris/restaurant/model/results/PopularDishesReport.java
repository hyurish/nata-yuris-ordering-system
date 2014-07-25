package ua.yuris.restaurant.model.results;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 10.06.14
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class PopularDishesReport {
    private static final Logger LOG = LoggerFactory.getLogger(PopularDishesReport.class);

    private String menuItemTitle;
    private Long categoryId;
    private String categoryTitle;
    private BigDecimal menuItemQuantity;
    private BigDecimal revenue;

    public PopularDishesReport() {
    }

    public PopularDishesReport(String menuItemTitle, Long categoryId, String categoryTitle,
                               BigDecimal menuItemQuantity, BigDecimal revenue) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.menuItemQuantity = menuItemQuantity;
        this.menuItemTitle = menuItemTitle;
        this.revenue = revenue;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public BigDecimal getMenuItemQuantity() {
        return menuItemQuantity;
    }

    public void setMenuItemQuantity(BigDecimal menuItemQuantity) {
        this.menuItemQuantity = menuItemQuantity;
    }

    public String getMenuItemTitle() {
        return menuItemTitle;
    }

    public void setMenuItemTitle(String menuItemTitle) {
        this.menuItemTitle = menuItemTitle;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Double getPrettyRevenue() {
        return revenue.divide(new BigDecimal(100.)).doubleValue();
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("menuItemTitle", this.getMenuItemTitle())
                .append("categoryId", this.getCategoryId())
                .append("categoryTitle", this.getCategoryTitle())
                .append("menuItemQuantity", this.getMenuItemQuantity())
                .append("revenue", this.getPrettyRevenue())
                .toString();
    }

}
