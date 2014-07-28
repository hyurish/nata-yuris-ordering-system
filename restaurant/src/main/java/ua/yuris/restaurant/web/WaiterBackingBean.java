package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

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
import ua.yuris.restaurant.util.FacesMessagesUtil;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 27.05.14
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class WaiterBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(WaiterBackingBean.class);
    private static final long FIRST_ORDER_IN_SYSTEM_ID = 1L;

    @ManagedProperty(value = "#{orderService}")
    private OrderService orderService;
    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;
    @ManagedProperty(value = "#{menuStateService}")
    private MenuStateService menuStateService;

    private List<Order> waiterOrders;
    private Order selectedOrder;
    private Order editableOrder;
    private OrderDetail selectedOrderDetail;

    private List<Order> noManOrders;
    private Order selectedNoManOrder;
    private Order lastNoManOrder;

    private boolean isDisabledIncluded;
    private OrderStatusType selectedOrderStatus;

    private List<Account> waiters;
    private Account currentWaiter;
    private List<RestaurantTable> restaurantTables;
    private MenuCategory selectedMenuCategory;
    private List<OrderStatusType> statuses = Arrays.asList(OrderStatusType.values());
    // Order statuses the currentWaiter can choose (except PAID)
    private List<OrderStatusType> restrictedStatuses;

    public WaiterBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        restaurantTables = orderService.findAllActiveTable();
        populateRestrictedStatuses();
        selectedOrderStatus = OrderStatusType.SUBMITTED;

        waiters = accountService.findAllAccountByRoleDescription("ROLE_WAITER");
        currentWaiter = getAuthorizedWaiter();
        if (currentWaiter != null) {
            loadWaiterOrders();
        }
        loadNoManOrders();
        lastNoManOrder = orderService.findOrder(FIRST_ORDER_IN_SYSTEM_ID);

    }

    private void populateRestrictedStatuses() {
        restrictedStatuses = new ArrayList<>();
        restrictedStatuses.add(OrderStatusType.SUBMITTED);
        restrictedStatuses.add(OrderStatusType.UNPAID);
    }

    private void loadWaiterOrders() {
        if (isDisabledIncluded) {
            waiterOrders = orderService.findAllOrderByWaiterAndStatus(currentWaiter,
                    selectedOrderStatus);
        } else {
            waiterOrders = orderService.findAllActiveOrderByWaiterAndStatus(currentWaiter,
                    selectedOrderStatus);
        }
    }

    private void loadNoManOrders() {
        noManOrders = orderService.findAllOrderByWaiterIsNull();
    }

    public void onRefreshWaiterOrder() {
        loadWaiterOrders();
    }

    public void onAddWaiterOrder() {
        editableOrder = createOrder();
    }

    private Order createOrder() {
        Order order = new Order(OrderStatusType.SUBMITTED);
        order.setOrderTime(new DateTime());
        Account waiter = getAuthorizedWaiter();
        if (waiter != null) {
            order.setWaiter(waiter);
        }
        return order;
    }

    public void serviceStateChange() {
        loadWaiterOrders();
    }

    public void onHideShowDisabled() {
        isDisabledIncluded = !isDisabledIncluded;
        loadWaiterOrders();
    }

    public void acceptOrder(Order order) {
        selectedNoManOrder = order;
        selectedNoManOrder.setWaiter(currentWaiter);

        try {
            orderService.saveOrder(selectedNoManOrder);
            loadNoManOrders();
            loadWaiterOrders();

            String summary = "Order '" + selectedNoManOrder.getNumber() + "' accepted";
            FacesMessagesUtil.addInfoMessageToFacesContext(summary);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            String summary = "Database operation failed";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
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
            loadWaiterOrders();
            editableOrder = getOrderCopy(selectedOrder);
            String summary = "Order '" + selectedOrder.getNumber() + "' printed";
            FacesMessagesUtil.addInfoMessageToFacesContext(summary);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            String summary = "Database operation failed";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
    }

    public boolean isPaid() {
        return editableOrder != null && editableOrder.getStatus() == OrderStatusType.PAID;
    }

    public void onCancel() {
        editableOrder = getOrderCopy(selectedOrder);
    }

    public void onSave() {
        selectedOrder = getOrderCopy(editableOrder);
        try {
            selectedOrder = orderService.saveOrder(selectedOrder);
            selectedOrderStatus = selectedOrder.getStatus();
            loadWaiterOrders();
            editableOrder = getOrderCopy(selectedOrder);

            String summary = "Order '" + selectedOrder.getNumber() + "' saved";
            FacesMessagesUtil.addInfoMessageToFacesContext(summary);
        } catch (TransactionException e) {
            LOG.error(e.getMessage());
            String summary = "Database operation failed";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
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
        try {
            checkEntityNotNullAndNew(orderDetail);
            decreaseOrRemoveMenuItem(orderDetail);
        } catch (RestaurantException e) {
            LOG.error(e.getMessage());
            String summary = "Menu item quantity can't be reduced";
            FacesMessagesUtil.addErrorMessageToFacesContext(summary);
        }
    }

    private void decreaseOrRemoveMenuItem(OrderDetail orderDetail) {
        if (orderDetail.getItemQuantity() == 1) {
            editableOrder.getOrderDetails().remove(orderDetail);
        } else if (orderDetail.getItemQuantity() > 1) {
            orderDetail.setItemQuantity(orderDetail.getItemQuantity() - 1);
        } else {
            throw new RestaurantException("Menu item quantity less then 1");
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
        if (order != null && order.getOrderTime().isAfter(lastNoManOrder.getOrderTime())) {
            setLastNoManOrder(order);
            loadNoManOrders();
            RequestContext.getCurrentInstance().update("nobodyOrdersForm");
        }
    }

    public void onCookedCheck() {
        loadWaiterOrders();
        RequestContext.getCurrentInstance().update("waiterOrdersForm");
    }

    private Account getAuthorizedWaiter() {
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
            detail = getOrderDetailCopy(orderDetail);
            detail.setOrder(orderCopy);
            orderDetails.add(detail);
        }
        orderCopy.setOrderDetails(orderDetails);
        return orderCopy;
    }

    private OrderDetail getOrderDetailCopy(OrderDetail orderDetail) {
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

    public List<Order> getNoManOrders() {
        return noManOrders;
    }

    public void setNoManOrders(List<Order> noManOrders) {
        this.noManOrders = noManOrders;
    }

    public Order getSelectedNoManOrder() {
        return selectedNoManOrder;
    }

    public void setSelectedNoManOrder(Order selectedNoManOrder) {
        this.selectedNoManOrder = selectedNoManOrder;
    }

    public OrderStatusType getSelectedOrderStatus() {
        return selectedOrderStatus;
    }

    public void setSelectedOrderStatus(OrderStatusType selectedOrderStatus) {
        this.selectedOrderStatus = selectedOrderStatus;
    }

    public Account getCurrentWaiter() {
        return currentWaiter;
    }

    public void setCurrentWaiter(Account currentWaiter) {
        this.currentWaiter = currentWaiter;
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

    public boolean isDisabledIncluded() {
        return isDisabledIncluded;
    }

    public void setDisabledIncluded(boolean disabledIncluded) {
        this.isDisabledIncluded = disabledIncluded;
    }

    public Order getLastNoManOrder() {
        return lastNoManOrder;
    }

    public void setLastNoManOrder(Order lastNoManOrder) {
        this.lastNoManOrder = lastNoManOrder;
    }
}
