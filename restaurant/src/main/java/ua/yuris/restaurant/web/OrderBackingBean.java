package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.yuris.restaurant.exception.RestaurantException;
import ua.yuris.restaurant.model.Order;
import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.RestaurantTable;
import ua.yuris.restaurant.service.OrderService;
import ua.yuris.restaurant.util.RestaurantTableUtil;
import ua.yuris.restaurant.web.cartstate.CartState;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 03.07.14
 * Time: 1:48
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class OrderBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(OrderBackingBean.class);
    private static final long DEFAULT_RESTAURANT_TABLE_ID =
            RestaurantTableUtil.getDefaultTableId();

    @ManagedProperty(value = "#{orderService}")
    private OrderService orderService;
    @ManagedProperty(value = "#{cartBean}")
    private CartBean cartBean;

    private Order currentOrder;

    private String guestName;
    private RestaurantTable restaurantTable;
    private List<RestaurantTable> restaurantTables;
    private String comments;

    public OrderBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        currentOrder = cartBean.getOrder();

        initializeRestaurantTable();
        initializeGuestName();
    }

    private void initializeRestaurantTable() {
        restaurantTables = orderService.findAllActiveTable();
        if (currentOrder.getRestaurantTable() != null) {
            restaurantTable = currentOrder.getRestaurantTable();
        } else {
            restaurantTable = getDefaultRestaurantTable();
        }
    }

    private RestaurantTable getDefaultRestaurantTable() {
        return orderService.findRestaurantTable(DEFAULT_RESTAURANT_TABLE_ID);
    }

    private void initializeGuestName() {
        if (currentOrder.getGuestName() != null) {
            guestName = currentOrder.getGuestName();
        }
    }

    public String getHeader() {
        CartState cartState = cartBean.getCartState();
        return cartState.getCartHeader();
    }

    public double getTotal() {
        return cartBean.getTotal();
    }

    public List<OrderDetail> getOrderDetails() {
        return currentOrder.getOrderDetails();
    }

    public void takeItem(OrderDetail orderDetail) {
        try {
            cartBean.takeItemFromCart(orderDetail);
        } catch (RestaurantException e) {
            LOG.error("Item '" + orderDetail.getMenuItem().getTitle() + "' not removed");
            LOG.error(e.getMessage());
        }
    }


    public void addItem(OrderDetail orderDetail) {
        try {
            cartBean.addItemToCart(orderDetail);
        } catch (RestaurantException e) {
            LOG.error("Item '" + orderDetail.getMenuItem().getTitle() + "' not added");
            LOG.error(e.getMessage());
        }
    }


    public void removeItem(OrderDetail orderDetail) {
        try {
            cartBean.removeNewOrderDetailFromCart(orderDetail);
        } catch (RestaurantException e) {
            LOG.error("OrderDetail '" + orderDetail.getMenuItem().getTitle() + "' not removed");
            LOG.error(e.getMessage());
        }
    }

    public boolean isSaveBtnEnabled() {
        CartState cartState = cartBean.getCartState();
        return cartState.isNewItemsIn();
    }

    public boolean isTakeBtnEnabled(OrderDetail orderDetail) {
        return orderDetail.getItemQuantity() > 1;
    }

    public void saveOrder() {
        try {
            cartBean.saveOrder(guestName, restaurantTable, comments);
            currentOrder = cartBean.getOrder();
        } catch (RestaurantException e) {
            LOG.error(e.getMessage());
        }

    }

    public String closeOrder() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "menu?faces-redirect=true";
    }

    public boolean isOrderEditable() {
        return cartBean.isCartEditable();
    }

    public void onOrderChangeCheck() {
        if (cartBean.isCartRefreshed()) {
            RequestContext.getCurrentInstance().update("orderForm");
        }
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public CartBean getCartBean() {
        return cartBean;
    }

    public void setCartBean(CartBean cartBean) {
        this.cartBean = cartBean;
    }

    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }

    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTable = restaurantTable;
    }

    public List<RestaurantTable> getRestaurantTables() {
        return restaurantTables;
    }

    public void setRestaurantTables(List<RestaurantTable> restaurantTables) {
        this.restaurantTables = restaurantTables;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
