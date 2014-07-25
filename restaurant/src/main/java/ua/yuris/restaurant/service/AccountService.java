package ua.yuris.restaurant.service;

import java.util.List;

import ua.yuris.restaurant.model.Account;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 29.05.14
 * Time: 20:01
 * To change this template use File | Settings | File Templates.
 */
public interface AccountService {

    boolean checkAvailableUsername(String username);

    boolean checkAvailableUsername(String username, Account currentAccount);

    Account findAccount(Long id);

    List<Account> findAllAccounts();

    List<Account> findAllActiveAccounts();

    List<Account> findAllAccountByRoleDescription(String role);

    Account saveAccount(Account account);
}
