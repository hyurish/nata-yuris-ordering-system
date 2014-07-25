package ua.yuris.restaurant.web;

import java.io.Serializable;

import ua.yuris.restaurant.model.enums.OrderStatusType;

/**
 * Encapsulates the criteria needed to perform orders search.
 */
public class SearchCriteria
        implements Serializable {

    /**
     * The maximum page size of the Order result list
     */
    private int pageSize = 15;

    /**
     * The page the user is currently on.
     */
    private int currentPage = 0;

    /**
     * Hidden orders flag. Search disabled if true.
     */
    private boolean withDisabled = false;

    /**
     * Order Status search criteria.
     */
    private OrderStatusType selectedOrderStatus;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public OrderStatusType getSelectedOrderStatus() {
        return selectedOrderStatus;
    }

    public void setSelectedOrderStatus(OrderStatusType selectedOrderStatus) {
        this.selectedOrderStatus = selectedOrderStatus;
    }

    public boolean isWithDisabled() {
        return withDisabled;
    }

    public void setWithDisabled(boolean withDisabled) {
        this.withDisabled = withDisabled;
    }
}