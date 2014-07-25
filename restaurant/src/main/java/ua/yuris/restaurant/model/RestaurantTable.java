package ua.yuris.restaurant.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 12.05.14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "RestaurantTables")
public class RestaurantTable
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(RestaurantTable.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="RestaurantTableId")
    private Long id;
    @Column(nullable = false)
    private String tableNumber;
    private Integer tableSize;
    @Column(columnDefinition = "TIMESTAMP")
    @Converter(name = "dateTimeConverter",
            converterClass = ua.yuris.restaurant.util.JodaDateTimeConverter.class)
    @Convert("dateTimeConverter")
    private DateTime createTime;
    private Boolean active = Boolean.TRUE;

    public RestaurantTable() {
        createTime = new DateTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantTable)) return false;

        if (getTableNumber() == null && getCreateTime() == null) return false;

        RestaurantTable restaurantTable = (RestaurantTable) o;
        if (restaurantTable.getTableNumber() == null) return false;
        if(!(getTableNumber().equals(restaurantTable.getTableNumber())))
            return false;
        if(!(getCreateTime().equals(restaurantTable.getCreateTime())))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tableNumber != null ? tableNumber.hashCode() : 0;
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
                .append("tableNumber", this.getTableNumber())
                .append("tableSize", this.getTableSize())
                .append("createTime", this.getCreateTime())
                .append("isActive", this.isActive())
                .toString();
    }

    public Boolean isActive() {
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

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getTableSize() {
        return tableSize;
    }

    public void setTableSize(Integer tableSize) {
        this.tableSize = tableSize;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }
}
