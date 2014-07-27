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

    private String customerName;
    private RestaurantTable restaurantTable;
    private List<RestaurantTable> restaurantTables;
    private String comments;

    public OrderBackingBean() {
    }

    @PostConstruct
    public void initialize() {

        restaurantTables = orderService.findAllActiveTable();
        if (cartBean.getOrder().getRestaurantTable() == null) {
            restaurantTable = getDefaultRestaurantTable();
        } else {
            restaurantTable = cartBean.getOrder().getRestaurantTable();
        }
        if (cartBean.getOrder().getGuestName() != null) {
            customerName = cartBean.getOrder().getGuestName();
        }
    }

    private RestaurantTable getDefaultRestaurantTable() {
        return orderService.findRestaurantTable(DEFAULT_RESTAURANT_TABLE_ID);
    }

    public String getHeader() {
        CartState cartState = cartBean.getCartState();
        return cartState.getCartHeader();
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
            cartBean.saveOrder(customerName, restaurantTable, comments);
        } catch (RestaurantException e) {
            LOG.error(e.getMessage());
        }

    }

    public String closeOrder() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().
                getExternalContext().getSession(false);
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
