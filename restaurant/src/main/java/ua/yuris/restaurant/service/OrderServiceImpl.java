package ua.yuris.restaurant.service;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ua.yuris.restaurant.model.Account;
import ua.yuris.restaurant.model.Order;
import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.RestaurantTable;
import ua.yuris.restaurant.model.enums.CookingPlaceType;
import ua.yuris.restaurant.model.enums.OrderStatusType;
import ua.yuris.restaurant.repository.OrderDetailRepository;
import ua.yuris.restaurant.repository.OrderRepository;
import ua.yuris.restaurant.repository.RestaurantTableRepository;
import ua.yuris.restaurant.web.SearchCriteria;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 12.05.14
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
@Named("orderService")
@Transactional(readOnly = true)
public class OrderServiceImpl
        implements OrderService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Inject
    private RestaurantTableRepository restaurantTableRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private OrderDetailRepository orderDetailRepository;

    public OrderServiceImpl() {
    }

    @Override
    public RestaurantTable findOne(Long restaurantTableId) {
        return restaurantTableRepository.findOne(restaurantTableId);
    }

    @Override
    public List<RestaurantTable> findAllActiveTable() {
        return restaurantTableRepository.findByActiveTrueOrderByTableNumberAsc();
    }

    @Override
    public Order findOrder(Long orderId) {
        return orderRepository.findOne(orderId);
    }

    @Override
    public List<Order> findAllOrderByWaiterIsNull() {
        return orderRepository.findByActiveTrueAndWaiterIdIsNullOrderByOrderTimeDesc();
    }

    @Override
    public List<Order> findAllActiveOrderByWaiterAndStatus(Account waiter, OrderStatusType status) {
        List<Order> orders =
                orderRepository.findByActiveTrueAndWaiterIdAndStatusOrderByOrderTimeDesc(
                        waiter.getId(), status
                );
        excludeDisabledOrderDetails(orders);
        return orders;
    }

    private void excludeDisabledOrderDetails(List<Order> orders) {
        for (Order order : orders) {
            for (Iterator<OrderDetail> it = order.getOrderDetails().iterator(); it.hasNext(); ) {
                OrderDetail orderDetail = it.next();
                if (!orderDetail.getActive()) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public List<Order> findAllOrderByWaiterAndStatus(Account waiter, OrderStatusType status) {
        return orderRepository.findByWaiterIdAndStatusOrderByOrderTimeDesc(waiter.getId(),
                status);
    }

    @Override
    public List<Order> findOrders(SearchCriteria searchCriteria, int startingAt, String sortField,
                                  boolean ascending) {
        Pageable page = createPage(searchCriteria, sortField, ascending);

        if (searchCriteria.isWithDisabled()) {
            return orderRepository.findByStatus(
                    searchCriteria.getSelectedOrderStatus(), page);
        } else {
            return orderRepository.findByActiveTrueAndStatus(
                    searchCriteria.getSelectedOrderStatus(), page);
        }
    }

    private Pageable createPage(SearchCriteria searchCriteria, String sortField, boolean ascending) {
        Sort sort = null;
        if (sortField != null) {
            sort = new Sort((ascending) ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        } else {
            sort = new Sort(Sort.Direction.DESC, "orderTime");
        }
        return new PageRequest(searchCriteria.getCurrentPage(),
                searchCriteria.getPageSize(), sort);
    }

    @Override
    public int countOrders(SearchCriteria searchCriteria) {
        if (searchCriteria.isWithDisabled()) {
            return orderRepository.countByStatus(
                    searchCriteria.getSelectedOrderStatus()).intValue();
        } else {
            return orderRepository.countByActiveTrueAndStatus(
                    searchCriteria.getSelectedOrderStatus()).intValue();
        }
    }

    @Override
    public Order getLastNoMansOrder() {
        List<Order> lastNoMansOrders = orderRepository.findLastNoMansOrder();
        if (!lastNoMansOrders.isEmpty()) {
            return lastNoMansOrders.get(0);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public Order saveOrder(Order order) {
        Assert.notNull(order, "The entity must not be null!");

        return orderRepository.save(order);
    }

    @Override
    public List<OrderDetail>
        findAllOrderInfoByCookedIsFalseAndOrderStatus(CookingPlaceType cookingPlace,
                                                  OrderStatusType status) {
        return orderDetailRepository.
                findByCookedIsFalseAndActiveTrueAndOrderActiveTrueAndMenuItemCookingPlaceAndOrderStatusOrderByOrderOrderTimeAsc
                        (cookingPlace, status);
    }

    @Override
    @Transactional(readOnly = false)
    public OrderDetail saveOrderInfo(OrderDetail orderDetail) {
        Assert.notNull(orderDetail, "The entity must not be null!");

        return orderDetailRepository.save(orderDetail);
    }
}
