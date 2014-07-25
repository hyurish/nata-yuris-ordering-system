package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;

import ua.yuris.restaurant.model.Account;
import ua.yuris.restaurant.model.Role;
import ua.yuris.restaurant.service.AccountService;
import ua.yuris.restaurant.service.RoleStateService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 05.06.14
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AccountsBackingBean
        implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsBackingBean.class);

    @ManagedProperty(value = "#{roleStateService}")
    private RoleStateService roleStateService;

    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;

    private List<Account> accounts;
    private boolean withDisabled;

    public AccountsBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        loadAccounts(withDisabled);
    }

    private void loadAccounts(boolean withDisabled) {
        if (withDisabled) {
            accounts = accountService.findAllAccounts();
        } else {
            accounts = accountService.findAllActiveAccounts();
        }
    }

    public void onEdit(RowEditEvent event) {
        Account account = (Account) event.getObject();
        try {
            account = accountService.saveAccount(account);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed", "Database operation failed");
            return;
        }
        addInfoMessageToFacesContext("Account '" + account.getUsername() + "' Edited",
                "Account '" + account.getUsername() + "' Edited");
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

    public void usernameAvailabilityCheck(Account account, FacesContext context,
                                          UIComponent component, Object value) {
        if (value == null) return;
        boolean available = accountService.checkAvailableUsername(value.toString(), account);

        if (!available) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Username validation failed",
                    "Username " + value.toString() + " already exists");
            throw new ValidatorException(msg);
        }
    }

    public void onCancel(RowEditEvent event) {
        addInfoMessageToFacesContext("Account editing cancelled",
                ((Account) event.getObject()).getUsername());
    }

    public void onHideShowDisabled(ActionEvent actionEvent) {
        withDisabled = !withDisabled;
        loadAccounts(withDisabled);
    }

    public void onRefreshAccounts() {
        loadAccounts(withDisabled);
    }

    public Set<Role> getRoles() {
        return roleStateService.findAllActiveRoles();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public RoleStateService getRoleStateService() {
        return roleStateService;
    }

    public void setRoleStateService(RoleStateService roleStateService) {
        this.roleStateService = roleStateService;
    }

    public boolean isWithDisabled() {
        return withDisabled;
    }

    public void setWithDisabled(boolean withDisabled) {
        this.withDisabled = withDisabled;
    }
}
