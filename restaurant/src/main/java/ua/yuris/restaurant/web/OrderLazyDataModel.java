package ua.yuris.restaurant.web;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ua.yuris.restaurant.model.Order;
import ua.yuris.restaurant.service.OrderService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 03.07.14
 * Time: 1:48
 * To change this template use File | Settings | File Templates.
 */
public class OrderLazyDataModel extends LazyDataModel<Order> {

    private OrderService orderService;

    private SearchCriteria searchCriteria;

    private List<Order> orders;

    private Order selectedOrder;

    public OrderLazyDataModel() {
    }

    public OrderLazyDataModel(SearchCriteria searchCriteria, OrderService orderService) {
        this.searchCriteria = searchCriteria;
        this.orderService = orderService;
    }

    @Override
    public List<Order> load(int startingAt, int pageSize, String sortField, SortOrder sortOrder,
                            Map<String, String> filters) {
        this.searchCriteria.setCurrentPage(startingAt / pageSize);
        this.searchCriteria.setPageSize(pageSize);
        this.orders = orderService.findOrders(searchCriteria, startingAt, sortField,
                sortOrder.equals(SortOrder.ASCENDING));
        return orders;
    }

    @Override
    public Order getRowData(String rowKey) {
        for (Order order : this.orders) {
            if (order.getId().equals(rowKey)) {
                return order;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Order order) {
        return order.getId();
    }

    @Override
    public int getRowCount() {
        return orderService.countOrders(searchCriteria);
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public int getCurrentPage() {
        return this.searchCriteria.getCurrentPage();
    }

    public int getPageSize() {
        return this.searchCriteria.getPageSize();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }
}