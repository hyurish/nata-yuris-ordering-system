package ua.yuris.restaurant.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 23.05.14
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "Roles")
public class Role
        implements Serializable, GrantedAuthority {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(Role.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="RoleId")
    private Long id;
    @Column(nullable = false)
    private String name;
    private Boolean active = Boolean.TRUE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Role_Permissions",
            joinColumns = {@JoinColumn(name = "RoleId")},
            inverseJoinColumns = {@JoinColumn(name = "PermissionId")})
    private Set<Permission> permissions = new HashSet<Permission>();

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
        ToStringCreator sc = new ToStringCreator(this);
        sc
                .append("id", this.getId())
                .append("new", this.isNewEntity())
                .append("description", this.getName())
                .append("getActive", this.getActive());
        if (permissions != null && permissions.size() > 0) {
            for (Permission permission : permissions) {
                sc.append(permission);
            }
        }
        return sc.toString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
