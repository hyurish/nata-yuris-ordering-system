package ua.yuris.restaurant.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;

import ua.yuris.restaurant.model.enums.CookingPlaceType;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 07.05.14
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "MenuItems")
public class MenuItem
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MenuItem.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MenuItemId")
    private Long id;
    @Column(nullable = false)
    private String title;
    @Lob
    private String description;
    private String measure;
    @Column(nullable = false)
    private Integer price;
    @ManyToOne
    @JoinColumn(name = "MenuCategoryId")
    private MenuCategory menuCategory;
    @Enumerated(EnumType.STRING)
    private CookingPlaceType cookingPlace;
    private String picture;
    private Integer priority;
    @Column(columnDefinition = "TIMESTAMP")
    @Converter(name = "dateTimeConverter",
            converterClass = ua.yuris.restaurant.util.JodaDateTimeConverter.class)
    @Convert("dateTimeConverter")
    private DateTime createTime;
    private Boolean active = Boolean.TRUE;

    public MenuItem() {
        createTime = new DateTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;

        if (getTitle() == null && getCreateTime() == null) return false;

        MenuItem menuItem = (MenuItem) o;
        if (menuItem.getTitle() == null) return false;

        if(!(getTitle().equals(menuItem.getTitle())))
            return false;
        if(!(getCreateTime().equals(menuItem.getCreateTime())))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
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
                .append("title", this.getTitle())
                .append("description", this.getDescription())
                .append("weight", this.getMeasure())
                .append("price", this.getPrice())
                .append("menuCategory", this.getMenuCategory())
                .append("cookingPlace", this.getCookingPlace())
                .append("picture", this.getPicture())
                .append("createTime", this.getCreateTime())
                .append("getActive", this.getActive())
                .toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Double getPrettyPrice() {
        return price / 100.0;
    }

    public void setPrettyPrice(Double prettyPrice) {
        this.price = ((Double) (prettyPrice * 100)).intValue();
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public CookingPlaceType getCookingPlace() {
        return cookingPlace;
    }

    public void setCookingPlace(CookingPlaceType cookingPlace) {
        this.cookingPlace = cookingPlace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String weight) {
        this.measure = weight;
    }

    public MenuCategory getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        this.menuCategory = menuCategory;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }
}
