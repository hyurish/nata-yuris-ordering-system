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
import ua.yuris.restaurant.web.cartstate.CartState;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 02.07.14
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class MenuBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(MenuBackingBean.class);
    private static final int INITIAL_MENU_CATEGORY_NUMBER = 0;

    @ManagedProperty(value = "#{menuStateService}")
    private MenuStateService menuStateService;

    @ManagedProperty(value = "#{cartBean}")
    private CartBean cartBean;

    private String currentMenuCategoryId;
    private MenuCategory currentMenuCategory;
    private List<MenuCategory> menuCategories;
    private List<MenuItem> menuItems;
    private boolean tint;

    public MenuBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        loadCategories();
        loadMenuItems();
    }

    private void loadCategories() {
        menuCategories = menuStateService.findAllActiveCategories();
        if (currentMenuCategory == null && !menuCategories.isEmpty()) {
            currentMenuCategory = menuCategories.get(INITIAL_MENU_CATEGORY_NUMBER);
        }
    }

    private void loadMenuItems() {
        if (currentMenuCategory != null) {
            menuItems = menuStateService.findAllActiveMenuItemByCategory(currentMenuCategory);
        } else {
            menuItems = new ArrayList<>();
        }
    }

    public boolean isCartEmpty() {
        CartState cartState = cartBean.getCartState();
        return cartState.isEmpty();
    }

    public String getSummary() {
        CartState cartState = cartBean.getCartState();
        return cartState.getSummary();
    }

    public String getCheckoutLabel() {
        CartState cartState = cartBean.getCartState();
        return cartState.getLabel();
    }

    public String getTotal() {
        CartState cartState = cartBean.getCartState();
        return "" + cartState.getTotal() + " ₴";
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
        parseCategoryId();
    }

    private void parseCategoryId() {
        if (currentMenuCategoryId != null) {
            try {
                Long categoryId = Long.parseLong(currentMenuCategoryId);
                currentMenuCategory = menuStateService.findCategory(categoryId);
                menuItems = menuStateService.findAllActiveMenuItemByCategory(currentMenuCategory);
            } catch (NumberFormatException e) {
                LOG.info("Invalid request parameter categoryId");
            }
        }
    }

    public void setCurrentMenuCategory(MenuCategory currentMenuCategory) {
        this.currentMenuCategory = currentMenuCategory;
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
