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
import ua.yuris.restaurant.util.FacesMessagesUtil;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 05.06.14
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AccountsBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsBackingBean.class);

    @ManagedProperty(value = "#{roleStateService}")
    private RoleStateService roleStateService;
    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;

    private List<Account> accounts;
    private boolean isDisabledIncluded;

    public AccountsBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        loadAccounts();
    }

    private void loadAccounts() {
        if (isDisabledIncluded) {
            accounts = accountService.findAllAccounts();
        } else {
            accounts = accountService.findAllActiveAccounts();
        }
    }

    public void onEdit(RowEditEvent event) {
        Account account = (Account) event.getObject();
        try {
            account = accountService.saveAccount(account);
            String summary = "Account '" + account.getUsername() + "' Edited";
            FacesMessagesUtil.addInfoMessageToFacesContext(summary);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            String summary = "Database operation failed";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
    }

    public void usernameAvailabilityValidation(Account account, FacesContext context,
                                               UIComponent component, Object value) {
        if (value == null) return;

        if (isUsernameExist(value.toString(), account)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Username validation failed",
                    "Username " + value.toString() + " already exists");
            throw new ValidatorException(msg);
        }
    }

    private boolean isUsernameExist(String username, Account account) {
        return !accountService.checkAvailableUsername(username, account);
    }

    public void onCancel(RowEditEvent event) {
        String summary = "Account '" + ((Account) event.getObject()).getUsername() + "' editing " +
                "cancelled";
        FacesMessagesUtil.addInfoMessageToFacesContext(summary);
    }

    public void onHideShowDisabled(ActionEvent actionEvent) {
        isDisabledIncluded = !isDisabledIncluded;
        loadAccounts();
    }

    public void onRefreshAccounts() {
        loadAccounts();
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

    public boolean isDisabledIncluded() {
        return isDisabledIncluded;
    }

    public void setDisabledIncluded(boolean disabledIncluded) {
        this.isDisabledIncluded = disabledIncluded;
    }
}
