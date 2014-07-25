package ua.yuris.restaurant.model.results;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 09.06.14
 * Time: 1:37
 * To change this template use File | Settings | File Templates.
 */
public class RevenueReport {
    private static final Logger LOG = LoggerFactory.getLogger(RevenueReport.class);

    private Date orderDate;
    private Long ordersNumber;
    private BigDecimal revenue;

    public RevenueReport() {
    }

    public RevenueReport(Date orderDate, Long ordersNumber, BigDecimal revenue) {
        this.orderDate = orderDate;
        this.ordersNumber = ordersNumber;
        this.revenue = revenue;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Long getOrdersNumber() {
        return ordersNumber;
    }

    public void setOrdersNumber(Long ordersNumber) {
        this.ordersNumber = ordersNumber;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Double getPrettyRevenue() {
        return revenue.divide(new BigDecimal(100.)).doubleValue();
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("orderDate", this.getOrderDate())
                .append("ordersNumber", this.getOrdersNumber())
                .append("revenue", this.getPrettyRevenue())
                .toString();
    }

}
