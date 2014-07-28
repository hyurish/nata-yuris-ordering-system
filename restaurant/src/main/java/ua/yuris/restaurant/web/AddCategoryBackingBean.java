package ua.yuris.restaurant.web;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.yuris.restaurant.model.MenuCategory;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 14.05.14
 * Time: 3:01
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class AddCategoryBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(AddCategoryBackingBean.class);

    private String categoryTitle;

    public AddCategoryBackingBean() {
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public void onCancelAction(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void onSaveAction(ActionEvent actionEvent) {
        MenuCategory menuCategory = createMenuCategory();
        RequestContext.getCurrentInstance().closeDialog(menuCategory);
    }

    private MenuCategory createMenuCategory() {
        MenuCategory menuCategory = new MenuCategory();
        menuCategory.setTitle(categoryTitle);
        menuCategory.setActive(true);
        return menuCategory;
    }
}
