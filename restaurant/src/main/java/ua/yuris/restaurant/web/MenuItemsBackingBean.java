package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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

    private List<MenuCategory> categories;
    private List<MenuItem> menuItems;

    private CookingPlaceType[] cookingPlaces = CookingPlaceType.values();
    private MenuCategory selectedMenuCategory;
    private boolean withDisabled;
    private MenuItem selectedMenuItem;

    public MenuItemsBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        categories = menuService.findAllActiveCategories();

        selectedMenuCategory = getCategoryAsParam("categoryId");
        if (selectedMenuCategory == null) {
            if (!categories.isEmpty()) {
                setSelectedMenuCategory(categories.get(0));
            } else {
                LOG.info("Categories list is empty");
            }
        }

        loadMenuItems(selectedMenuCategory, withDisabled);
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

    private void loadMenuItems(MenuCategory menuCategory, boolean withDisabled) {
        if (withDisabled) {
            menuItems = menuService.findAllMenuItemsByCategory(menuCategory);
        } else {
            menuItems = menuService.findAllActiveMenuItemsByCategory(menuCategory);
        }
    }

    public MenuService getMenuService() {
        return menuService;
    }

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    public List<MenuCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<MenuCategory> categories) {
        this.categories = categories;
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

    public boolean isWithDisabled() {
        return withDisabled;
    }

    public void setWithDisabled(boolean withDisabled) {
        this.withDisabled = withDisabled;
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

    public void changeCategory(MenuCategory menuCategory) {
        setSelectedMenuCategory(menuCategory);
        loadMenuItems(selectedMenuCategory, withDisabled);
    }

    public void onEdit(RowEditEvent event) {
        MenuItem menuItem = (MenuItem) event.getObject();
        try {
            menuService.saveMenuItem(menuItem);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed", "Database operation failed");
            return;
        }
        addInfoMessageToFacesContext("Menu Item '" + menuItem.getTitle() + "' Edited",
                "Menu Item '" + menuItem.getTitle() + "' Edited");
    }

    private void addInfoMessageToFacesContext(String summary, String detail) {
        FacesMessage msg = new FacesMessage(summary, detail);
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private void addErrorMessageToFacesContext(String summary, String detail) {
        FacesMessage msg = new FacesMessage(summary, detail);
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    public void onCancel(RowEditEvent event) {
        addInfoMessageToFacesContext("MenuItem '" + ((MenuItem) event.getObject()).getTitle() +
                "'  editing cancelled",
                "MenuItem '" + ((MenuItem) event.getObject()).getTitle() + "'  editing cancelled");
    }

    public void onHideShowDisabled(ActionEvent actionEvent) {
        withDisabled = !withDisabled;
        loadMenuItems(selectedMenuCategory, withDisabled);
    }

    public void onRefreshMenuItems() {
        loadMenuItems(selectedMenuCategory, withDisabled);
    }

    public void chooseImage(MenuItem menuItem) {
        setSelectedMenuItem(menuItem);
        RequestContext.getCurrentInstance().openDialog("select-image");
    }

    public void onImageChosen(SelectEvent event) {
        if (event.getObject() != null) {
            String image = (String) event.getObject();

            if (!getSelectedMenuItem().getPicture().equals(image)) {
                getSelectedMenuItem().setPicture(image);

                try {
                    menuService.saveMenuItem(getSelectedMenuItem());
                } catch (TransactionException e) {
                    LOG.error(e.getMessage());
                    addErrorMessageToFacesContext("Database operation failed",
                            "Database operation failed");
                    return;
                }

                addInfoMessageToFacesContext("Icon of '" + getSelectedMenuItem().getTitle() +
                        "' Edited", "Icon of '" + getSelectedMenuItem().getTitle() + "' Edited");
            }

        }

    }
}
