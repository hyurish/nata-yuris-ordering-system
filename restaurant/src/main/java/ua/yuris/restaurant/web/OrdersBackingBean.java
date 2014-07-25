package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;

import ua.yuris.restaurant.model.Account;
import ua.yuris.restaurant.model.Order;
import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.RestaurantTable;
import ua.yuris.restaurant.model.enums.OrderStatusType;
import ua.yuris.restaurant.service.AccountService;
import ua.yuris.restaurant.service.OrderService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 07.06.14
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class OrdersBackingBean
        implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(OrdersBackingBean.class);


    @ManagedProperty(value = "#{orderService}")
    private OrderService orderService;

    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;

    private OrderLazyDataModel orders;
    private SearchCriteria searchCriteria;

    private List<Account> waiters;
    private List<RestaurantTable> restaurantTables;
    private List<OrderStatusType> statuses = Arrays.asList(OrderStatusType.values());

    private Order selectedOrder;

    public OrdersBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        restaurantTables = orderService.findAllActiveTable();

        waiters = accountService.findAllAccountByRoleDescription("ROLE_WAITER");

        searchCriteria = new SearchCriteria();
        searchCriteria.setSelectedOrderStatus(OrderStatusType.SUBMITTED);

        orders = new OrderLazyDataModel(searchCriteria, orderService);
    }

    public void onEdit(RowEditEvent event) {
        Order order = (Order) event.getObject();
        try {
            order = orderService.saveOrder(order);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed", "Database operation failed");
            return;
        }

        addInfoMessageToFacesContext("Order '" + order.getNumber() + "' Edited",
                "Order '" + order.getNumber() + "' Edited");
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
        addInfoMessageToFacesContext("Order '" + ((Order) event.getObject()).getNumber() +
                "' editing cancelled",
                "Order '" + ((Order) event.getObject()).getNumber() + "' editing cancelled");
    }

    public void onOrderInfoEdit(RowEditEvent event) {
        OrderDetail orderDetail = (OrderDetail) event.getObject();
        try {
            orderDetail = orderService.saveOrderInfo(orderDetail);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed", "Database operation failed");
            return;
        }

        addInfoMessageToFacesContext("Order Details '" + orderDetail.getMenuItem().getTitle() +
                "' Edited", "Order Details '" + orderDetail.getMenuItem().getTitle() + "' Edited");
    }

    public void onOrderInfoCancel(RowEditEvent event) {
        addInfoMessageToFacesContext("Order Details '" +
                ((OrderDetail) event.getObject()).getMenuItem().getTitle() + "' editing cancelled",
                "Order Details '" + ((OrderDetail) event.getObject()).getMenuItem().getTitle() +
                        "' editing cancelled");
    }

    public void serviceStateChange() {
        orders = new OrderLazyDataModel(searchCriteria, orderService);
    }

    public void onHideShowDisabled(ActionEvent actionEvent) {
        searchCriteria.setWithDisabled(!searchCriteria.isWithDisabled());
        orders = new OrderLazyDataModel(searchCriteria, orderService);
    }

    public void onRefreshAccounts() {
        orders = new OrderLazyDataModel(searchCriteria, orderService);
    }

    public void editOrderDetails(Order order) {
        setSelectedOrder(order);
    }

    public OrderLazyDataModel getOrders() {
        return orders;
    }

    public void setOrders(OrderLazyDataModel orders) {
        this.orders = orders;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public List<RestaurantTable> getRestaurantTables() {
        return restaurantTables;
    }

    public void setRestaurantTables(List<RestaurantTable> restaurantTables) {
        this.restaurantTables = restaurantTables;
    }

    public List<OrderStatusType> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<OrderStatusType> statuses) {
        this.statuses = statuses;
    }

    public List<Account> getWaiters() {
        return waiters;
    }

    public void setWaiters(List<Account> waiters) {
        this.waiters = waiters;
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }
}
