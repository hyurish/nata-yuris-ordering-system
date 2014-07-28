package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;

import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.enums.CookingPlaceType;
import ua.yuris.restaurant.model.enums.OrderStatusType;
import ua.yuris.restaurant.service.AccountService;
import ua.yuris.restaurant.service.OrderService;
import ua.yuris.restaurant.util.FacesMessagesUtil;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 02.06.14
 * Time: 12:26
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class CookBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(CookBackingBean.class);
    private static final int NUMBER_OF_ORDER_DECORATION_STYLES = 3;

    @ManagedProperty(value = "#{orderService}")
    private OrderService orderService;
    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;

    private List<OrderDetail> notCookedOrderDetails;

    private int orderIndex;
    private Long lastOrderId = 0L;

    public CookBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        loadNotCookedOrderDetails();
    }

    private void loadNotCookedOrderDetails() {
        notCookedOrderDetails = orderService.findAllOrderInfoByCookedIsFalseAndOrderStatus(
                CookingPlaceType.KITCHEN, OrderStatusType.SUBMITTED);
    }

    public String getOrderDetailRowStyleClass(OrderDetail orderDetail) {
        fixOrderIndex(orderDetail);
        int decorationStyleIndex = orderIndex % NUMBER_OF_ORDER_DECORATION_STYLES;
        switch (decorationStyleIndex) {
            case 0:
                return "green-bg";
            case 1:
                return "blue-bg";
            case 2:
                return "red-bg";
        }
        return "";
    }

    private void fixOrderIndex(OrderDetail orderDetail) {
        Long currentOrderId = orderDetail.getOrder().getId();
        if (!lastOrderId.equals(currentOrderId)) {
            lastOrderId = currentOrderId;
            orderIndex++;
        }
    }

    public void serviceCookedValueChange(OrderDetail orderDetail) {
        try {
            orderService.saveOrderInfo(orderDetail);
            String summary = "Cooked status changed: " + orderDetail.getMenuItem().getTitle();
            FacesMessagesUtil.addInfoMessageToFacesContext(summary);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            String summary = "Database operation failed";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
    }

    public void onNewOrdersCheck() {
        loadNotCookedOrderDetails();
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
