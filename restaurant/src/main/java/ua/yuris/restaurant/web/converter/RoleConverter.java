package ua.yuris.restaurant.web.converter;

import java.util.Set;

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

import ua.yuris.restaurant.model.Role;
import ua.yuris.restaurant.service.RoleStateService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 05.06.14
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class RoleConverter
        implements Converter {
    private static final Logger LOG = LoggerFactory.getLogger(RoleConverter.class);

    @ManagedProperty(value = "#{roleStateService}")
    private RoleStateService roleStateService;

    public Set<Role> roles;

    public RoleConverter() {
    }

    @PostConstruct
    public void init() {
        roles = roleStateService.findAllActiveRoles();
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
                    for (Role r : roles) {
                        if (r.getId().longValue() == id) {
                            return r;
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Conversion Error", "Not a valid role"));
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
            return String.valueOf(((Role) value).getId());
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Conversion Error", "Conversion Error"), e);
        }

    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public RoleStateService getRoleStateService() {
        return roleStateService;
    }

    public void setRoleStateService(RoleStateService roleStateService) {
        this.roleStateService = roleStateService;
    }
}
