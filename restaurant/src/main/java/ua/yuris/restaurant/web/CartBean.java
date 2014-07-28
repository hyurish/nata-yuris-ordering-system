package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;

import ua.yuris.restaurant.exception.CartOperationRestaurantException;
import ua.yuris.restaurant.exception.DataAccessRestaurantException;
import ua.yuris.restaurant.exception.IllegalArgumentRestaurantException;
import ua.yuris.restaurant.exception.RestaurantException;
import ua.yuris.restaurant.model.MenuItem;
import ua.yuris.restaurant.model.Order;
import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.RestaurantTable;
import ua.yuris.restaurant.model.enums.OrderStatusType;
import ua.yuris.restaurant.service.OrderService;
import ua.yuris.restaurant.web.cartstate.CartState;
import ua.yuris.restaurant.web.cartstate.CartStateFactory;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 02.07.14
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
/*
 * CartBean
 */
@ManagedBean
@SessionScoped
public class CartBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(CartBean.class);

    @ManagedProperty(value = "#{orderService}")
    private OrderService orderService;

    private Order order;

    private CartState cartState;
    private boolean isCartStateChanged = true;

    public CartBean() {
        order = new Order(OrderStatusType.SUBMITTED);
    }

    public double getTotal() {
        int total = 0;
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            total += orderDetail.getItemQuantity() * orderDetail.getItemPrice();
        }
        return total / 100.0;
    }

    public boolean isNewItemInCart(MenuItem menuItem) {
        return findNewOrderDetail(menuItem) != null;
    }

    public Integer countNewItemsInCart(MenuItem menuItem) {
        int nItems = 0;
        OrderDetail orderDetail = findNewOrderDetail(menuItem);
        if (orderDetail != null) {
            nItems = orderDetail.getItemQuantity();
        }
        return nItems;
    }

    private OrderDetail findNewOrderDetail(MenuItem menuItem) {
        OrderDetail orderDetail = null;
        for (OrderDetail detail : order.getOrderDetails()) {
            if (detail.isNewEntity() && detail.getMenuItem().equals(menuItem)) {
                orderDetail = detail;
                break;
            }
        }
        return orderDetail;
    }

    /**
     * Adds one menu item to cart by increasing menu item quantity of corresponding new
     * OrderDetail if it exists, or otherwise creates new OrderDetail object for given menu item.
     */
    public void addItemToCart(MenuItem menuItem) throws RestaurantException {
        if (menuItem == null) {
            throw new IllegalArgumentRestaurantException("null isn't valid argument");
        }

        OrderDetail orderDetail = findNewOrderDetail(menuItem);
        if (orderDetail != null) {
            orderDetail.setItemQuantity(orderDetail.getItemQuantity() + 1);
        } else {
            orderDetail = createOrderDetail(menuItem);
            order.addOrderDetail(orderDetail);
        }
        isCartStateChanged = true;
    }

    private OrderDetail createOrderDetail(MenuItem menuItem) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setMenuItem(menuItem);
        orderDetail.setItemQuantity(1);
        orderDetail.setItemPrice(menuItem.getPrice());
        return orderDetail;
    }

    /**
     * Adds one menu item to cart by increasing menu item quantity of new OrderDetail (id == null)
     * if it exists in the cart, or otherwise throws CartOperationRestaurantException.
     */
    public void addItemToCart(OrderDetail orderDetail) throws RestaurantException {
        checkEntityNotNullAndNew(orderDetail);

        List<OrderDetail> orderDetails = order.getOrderDetails();
        if (orderDetails.contains(orderDetail)) {
            orderDetail.setItemQuantity(orderDetail.getItemQuantity() + 1);
        } else {
            throw new CartOperationRestaurantException("OrderDetail not found in the cart");
        }
        isCartStateChanged = true;
    }

    private void checkEntityNotNullAndNew(OrderDetail orderDetail) throws RestaurantException {
        if (orderDetail == null || !orderDetail.isNewEntity()) {
            throw new IllegalArgumentRestaurantException("Argument is not a new entity");
        }
    }

    /**
     * Reduces menu items quantity of the corresponding new OrderDetail by one.
     * Removes corresponding new OrderDetail object from cart if its menu items quantity equals 1.
     * If cart doesn't contain corresponding new OrderDetail throws CartOperationRestaurantException
     */
    public void takeItemFromCart(MenuItem menuItem) throws RestaurantException {
        OrderDetail orderDetail = findNewOrderDetail(menuItem);
        if (orderDetail == null) {
            throw new CartOperationRestaurantException("MenuItem not found in the cart");
        }

        if (orderDetail.getItemQuantity() > 1) {
            orderDetail.setItemQuantity(orderDetail.getItemQuantity() - 1);
        } else if (orderDetail.getItemQuantity() == 1) {
            List<OrderDetail> orderDetails = order.getOrderDetails();
            orderDetails.remove(orderDetail);
        }
        isCartStateChanged = true;
    }

    /**
     * Reduces menu items quantity of the new OrderDetail (id == null) by one.
     * Removes new OrderDetail object from cart if its menu items quantity equals 1.
     */
    public void takeItemFromCart(OrderDetail orderDetail) throws RestaurantException {
        checkEntityNotNullAndNew(orderDetail);

        List<OrderDetail> orderDetails = order.getOrderDetails();
        if (!orderDetails.contains(orderDetail)) {
            throw new CartOperationRestaurantException("OrderDetail not found in the cart");
        }

        if (orderDetail.getItemQuantity() > 1) {
            orderDetail.setItemQuantity(orderDetail.getItemQuantity() - 1);
        } else if (orderDetail.getItemQuantity() == 1) {
            orderDetails.remove(orderDetail);
        }
        isCartStateChanged = true;
    }

    /*
     * Removes new OrderDetail (id == null) from the cart if it exists in the cart,
     * or otherwise throws CartOperationRestaurantException. .
     */
    public void removeNewOrderDetailFromCart(OrderDetail orderDetail) throws RestaurantException {
        checkEntityNotNullAndNew(orderDetail);

        List<OrderDetail> orderDetails = order.getOrderDetails();
        if (orderDetails.contains(orderDetail)) {
            orderDetails.remove(orderDetail);
        } else {
            throw new CartOperationRestaurantException("OrderDetail not found in the cart");
        }
        isCartStateChanged = true;
    }

    public void saveOrder(String guestName, RestaurantTable restaurantTable, String comments)
            throws RestaurantException {
        populateOrder(guestName, restaurantTable, comments);
        try {
            order = orderService.saveOrder(order);
        } catch (TransactionException e) {
            LOG.error("Order '" + order.toString() + "' saving failed with error message: " +
                    e.getMessage());
            throw new DataAccessRestaurantException("Order saving failed",e);
        }
        isCartStateChanged =true;
    }

    private void populateOrder(String guestName, RestaurantTable restaurantTable, String comments) {
        if (order.getRestaurantTable() == null) {
            order.setRestaurantTable(restaurantTable);
        }
        if (order.getGuestName() == null) {
            order.setGuestName(guestName);
        }
        if (comments != null && !comments.equals("")) {
            order.setComments(comments);
        }
        if (order.getOrderTime() == null) {
            order.setOrderTime(new DateTime());
        }
    }

    /*
     * Cart is editable until bill is issued.
     */
    public boolean isCartEditable() {
        return order.getStatus() == null || order.getStatus() == OrderStatusType.SUBMITTED;
    }

    /*
     * Refresh order if it's not being edited now to get possible changes made elsewhere.
     */
    public boolean isCartRefreshed() {
        if (countNewItems() == 0 && !order.isNewEntity()) {
            order = orderService.findOrder(order.getId());
            return true;
        }
        return false;
    }

    public CartState getCartState() {
        if (isCartStateChanged) {
            int nNewItems = countNewItems();
            int nConfirmedItems = countConfirmedItems();
            double totalOfNewItems = getTotalOfNewItems();
            double totalOfConfirmedItems = getTotalOfConfirmedItems();
            cartState = CartStateFactory.getCartState(nNewItems, nConfirmedItems,
                    totalOfNewItems, totalOfConfirmedItems);
        }
        return cartState;
    }

    private int countNewItems() {
        int nItems = 0;
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            if (orderDetail.isNewEntity()) {
                nItems += orderDetail.getItemQuantity();
            }
        }
        return nItems;
    }

    private int countConfirmedItems() {
        int nItems = 0;
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            if (!orderDetail.isNewEntity()) {
                nItems += orderDetail.getItemQuantity();
            }
        }
        return nItems;
    }

    private double getTotalOfNewItems() {
        int total = 0;
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            if (orderDetail.isNewEntity()) {
                total += orderDetail.getItemQuantity() * orderDetail.getItemPrice();
            }
        }
        return total / 100.0;
    }

    private double getTotalOfConfirmedItems() {
        int total = 0;
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            if (!orderDetail.isNewEntity()) {
                total += orderDetail.getItemQuantity() * orderDetail.getItemPrice();
            }
        }
        return total / 100.0;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}