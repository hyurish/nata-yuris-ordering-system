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
import ua.yuris.restaurant.service.MenuService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 13.05.14
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class CategoriesBackingBean
        implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(CategoriesBackingBean.class);

    @ManagedProperty(value = "#{menuService}")
    private MenuService menuService;

    private List<MenuCategory> categories;
    private boolean withDisabled;

    private MenuCategory selectedMenuCategory;

    public CategoriesBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        loadCategories(withDisabled);
    }

    private void loadCategories(boolean withDisabled) {
        if (withDisabled) {
            categories = menuService.findAllCategories();
        } else {
            categories = menuService.findAllActiveCategories();
        }
    }

    public void onEdit(RowEditEvent event) {
        MenuCategory menuCategory = (MenuCategory) event.getObject();
        try {
            menuService.saveCategory(menuCategory);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed", "Database operation failed");
            return;
        }
        addInfoMessageToFacesContext("MenuCategory '" + menuCategory.getTitle() + "' Edited",
                "MenuCategory '" + menuCategory.getTitle() + "' Edited");
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
        addInfoMessageToFacesContext("MenuCategory editing cancelled",
                "MenuCategory '" + ((MenuCategory) event.getObject()).getTitle() +
                        "' editing cancelled");
    }

    public void onHideShowDisabled(ActionEvent actionEvent) {
        withDisabled = !withDisabled;
        loadCategories(withDisabled);
    }

    public void onRefreshCategory() {
        loadCategories(withDisabled);
    }

    public void addCategory() {
        RequestContext.getCurrentInstance().openDialog("add-category");
    }

    public void onDialogReturn(SelectEvent event) {
        if (event.getObject() != null) {
            MenuCategory menuCategory = (MenuCategory) event.getObject();

            Integer maxPriority = menuService.getCategoryMaxPriority();
            if (maxPriority == null) {
                maxPriority = 0;
            }
            menuCategory.setPriority(++maxPriority);
            try {
                menuService.saveCategory(menuCategory);
            } catch (TransactionException e) {
                LOG.error(e.getMessage());
                addErrorMessageToFacesContext("Database operation failed",
                        "Database operation failed");
                return;
            }
            loadCategories(withDisabled);
            addInfoMessageToFacesContext("MenuCategory '" + menuCategory.getTitle() + "' Added",
                    "MenuCategory '" + menuCategory.getTitle() + "' Added");
        }
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

    public boolean getWithDisabled() {
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
}
