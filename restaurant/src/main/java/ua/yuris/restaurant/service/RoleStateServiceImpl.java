package ua.yuris.restaurant.service;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import ua.yuris.restaurant.model.Role;
import ua.yuris.restaurant.repository.RoleRepository;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 05.06.14
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
@Named("roleStateService")
@Transactional(readOnly = true)
@Scope("session")
public class RoleStateServiceImpl
        implements RoleStateService {
    private static final Logger LOG = LoggerFactory.getLogger(RoleStateServiceImpl.class);

    @Inject
    private RoleRepository roleRepository;

    private Set<Role> roles;

    public RoleStateServiceImpl() {
    }

    @PostConstruct
    public void initialize() {
        roles = roleRepository.findByActiveTrue();
    }

    @Override
    public Set<Role> findAllActiveRoles() {
        return roles;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
