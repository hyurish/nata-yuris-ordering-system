package ua.yuris.restaurant.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;

import ua.yuris.restaurant.model.results.PopularDishesReport;
import ua.yuris.restaurant.model.results.RevenueReport;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 12.05.14
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "OrderDetails")
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "RevenueResultSetMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = RevenueReport.class,
                                columns = {
                                        @ColumnResult(name = "orderDate", type = Date.class),
                                        @ColumnResult(name = "orderQuantity", type = Long.class),
                                        @ColumnResult(name = "revenue", type = BigDecimal.class)
                                }
                        )
                }
        ),
        @SqlResultSetMapping(
                name = "PopularDishesResultSetMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = PopularDishesReport.class,
                                columns = {
                                        @ColumnResult(name = "menuItemTitle"),
                                        @ColumnResult(name = "categoryId", type = Long.class),
                                        @ColumnResult(name = "categoryTitle"),
                                        @ColumnResult(name = "menuItemQuantity",
                                                type = BigDecimal.class),
                                        @ColumnResult(name = "revenue", type = BigDecimal.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "OrderDetail.getRevenueWithNamedNativeQuery",
                query =
                        "SELECT date(o.OrderTime) as orderDate, " +
                                "count(distinct o.OrderId) as orderQuantity, " +
                                "sum(od.ItemPrice * od.ItemQuantity) as revenue " +
                                "FROM Orders o " +
                                "LEFT OUTER JOIN OrderDetails od ON (od.OrderId = o.OrderId) " +
                                "WHERE (od.Active = true) AND (o.Active = true) " +
                                "AND (o.Status IN ('PAID', 'SUBMITTED')) " +
                                "AND (o.OrderTime > ?1) AND (o.OrderTime < ?2) " +
                                "GROUP BY date(o.OrderTime)",
                resultSetMapping = "RevenueResultSetMapping"
        ),
        @NamedNativeQuery(name = "OrderDetail.getPopularDishesWithNamedNativeQuery",
                query =
                        "SELECT m.Title as menuItemTitle, " +
                                "c.MenuCategoryId as categoryId, " +
                                "c.Title as categoryTitle, " +
                                "sum(od.ItemQuantity) as menuItemQuantity, " +
                                "sum(od.ItemPrice * od.ItemQuantity) as revenue " +
                                "FROM Orders o " +
                                "LEFT OUTER JOIN OrderDetails od ON (od.OrderId = o.OrderId) " +
                                "LEFT OUTER JOIN MenuItems m ON (m.MenuItemId = od.MenuItemId) " +
                                "LEFT OUTER JOIN MenuCategories c " +
                                "ON (c.MenuCategoryId = m.MenuCategoryId) " +
                                "WHERE (od.Active = true) AND (o.Active = true) " +
                                "AND (o.Status IN ('PAID', 'SUBMITTED')) " +
                                "AND (o.OrderTime > ?1) AND (o.OrderTime < ?2) " +
                                "GROUP BY c.MenuCategoryId, m.MenuItemId " +
                                "ORDER BY c.Priority, MenuItemQuantity desc",
                resultSetMapping = "PopularDishesResultSetMapping"
        )
})
public class OrderDetail
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(OrderDetail.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="OrderDetailId")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "MenuItemId")
    private MenuItem menuItem;
    @ManyToOne
    @JoinColumn(name = "OrderId")
    private Order order;
    private Integer itemQuantity;
    private Integer itemPrice;
    private Boolean cooked = Boolean.FALSE;
    @Column(columnDefinition = "TIMESTAMP")
    @Converter(name = "dateTimeConverter",
            converterClass = ua.yuris.restaurant.util.JodaDateTimeConverter.class)
    @Convert("dateTimeConverter")
    private DateTime createTime;
    private Boolean active = Boolean.TRUE;


    public OrderDetail() {
        createTime = new DateTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDetail)) return false;

        if (getMenuItem() == null && getCreateTime() == null) return false;

        OrderDetail orderDetail = (OrderDetail) o;
        if (orderDetail.getMenuItem() == null) return false;

        if(!(getMenuItem().equals(orderDetail.getMenuItem())))
            return false;
        if(!(getCreateTime().equals(orderDetail.getCreateTime())))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = menuItem != null ? menuItem.hashCode() : 0;
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }

    public boolean isNewEntity() {
        return (this.id == null);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", this.getId())
                .append("new", this.isNewEntity())
                .append("itemQuantity", this.getItemQuantity())
                .append("itemPrice", this.getItemPrice())
                .append("cooked", this.getCooked())
                .append("getActive", this.getActive())
                .append("order", this.getOrder())
                .append("createTime", this.getCreateTime())
                .toString();
    }

    public Double getPrettyPrice() {
        return itemPrice / 100.0;
    }

    public void setPrettyPrice(Double prettyPrice) {
        Double d = prettyPrice * 100;
        this.itemPrice = ((Double) (prettyPrice * 100)).intValue();
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

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public Integer getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Integer itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Boolean getCooked() {
        return cooked;
    }

    public void setCooked(Boolean cooked) {
        this.cooked = cooked;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }
}
