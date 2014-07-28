package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;

import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.model.MenuItem;
import ua.yuris.restaurant.model.enums.CookingPlaceType;
import ua.yuris.restaurant.service.MenuService;
import ua.yuris.restaurant.util.FacesMessagesUtil;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 15.05.14
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class MenuItemsBackingBean
        implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(MenuItemsBackingBean.class);

    @ManagedProperty(value = "#{menuService}")
    private MenuService menuService;

    private List<MenuCategory> menuCategories;
    private List<MenuItem> menuItems;

    private CookingPlaceType[] cookingPlaces = CookingPlaceType.values();
    private MenuCategory selectedMenuCategory;
    private boolean isDisabledIncluded;
    private MenuItem selectedMenuItem;

    public MenuItemsBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        loadMenuCategories();

        selectedMenuCategory = getCategoryAsParam("categoryId");
        if (selectedMenuCategory == null) {
            if (!menuCategories.isEmpty()) {
                setSelectedMenuCategory(menuCategories.get(0));
            } else {
                LOG.info("Categories list is empty");
            }
        }

        loadMenuItems();
    }

    private void loadMenuCategories() {
        menuCategories = menuService.findAllActiveCategories();
    }

    private MenuCategory getCategoryAsParam(String param) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.getExternalContext().getRequestParameterMap().containsKey(param)) {
            return null;
        }

        String categoryIdParam =
                facesContext.getExternalContext().getRequestParameterMap().get(param);
        Long categoryId = -1L;
        try {
            categoryId = Long.valueOf(categoryIdParam);
        } catch (NumberFormatException e) {
            LOG.info("Error while parsing menuCategory id");
            return null;
        }

        MenuCategory menuCategory = menuService.findCategory(categoryId);

        if (menuCategory == null) {
            LOG.info("MenuCategory is not found");
            return null;
        }
        return menuCategory;
    }

    private void loadMenuItems() {
        if (isDisabledIncluded) {
            menuItems = menuService.findAllMenuItemsByCategory(selectedMenuCategory);
        } else {
            menuItems = menuService.findAllActiveMenuItemsByCategory(selectedMenuCategory);
        }
    }

    public MenuService getMenuService() {
        return menuService;
    }

    public void changeCategory(MenuCategory menuCategory) {
        selectedMenuCategory = menuCategory;
        loadMenuItems();
    }

    public void onEdit(RowEditEvent event) {
        MenuItem menuItem = (MenuItem) event.getObject();
        try {
            menuService.saveMenuItem(menuItem);
            String summary = "Menu Item '" + menuItem.getTitle() + "' Edited";
            FacesMessagesUtil.addInfoMessageToFacesContext(summary);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            String summary = "Database operation failed";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
    }

    public void onCancel(RowEditEvent event) {
        String summary = "MenuItem '" + ((MenuItem) event.getObject()).getTitle() +
                "'  editing cancelled";
        FacesMessagesUtil.addInfoMessageToFacesContext(summary);
    }

    public void onHideShowDisabled(ActionEvent actionEvent) {
        isDisabledIncluded = !isDisabledIncluded;
        loadMenuItems();
    }

    public void onRefreshMenuItems() {
        loadMenuItems();
    }

    public void onChooseImage(MenuItem menuItem) {
        selectedMenuItem = menuItem;
        RequestContext.getCurrentInstance().openDialog("select-image");
    }

    public void onImageChosen(SelectEvent event) {
        if (event.getObject() != null) {
            String newImage = (String) event.getObject();

            String oldImage = selectedMenuItem.getPicture();
            if (!oldImage.equals(newImage)) {
                selectedMenuItem.setPicture(newImage);

                try {
                    menuService.saveMenuItem(selectedMenuItem);
                    String summary = "Icon of '" + selectedMenuItem.getTitle() + "' Edited";
                    FacesMessagesUtil.addInfoMessageToFacesContext(summary);
                } catch (TransactionException e) {
                    LOG.error(e.getMessage());
                    String summary = "Database operation failed";
                    FacesMessagesUtil.addErrorMessageToFacesContext(summary);
                }
            }

        }

    }

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public CookingPlaceType[] getCookingPlaces() {
        return cookingPlaces;
    }

    public void setCookingPlaces(CookingPlaceType[] cookingPlaces) {
        this.cookingPlaces = cookingPlaces;
    }

    public boolean isDisabledIncluded() {
        return isDisabledIncluded;
    }

    public void setDisabledIncluded(boolean disabledIncluded) {
        this.isDisabledIncluded = disabledIncluded;
    }

    public MenuCategory getSelectedMenuCategory() {
        return selectedMenuCategory;
    }

    public void setSelectedMenuCategory(MenuCategory selectedMenuCategory) {
        this.selectedMenuCategory = selectedMenuCategory;
    }

    public MenuItem getSelectedMenuItem() {
        return selectedMenuItem;
    }

    public void setSelectedMenuItem(MenuItem selectedMenuItem) {
        this.selectedMenuItem = selectedMenuItem;
    }
}
