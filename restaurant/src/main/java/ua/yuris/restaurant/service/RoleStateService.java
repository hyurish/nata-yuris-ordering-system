package ua.yuris.restaurant.service;

import java.util.Set;

import ua.yuris.restaurant.model.Role;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 05.06.14
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public interface RoleStateService {

    Set<Role> findAllActiveRoles();
}
