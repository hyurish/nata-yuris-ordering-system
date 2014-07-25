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

import ua.yuris.restaurant.model.Account;
import ua.yuris.restaurant.service.AccountService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 29.05.14
 * Time: 20:31
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class WaiterConverter implements Converter {
    private static final Logger LOG = LoggerFactory.getLogger(WaiterConverter.class);

    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;

    public List<Account> waiters;

    public WaiterConverter() {
    }

    @PostConstruct
    public void init() {
        waiters = accountService.findAllAccountByRoleDescription("ROLE_WAITER");
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
                    for (Account a : waiters) {
                        if (a.getId().longValue() == id) {
                            return a;
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Conversion Error", "Not a valid account"));
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
            return String.valueOf(((Account) value).getId());
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Conversion Error", "Conversion Error"), e);
        }
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public List<Account> getWaiters() {
        return waiters;
    }

    public void setWaiters(List<Account> waiters) {
        this.waiters = waiters;
    }
}
