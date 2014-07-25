package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

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
 * Date: 16.05.14
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AddMenuItemBackingBean
        implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(AddCategoryBackingBean.class);

    @ManagedProperty(value = "#{menuService}")
    private MenuService menuService;

    private String title;
    private String description;
    private String measure;
    private double price;
    private CookingPlaceType cookingPlace;
    private MenuCategory menuCategory;

    private CookingPlaceType[] cookingPlaces = CookingPlaceType.values();
    private List<MenuCategory> menuCategories;

    private MenuCategory selectedMenuCategory;

    public AddMenuItemBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        menuCategories = menuService.findAllActiveCategories();
    }

    public void onPreRenderView(ComponentSystemEvent event) {
        if (this.selectedMenuCategory != null) {
            if (menuCategory == null)
                menuCategory = selectedMenuCategory;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CookingPlaceType getCookingPlace() {
        return cookingPlace;
    }

    public void setCookingPlace(CookingPlaceType cookingPlace) {
        this.cookingPlace = cookingPlace;
    }

    public CookingPlaceType[] getCookingPlaces() {
        return cookingPlaces;
    }

    public void setCookingPlaces(CookingPlaceType[] cookingPlaces) {
        this.cookingPlaces = cookingPlaces;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    public MenuService getMenuService() {
        return menuService;
    }

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    public MenuCategory getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        this.menuCategory = menuCategory;
    }

    public MenuCategory getSelectedMenuCategory() {
        return selectedMenuCategory;
    }

    public void setSelectedMenuCategory(MenuCategory selectedMenuCategory) {
        this.selectedMenuCategory = selectedMenuCategory;
    }

    public String onCancel() {
        FacesMessage msg = new FacesMessage("Menu Item creation cancelled", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);

        return "menu-items?faces-redirect=true&amp;categoryId=" + getMenuCategory().getId();
    }

    public String onSave() {
        MenuItem menuItem = createMenuItem();

        try {
            menuService.saveMenuItem(menuItem);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed",
                    "Database operation failed");
            return "";
        }
        addInfoMessageToFacesContext("Menu Item '" + menuItem.getTitle() + "' Added",
                "Menu Item '" + menuItem.getTitle() + "' Added");

        return "menu-items?faces-redirect=true&amp;categoryId=" + getMenuCategory().getId();
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

    private MenuItem createMenuItem() {
        MenuItem menuItem = new MenuItem();
        menuItem.setTitle(title);
        menuItem.setDescription(description);
        menuItem.setPrettyPrice(price);
        menuItem.setMenuCategory(menuCategory);
        menuItem.setCookingPlace(cookingPlace);
        menuItem.setMeasure(measure);
        menuItem.setPicture("default.jpg");
        menuItem.setActive(true);

        Integer maxPriority = menuService.getMenuItemMaxPriority();
        if (maxPriority == null) {
            maxPriority = 0;
        }
        menuItem.setPriority(++maxPriority);
        return menuItem;
    }
}
