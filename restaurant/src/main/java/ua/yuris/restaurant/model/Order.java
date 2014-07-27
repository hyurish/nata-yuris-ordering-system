package ua.yuris.restaurant.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;

import ua.yuris.restaurant.model.enums.OrderStatusType;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 28.04.14
 * Time: 0:09
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "Orders")
public class Order
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(Order.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="OrderId")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "GuestId")
    private Account guest;
    private String guestName;
    @ManyToOne
    @JoinColumn(name = "WaiterId")
    private Account waiter;
    @ManyToOne
    @JoinColumn(name = "RestaurantTableId")
    private RestaurantTable restaurantTable;
    @Column(columnDefinition = "TIMESTAMP")
    @Converter(name = "dateTimeConverter",
            converterClass = ua.yuris.restaurant.util.JodaDateTimeConverter.class)
    @Convert("dateTimeConverter")
    private DateTime orderTime;
    @Lob
    private String comments;
    @Enumerated(EnumType.STRING)
    private OrderStatusType status;
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "order")
    private List<OrderDetail> orderDetails;
    @Column(columnDefinition = "TIMESTAMP")
    @Converter(name = "dateTimeConverter",
            converterClass = ua.yuris.restaurant.util.JodaDateTimeConverter.class)
    @Convert("dateTimeConverter")
    private DateTime createTime;
    private Boolean active = Boolean.TRUE;

    public Order() {
        createTime = new DateTime();
    }

    public Order(OrderStatusType status) {
        this();
        this.status = status;
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        getOrderDetails().add(orderDetail);
        orderDetail.setOrder(this);
    }

    public String getNumber() {
        String number = "/";
        if (!isNewEntity()) {
            number += id;
        }
        if (restaurantTable != null) {
            number = restaurantTable.getTableNumber() + number;
        }

        return number;
    }

    public String getGuestPreciseName() {
        if (guest != null) {
            return guest.getFirstName();
        }
        if (guestName != null && !guestName.trim().equals("")) {
            return guestName;
        }
        return null;
    }

    public Double getTotal() {
        Integer total = 0;
        for (OrderDetail orderDetail : getOrderDetails()) {
            total += orderDetail.getItemPrice() * orderDetail.getItemQuantity() ;
        }
        return total / 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        if (getCreateTime() == null) return false;

        Order order = (Order) o;
        if(!(getCreateTime().equals(order.getCreateTime())))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return createTime != null ? createTime.hashCode() : 0;
    }

    public boolean isNewEntity() {
        return (this.id == null);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", this.getId())
                .append("new", this.isNewEntity())
                .append("customerName", this.getGuestName())
                .append("restaurantTable", this.getRestaurantTable())
                .append("waiter", this.getWaiter())
                .append("orderTime", this.getOrderTime())
                .append("status", this.getStatus())
                .append("createTime", this.getCreateTime())
                .append("getActive", this.getActive())
                .toString();
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getGuest() {
        return guest;
    }

    public void setGuest(Account user) {
        this.guest = user;
    }

    public Account getWaiter() {
        return waiter;
    }

    public void setWaiter(Account waiter) {
        this.waiter = waiter;
    }

    public DateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(DateTime orderTime) {
        this.orderTime = orderTime;
    }

    public Date getOrderTimeInDateFormat() {
        return orderTime != null ? getOrderTime().toDate() : null;
    }

    public void setOrderTimeInDateFormat(Date date) {
        if (date != null) {
            setOrderTime(new DateTime(date));
        }
    }

    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }

    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTable = restaurantTable;
    }

    public OrderStatusType getStatus() {
        return status;
    }

    public void setStatus(OrderStatusType status) {
        this.status = status;
    }

    public List<OrderDetail> getActiveOrderInfos() {
        if (this.orderDetails == null) {
            this.orderDetails = new ArrayList<OrderDetail>();
        }
        return orderDetails;
    }

    public void setActiveOrderInfos(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String customerName) {
        this.guestName = customerName;
    }

    public List<OrderDetail> getOrderDetails() {
        if (this.orderDetails == null) {
            this.orderDetails = new ArrayList<OrderDetail>();
        }
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }
}
