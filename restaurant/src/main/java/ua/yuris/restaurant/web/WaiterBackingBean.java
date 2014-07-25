package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionException;

import ua.yuris.restaurant.exception.IllegalArgumentRestaurantException;
import ua.yuris.restaurant.exception.RestaurantException;
import ua.yuris.restaurant.model.Account;
import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.model.MenuItem;
import ua.yuris.restaurant.model.Order;
import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.RestaurantTable;
import ua.yuris.restaurant.model.enums.OrderStatusType;
import ua.yuris.restaurant.service.AccountService;
import ua.yuris.restaurant.service.MenuStateService;
import ua.yuris.restaurant.service.OrderService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 27.05.14
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class WaiterBackingBean
        implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(WaiterBackingBean.class);
    private static final long FIRST_ORDER_IN_SYSTEM_ID = 1L;

    @ManagedProperty(value = "#{orderService}")
    private OrderService orderService;

    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;

    @ManagedProperty(value = "#{menuStateService}")
    private MenuStateService menuStateService;

    private Account waiter;
    private List<Order> waiterOrders;
    private Order selectedOrder;

    private Order selectedNobodyOrder;
    private List<Order> nobodyOrders;

    private boolean withDisabled;
    private OrderStatusType selectedOrderStatus;

    private Order editableOrder;
    private List<Account> waiters;
    private List<RestaurantTable> restaurantTables;
    private List<OrderStatusType> statuses = Arrays.asList(OrderStatusType.values());
    private List<OrderStatusType> restrictedStatuses = new ArrayList<>();

    private MenuCategory selectedMenuCategory;
    private OrderDetail selectedOrderDetail;

    private Order lastNoMansOrder;

    public WaiterBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        restaurantTables = orderService.findAllActiveTable();

        restrictedStatuses.add(OrderStatusType.SUBMITTED);
        restrictedStatuses.add(OrderStatusType.UNPAID);

        selectedOrderStatus = OrderStatusType.SUBMITTED;

        waiters = accountService.findAllAccountByRoleDescription("ROLE_WAITER");
        waiter = getCurrentWaiter();
        if (waiter != null) {
            setWaiterOrders(loadOrders(selectedOrderStatus, withDisabled, waiter));
        }
        setNobodyOrders(loadNobodyOrders());
        lastNoMansOrder = orderService.findOrder(FIRST_ORDER_IN_SYSTEM_ID);

    }

    private List<Order> loadOrders(OrderStatusType selectedOrderStatus, boolean withDisabled,
                                   Account waiter) {
        if (withDisabled) {
            return orderService.findAllOrderByWaiterAndStatus(waiter, selectedOrderStatus);
        } else {
            return orderService.findAllActiveOrderByWaiterAndStatus(waiter, selectedOrderStatus);
        }
    }

    private List<Order> loadNobodyOrders() {
        return orderService.findAllOrderByWaiterIsNull();
    }

    public void onRefreshWaiterOrder() {
        setWaiterOrders(loadOrders(selectedOrderStatus, withDisabled, waiter));
    }

    public void onAddWaiterOrder() {
        editableOrder = new Order();
        editableOrder.setOrderTime(new DateTime());
        Account waiter = getCurrentWaiter();
        if (waiter != null) {
            editableOrder.setWaiter(waiter);
        }
    }

    public void serviceStateChange() {
        setWaiterOrders(loadOrders(selectedOrderStatus, withDisabled, waiter));
    }

    public void onHideShowDisabled() {
        withDisabled = !withDisabled;
        setWaiterOrders(loadOrders(selectedOrderStatus, withDisabled, waiter));
    }

    public void acceptOrder(Order order) {
        setSelectedNobodyOrder(order);
        selectedNobodyOrder.setWaiter(waiter);

        try {
            orderService.saveOrder(selectedNobodyOrder);
            setNobodyOrders(loadNobodyOrders());
            setWaiterOrders(loadOrders(selectedOrderStatus, withDisabled, waiter));
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed", "Database operation failed");
            return;
        }
        addInfoMessageToFacesContext("Order '" + selectedNobodyOrder.getNumber() + "' accepted",
                "Order '" + selectedNobodyOrder.getNumber() + "' accepted");
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

    public void onRowSelect(SelectEvent event) {
        editableOrder = getOrderCopy(selectedOrder);
    }

    public String getOrderStatusColorClass(Order order) {
        switch (order.getStatus()) {
            case SUBMITTED:
                return "greenIcon";
            case UNPAID:
                return "redIcon";
            default:
                return "greyIcon";
        }
    }

    public void onRefreshOrder() {
        selectedOrder = orderService.findOrder(selectedOrder.getId());
        editableOrder = getOrderCopy(selectedOrder);
    }

    public void onPrint() {
        selectedOrder.setStatus(OrderStatusType.PAID);
        try {
            orderService.saveOrder(selectedOrder);
            selectedOrderStatus = selectedOrder.getStatus();
            setWaiterOrders(loadOrders(selectedOrderStatus, withDisabled, waiter));
            editableOrder = getOrderCopy(selectedOrder);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed", "Database operation failed");
            return;
        }
        addInfoMessageToFacesContext("Order '" + selectedOrder.getNumber() + "' printed",
                "Order '" + selectedOrder.getNumber() + "' printed");
    }

    public boolean isPaid() {
        if (editableOrder.getStatus() != null && editableOrder.getStatus() == OrderStatusType.PAID) {
            return true;
        }
        return false;
    }

    public void onCancel() {
        editableOrder = getOrderCopy(selectedOrder);
    }

    public void onSave() {
        selectedOrder = getOrderCopy(editableOrder);
        try {
            selectedOrder = orderService.saveOrder(selectedOrder);
            selectedOrderStatus = selectedOrder.getStatus();
            setWaiterOrders(loadOrders(selectedOrderStatus, withDisabled, waiter));
            editableOrder = getOrderCopy(selectedOrder);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext("Database operation failed", "Database operation failed");
            return;
        }
        addInfoMessageToFacesContext("Order '" + selectedOrder.getNumber() + "' saved",
                "Order '" + selectedOrder.getNumber() + "' saved");
    }

    public void onBack() {
        selectedMenuCategory = null;
    }

    public void serviceCategoryChosen(MenuCategory menuCategory) {
        selectedMenuCategory = menuCategory;
    }

    public void serviceMenuItemChosen(MenuItem menuItem) {
        if (editableOrder != null) {
            OrderDetail orderDetail = findNewOrderDetail(editableOrder.getOrderDetails(), menuItem);
            if (orderDetail != null) {
                orderDetail.setItemQuantity(orderDetail.getItemQuantity() + 1);
            } else {
                orderDetail = createOrderDetail(menuItem);
                editableOrder.addOrderDetail(orderDetail);
            }
        }
    }

    private OrderDetail createOrderDetail(MenuItem menuItem) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setMenuItem(menuItem);
        orderDetail.setItemQuantity(1);
        orderDetail.setItemPrice(menuItem.getPrice());
        return orderDetail;
    }

    private OrderDetail findNewOrderDetail(List<OrderDetail> orderDetails, MenuItem menuItem) {
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.isNewEntity() && orderDetail.getMenuItem().equals(menuItem)) {
                return orderDetail;
            }
        }
        return null;
    }

    public void onTakeMenuItem(OrderDetail orderDetail) {
        try{
            checkEntityNotNullAndNew(orderDetail);
        } catch (RestaurantException e) {
            LOG.error(e.getMessage());
            addErrorMessageToFacesContext(e.getMessage(), e.getMessage());
            return;
        }

        if (orderDetail.getItemQuantity() == 1) {
            editableOrder.getOrderDetails().remove(orderDetail);
        }
        if (orderDetail.getItemQuantity() > 1) {
            orderDetail.setItemQuantity(orderDetail.getItemQuantity() - 1);
        }
    }

    private void checkEntityNotNullAndNew(OrderDetail orderDetail) throws RestaurantException{
        if (orderDetail == null || !orderDetail.isNewEntity()) {
            throw new IllegalArgumentRestaurantException("Argument is not a new entity");
        }
    }

    public void onRemoveMenuItem(OrderDetail orderDetail) {
        if (orderDetail.isNewEntity()) {
            editableOrder.getOrderDetails().remove(orderDetail);
        }
    }

    public boolean isRemovePossible(OrderDetail orderDetail) {
        return orderDetail.getItemQuantity() > 0;
    }

    public void onAddMenuItem(OrderDetail orderDetail) {
        orderDetail.setItemQuantity(orderDetail.getItemQuantity() + 1);
    }

    public void onNoMansOrdersCheck() {
        Order order = orderService.getLastNoMansOrder();
        if (order != null && order.getOrderTime().isAfter(lastNoMansOrder.getOrderTime())) {
            setLastNoMansOrder(order);
            setNobodyOrders(loadNobodyOrders());
            RequestContext.getCurrentInstance().update("nobodyOrdersForm");
        }
    }

    public void onCookedCheck() {
        setWaiterOrders(loadOrders(selectedOrderStatus, withDisabled, waiter));
        RequestContext.getCurrentInstance().update("waiterOrdersForm");
    }

    private Account getCurrentWaiter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Account currentUser = (Account) authentication.getPrincipal();
        for (GrantedAuthority authority : currentUser.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_WAITER")) {
                return currentUser;
            }
        }
        return null;
    }

    private Order getOrderCopy(Order order) {
        if (order == null) {
            return null;
        }
        Order orderCopy = new Order();
        orderCopy.setId(order.getId());
        orderCopy.setGuest(order.getGuest());
        orderCopy.setGuestName(order.getGuestName());
        orderCopy.setWaiter(order.getWaiter());
        orderCopy.setOrderTime(order.getOrderTime());
        orderCopy.setRestaurantTable(order.getRestaurantTable());
        orderCopy.setStatus(order.getStatus());
        orderCopy.setComments(order.getComments());
        orderCopy.setCreateTime(order.getCreateTime());
        orderCopy.setActive(order.getActive());
        List<OrderDetail> orderDetails = new ArrayList<>();
        OrderDetail detail;
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            detail = getOrderDetail(orderDetail);
            detail.setOrder(orderCopy);
            orderDetails.add(detail);
        }
        orderCopy.setOrderDetails(orderDetails);
        return orderCopy;
    }

    private OrderDetail getOrderDetail(OrderDetail orderDetail) {
        OrderDetail orderDetailCopy = new OrderDetail();
        orderDetailCopy.setItemQuantity(orderDetail.getItemQuantity());
        orderDetailCopy.setCooked(orderDetail.getCooked());
        orderDetailCopy.setCreateTime(orderDetail.getCreateTime());
        orderDetailCopy.setActive(orderDetail.getActive());
        orderDetailCopy.setMenuItem(orderDetail.getMenuItem());
        orderDetailCopy.setItemPrice(orderDetail.getItemPrice());
        orderDetailCopy.setId(orderDetail.getId());
        return orderDetailCopy;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<Order> getWaiterOrders() {
        return waiterOrders;
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

    public void setWaiterOrders(List<Order> waiterOrders) {
        this.waiterOrders = waiterOrders;
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public List<Order> getNobodyOrders() {
        return nobodyOrders;
    }

    public void setNobodyOrders(List<Order> nobodyOrders) {
        this.nobodyOrders = nobodyOrders;
    }

    public Order getSelectedNobodyOrder() {
        return selectedNobodyOrder;
    }

    public void setSelectedNobodyOrder(Order selectedNobodyOrder) {
        this.selectedNobodyOrder = selectedNobodyOrder;
    }

    public OrderStatusType getSelectedOrderStatus() {
        return selectedOrderStatus;
    }

    public void setSelectedOrderStatus(OrderStatusType selectedOrderStatus) {
        this.selectedOrderStatus = selectedOrderStatus;
    }

    public Account getWaiter() {
        return waiter;
    }

    public void setWaiter(Account waiter) {
        this.waiter = waiter;
    }

    public Order getEditableOrder() {
        return editableOrder;
    }

    public void setEditableOrder(Order editableOrder) {
        this.editableOrder = editableOrder;
    }

    public List<RestaurantTable> getRestaurantTables() {
        return restaurantTables;
    }

    public void setRestaurantTables(List<RestaurantTable> restaurantTables) {
        this.restaurantTables = restaurantTables;
    }

    public List<OrderStatusType> getRestrictedStatuses() {
        return restrictedStatuses;
    }

    public void setRestrictedStatuses(List<OrderStatusType> restrictedStatuses) {
        this.restrictedStatuses = restrictedStatuses;
    }

    public List<OrderStatusType> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<OrderStatusType> statuses) {
        this.statuses = statuses;
    }

    public MenuStateService getMenuStateService() {
        return menuStateService;
    }

    public void setMenuStateService(MenuStateService menuStateService) {
        this.menuStateService = menuStateService;
    }

    public List<MenuCategory> getCategories() {
        return menuStateService.findAllActiveCategories();
    }

    public List<MenuItem> getMenuItems() {
        return menuStateService.findAllActiveMenuItemByCategory(selectedMenuCategory);
    }

    public MenuCategory getSelectedMenuCategory() {
        return selectedMenuCategory;
    }

    public void setSelectedMenuCategory(MenuCategory selectedMenuCategory) {
        this.selectedMenuCategory = selectedMenuCategory;
    }

    public OrderDetail getSelectedOrderDetail() {
        return selectedOrderDetail;
    }

    public void setSelectedOrderDetail(OrderDetail selectedOrderDetail) {
        this.selectedOrderDetail = selectedOrderDetail;
    }

    public boolean isWithDisabled() {
        return withDisabled;
    }

    public void setWithDisabled(boolean withDisabled) {
        this.withDisabled = withDisabled;
    }

    public Order getLastNoMansOrder() {
        return lastNoMansOrder;
    }

    public void setLastNoMansOrder(Order lastNoMansOrder) {
        this.lastNoMansOrder = lastNoMansOrder;
    }
}
