package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;

import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.enums.CookingPlaceType;
import ua.yuris.restaurant.model.enums.OrderStatusType;
import ua.yuris.restaurant.service.AccountService;
import ua.yuris.restaurant.service.OrderService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 02.06.14
 * Time: 12:26
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class CookBackingBean
        implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(CookBackingBean.class);

    @ManagedProperty(value = "#{orderService}")
    private OrderService orderService;

    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;

    private List<OrderDetail> notCookedOrderDetails;

    private int orderCount;
    private Long lastOrderId;

    public CookBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        setNotCookedOrderDetails(loadNotCookedOrderDetails());
    }

    private List<OrderDetail> loadNotCookedOrderDetails() {
        orderCount = 0;
        lastOrderId = 0L;
        return orderService.findAllOrderInfoByCookedIsFalseAndOrderStatus(CookingPlaceType.KITCHEN,
                OrderStatusType.SUBMITTED);
    }

    public String getOrderDetailRowStyleClass(OrderDetail orderDetail) {
        if (!lastOrderId.equals(orderDetail.getOrder().getId())) {
            orderCount++;
            lastOrderId = orderDetail.getOrder().getId();
        }
        int i = orderCount % 3;
        switch (i) {
            case 0:
                return "green-bg";
            case 1:
                return "blue-bg";
            case 2:
                return "red-bg";
        }
        return "";
    }

    public void serviceCookedValueChange(OrderDetail orderDetail) {
        try {
            orderService.saveOrderInfo(orderDetail);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed",
                    "Database operation failed");
            return;
        }

        addInfoMessageToFacesContext("Cooked status changed: " +
                orderDetail.getMenuItem().getTitle(),
                "Cooked status changed: " + orderDetail.getMenuItem().getTitle());
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
    public void onNewOrdersCheck() {
        setNotCookedOrderDetails(loadNotCookedOrderDetails());
        RequestContext.getCurrentInstance().update("orderDetailsForm");
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<OrderDetail> getNotCookedOrderDetails() {
        return notCookedOrderDetails;
    }

    public void setNotCookedOrderDetails(List<OrderDetail> notCookedOrderDetails) {
        this.notCookedOrderDetails = notCookedOrderDetails;
    }
}
