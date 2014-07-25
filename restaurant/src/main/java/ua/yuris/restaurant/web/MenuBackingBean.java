package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.yuris.restaurant.exception.RestaurantException;
import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.model.MenuItem;
import ua.yuris.restaurant.service.MenuStateService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 02.07.14
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class MenuBackingBean
        implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(MenuBackingBean.class);

    @ManagedProperty(value = "#{menuStateService}")
    private MenuStateService menuStateService;

    @ManagedProperty(value = "#{cartBean}")
    private CartBean cartBean;

    private List<MenuItem> menuItems;
    private List<MenuCategory> menuCategories;
    private String currentMenuCategoryId;
    private MenuCategory currentMenuCategory;
    private boolean tint;

    public MenuBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        menuCategories = menuStateService.findAllActiveCategories();
        if (!menuCategories.isEmpty()) {
            setCurrentMenuCategory(menuCategories.get(0));
        } else {
            menuItems = new ArrayList<>();
        }

    }

    public boolean isCartAndOrderEmpty() {
        return cartBean.isCartEmpty();
    }

    public String getSummary() {
        int numberInCart = cartBean.countNewItems();
        if (numberInCart > 0) {
            return numberInCart == 1 ? "" + numberInCart + " Item in Cart" : "" + numberInCart +
                    " Items in Cart";
        }
        int numberInOrder = cartBean.countConfirmedItems();
        if (numberInOrder > 0) {
            return numberInOrder == 1 ? "" + numberInOrder + " Item Ordered" : "" + numberInOrder +
                    " Items Ordered";
        }
        return "Cart is Empty";
    }

    public String getCheckout() {
        int numberInCart = cartBean.countNewItems();
        if (numberInCart > 0) {
            return "Cart";
        }
        int numberInOrder = cartBean.countConfirmedItems();
        if (numberInOrder > 0) {
            return "Your Order";
        }
        return "Cart";
    }

    public String getTotal() {
        double totalInCart = cartBean.getTotalOfNewItems();
        if (totalInCart > 0.01) {
            return "" + totalInCart + " ₴";
        }
        double totalInOrder = cartBean.getTotalOfConfirmedItems();
        if (totalInOrder > 0.01) {
            return "" + totalInOrder + " ₴";
        }
        return "0 ₴";
    }

    public boolean getChangeBgTint() {
        tint = !tint;
        return tint;
    }

    public boolean isItemInCart(MenuItem menuItem) {
        return cartBean.isNewItemInCart(menuItem);
    }

    public Integer getItemNumberInCart(MenuItem menuItem) {
        return cartBean.countNewItemsInCart(menuItem);
    }

    public void takeItem(MenuItem menuItem) {
        try {
            cartBean.takeItemFromCart(menuItem);
        } catch (RestaurantException e) {
            LOG.error("Item '" + menuItem.getTitle() + "' not removed");
        }
    }

    public void addItem(MenuItem menuItem) {
        try {
            cartBean.addItemToCart(menuItem);
        } catch (RestaurantException e) {
            LOG.error("Item '" + menuItem.getTitle() + "' not added");
        }
    }

    public CartBean getCartBean() {
        return cartBean;
    }

    public void setCartBean(CartBean cartBean) {
        this.cartBean = cartBean;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    public MenuCategory getCurrentMenuCategory() {
        return currentMenuCategory;
    }

    public String getCurrentMenuCategoryId() {
        return currentMenuCategoryId;
    }

    public void setCurrentMenuCategoryId(String currentMenuCategoryId) {
        this.currentMenuCategoryId = currentMenuCategoryId;
        if (currentMenuCategoryId != null) {
            try {
                Long categoryId = Long.parseLong(currentMenuCategoryId);
                MenuCategory menuCategory = menuStateService.findCategory(categoryId);
                setCurrentMenuCategory(menuCategory);
            } catch (NumberFormatException e) {
                LOG.info("Invalid request parameter categoryId");
            }
        }
    }

    public void setCurrentMenuCategory(MenuCategory currentMenuCategory) {
        this.currentMenuCategory = currentMenuCategory;
        menuItems = menuStateService.findAllActiveMenuItemByCategory(currentMenuCategory);
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public MenuStateService getMenuStateService() {
        return menuStateService;
    }

    public void setMenuStateService(MenuStateService menuStateService) {
        this.menuStateService = menuStateService;
    }
}
