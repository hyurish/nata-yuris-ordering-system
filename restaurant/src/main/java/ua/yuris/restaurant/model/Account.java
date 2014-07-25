package ua.yuris.restaurant.model;

import java.io.Serializable;
import java.util.Collection;
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
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 23.05.14
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "Accounts")
public class Account
        implements Serializable, UserDetails {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(Account.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="AccountId")
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Account_Roles",
            joinColumns = {@JoinColumn(name = "AccountId")},
            inverseJoinColumns = {@JoinColumn(name = "RoleId")})
    private Set<Role> roles = new HashSet<Role>();
    private Boolean active = Boolean.TRUE;

    public Account() {
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.addAll(getRoles());
        authorities.addAll(getPermissions());
        return authorities;
    }

    public Set<Permission> getPermissions() {
        Set<Permission> permissions = new HashSet<Permission>();
        for (Role role : roles) {
            permissions.addAll((Collection<? extends Permission>) role.getPermissions());
        }
        return permissions;
    }

    public String getRoleNames() {
        String roleNames = "";
        for (Role role : roles) {
            roleNames += role.getName() + " ";
        }
        return roleNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        Account account = (Account) o;
        return (getUsername().equals(account.getUsername()));
    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
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
                .append("firstName", this.getFirstName())
                .append("lastName", this.getLastName())
                .append("username", this.getUsername())
                .append("password", this.getPassword())
                .append("isActive", this.getActive());
        Collection<? extends GrantedAuthority> authorities = getAuthorities();
        for (GrantedAuthority authority : authorities) {
            sc.append(authority);
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
