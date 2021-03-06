package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.ValidatorException;

import org.primefaces.component.inputtext.InputText;
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
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class AddAccountBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(AddAccountBackingBean.class);

    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;
    @ManagedProperty(value = "#{roleStateService}")
    private RoleStateService roleStateService;

    private Account account;

    public Set<Role> roles;

    public AddAccountBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        account = new Account();
        loadRoles();
    }

    private void loadRoles() {
        roles = roleStateService.findAllActiveRoles();
    }

    public String onCancel() {
        String summary = "Account creation cancelled";
        FacesMessagesUtil.addInfoMessageToFacesContext(summary);

        return "security-officer?faces-redirect=true";
    }

    public String onSave() {
        try {
            accountService.saveAccount(account);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            String summary = "Database operation failed";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
            return "";
        }

        return "security-officer?faces-redirect=true";
    }

    public void onUsernameChanged(AjaxBehaviorEvent event) {

        InputText inputText = (InputText) event.getSource();
        String username = (String) inputText.getValue();

        if (isUsernameExist(username)) {
            String summary = "Username '" + username + "' already exists";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
    }

    private boolean isUsernameExist(String username) {
        return !accountService.checkAvailableUsername(username);
    }

    public void usernameAvailabilityValidation(FacesContext context, UIComponent component,
                                               Object value) {
        if (value == null) return;

        if (isUsernameExist(value.toString())) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Username validation failed",
                    "Username " + value.toString() + " already exists");
            throw new ValidatorException(msg);
        }
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
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
