package ua.yuris.restaurant.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 08.05.14
 * Time: 22:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "MenuCategories")
public class MenuCategory
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MenuCategory.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MenuCategoryId")
    private Long id;
    @Column(nullable = false)
    private String title;
    private Integer priority;
    @Column(columnDefinition = "TIMESTAMP")
    @Converter(name = "dateTimeConverter",
            converterClass = ua.yuris.restaurant.util.JodaDateTimeConverter.class)
    @Convert("dateTimeConverter")
    private DateTime createTime;
    private Boolean active = Boolean.TRUE;

    public MenuCategory() {
        createTime = new DateTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuCategory)) return false;

        if (getTitle() == null && getCreateTime() == null) return false;

        MenuCategory menuCategory = (MenuCategory) o;
        if(!(getTitle().equals(menuCategory.getTitle())))
            return false;
        if(!(getCreateTime().equals(menuCategory.getCreateTime())))
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
                .append("priority", this.getPriority())
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }
}
