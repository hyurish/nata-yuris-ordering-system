package ua.yuris.restaurant.service;

import java.util.List;

import ua.yuris.restaurant.model.Account;
import ua.yuris.restaurant.model.Order;
import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.RestaurantTable;
import ua.yuris.restaurant.model.enums.CookingPlaceType;
import ua.yuris.restaurant.model.enums.OrderStatusType;
import ua.yuris.restaurant.web.SearchCriteria;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 12.05.14
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
public interface OrderService {
    RestaurantTable findOne(Long restaurantTableId);

    List<RestaurantTable> findAllActiveTable();

    Order findOrder(Long orderId);

    List<Order> findAllOrderByWaiterIsNull();

    List<Order> findAllOrderByWaiterAndStatus(Account waiter, OrderStatusType status);

    List<Order> findAllActiveOrderByWaiterAndStatus(Account waiter, OrderStatusType status);

    List<Order> findOrders(SearchCriteria searchCriteria, int startingAt, String sortField, boolean ascending);

    int countOrders(SearchCriteria searchCriteria);

    Order getLastNoMansOrder();

    Order saveOrder(Order order);

    List<OrderDetail> findAllOrderInfoByCookedIsFalseAndOrderStatus(CookingPlaceType type, OrderStatusType status);

    OrderDetail saveOrderInfo(OrderDetail orderDetail);
}
