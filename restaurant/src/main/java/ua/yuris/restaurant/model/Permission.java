package ua.yuris.restaurant.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 23.05.14
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "Permissions")
public class Permission
        implements Serializable, GrantedAuthority {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(Permission.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PermissionId")
    private Long id;
    @Column(nullable = false)
    private String name;
    private Boolean active = Boolean.TRUE;

    @Override
    public String getAuthority() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrantedAuthority)) return false;

        GrantedAuthority ga = (GrantedAuthority) o;
        return (getAuthority().equals(ga.getAuthority()));
    }

    @Override
    public int hashCode() {
        return getAuthority().hashCode();
    }

    public boolean isNewEntity() {
        return (this.id == null);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", this.getId())
                .append("new", this.isNewEntity())
                .append("permission", this.getName())
                .append("getActive", this.getActive())
                .toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String permission) {
        this.name = permission;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
