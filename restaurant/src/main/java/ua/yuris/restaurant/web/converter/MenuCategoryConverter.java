package ua.yuris.restaurant.web.converter;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.service.MenuStateService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 13.05.14
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class MenuCategoryConverter
        implements Converter {
    private static final Logger LOG = LoggerFactory.getLogger(MenuCategoryConverter.class);

    @ManagedProperty(value = "#{menuStateService}")
    private MenuStateService menuStateService;

    public List<MenuCategory> categories;

    public MenuCategoryConverter() {
    }

    @PostConstruct
    public void init() {
        categories = menuStateService.findAllActiveCategories();
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (facesContext == null) throw new NullPointerException("facesContext");
        if (uiComponent == null) throw new NullPointerException("uiComponent");

        if (value != null) {
            value = value.trim();
            if (value.length() > 0) {
                try {
                    long id = Long.parseLong(value);
                    for (MenuCategory c : categories) {
                        if (c.getId().longValue() == id) {
                            return c;
                        }
                    }
                } catch (NumberFormatException numberFormatException) {
                    throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Conversion Error", "Not a valid category"));
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (facesContext == null) throw new NullPointerException("facesContext");
        if (uiComponent == null) throw new NullPointerException("uiComponent");

        if (value == null || value.equals("")) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        }
        try {
            return String.valueOf(((MenuCategory) value).getId());
        } catch (Exception exception) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Conversion Error", "Conversion Error"), exception);
        }

    }

    public MenuStateService getMenuStateService() {
        return menuStateService;
    }

    public void setMenuStateService(MenuStateService menuStateService) {
        this.menuStateService = menuStateService;
    }

    public List<MenuCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<MenuCategory> categories) {
        this.categories = categories;
    }
}
