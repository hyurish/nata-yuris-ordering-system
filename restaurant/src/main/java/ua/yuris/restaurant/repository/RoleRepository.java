package ua.yuris.restaurant.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.yuris.restaurant.model.Role;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 05.06.14
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    Set<Role> findByActiveTrue();
}
