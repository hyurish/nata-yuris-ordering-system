package ua.yuris.restaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ua.yuris.restaurant.model.Account;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 23.05.14
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    Integer countByUsername(String username);

    Account findByUsername(String username);

    List<Account> findByActiveTrue();

    List<Account> findByOrderByActiveDesc();

    @Query("select account from Account account left join account.roles rol " +
            "where rol.name = :roleName and account.active = 1")
    List<Account> findByRoleDescription(@Param("roleName") String roleName);
}
