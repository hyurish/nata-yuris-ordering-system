package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;

import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.service.MenuService;
import ua.yuris.restaurant.util.FacesMessagesUtil;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 13.05.14
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class CategoriesBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(CategoriesBackingBean.class);

    @ManagedProperty(value = "#{menuService}")
    private MenuService menuService;

    private List<MenuCategory> categories;
    private boolean isDisabledIncluded;

    private MenuCategory selectedMenuCategory;

    public CategoriesBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        if (isDisabledIncluded) {
            categories = menuService.findAllCategories();
        } else {
            categories = menuService.findAllActiveCategories();
        }
    }

    public void onEdit(RowEditEvent event) {
        MenuCategory menuCategory = (MenuCategory) event.getObject();
        try {
            menuService.saveCategory(menuCategory);
            String summary = "MenuCategory '" + menuCategory.getTitle() + "' Edited";
            FacesMessagesUtil.addInfoMessageToFacesContext(summary);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            String summary = "Database operation failed";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
    }

    public void onCancel(RowEditEvent event) {
        String summary = "MenuCategory '" + ((MenuCategory) event.getObject()).getTitle() +
                "' editing cancelled";
        FacesMessagesUtil.addInfoMessageToFacesContext(summary);
    }

    public void onHideShowDisabled(ActionEvent actionEvent) {
        isDisabledIncluded = !isDisabledIncluded;
        loadCategories();
    }

    public void onRefreshCategory() {
        loadCategories();
    }

    public void addCategory() {
        RequestContext.getCurrentInstance().openDialog("add-category");
    }

    public void onDialogReturn(SelectEvent event) {
        MenuCategory menuCategory = (MenuCategory) event.getObject();
        menuCategory.setPriority(getNextPriority());

        try {
            menuService.saveCategory(menuCategory);
            loadCategories();
            String summary = "MenuCategory '" + menuCategory.getTitle() + "' Added";
            FacesMessagesUtil.addInfoMessageToFacesContext(summary);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            String summary = "Database operation failed";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
    }

    private Integer getNextPriority() {
        Integer maxPriority = menuService.getCategoryMaxPriority();
        if (maxPriority == null) {
            maxPriority = 0;
        }
        return ++maxPriority;
    }

    public List<MenuCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<MenuCategory> categories) {
        this.categories = categories;
    }

    public MenuService getMenuService() {
        return menuService;
    }

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    public boolean getDisabledIncluded() {
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
}
