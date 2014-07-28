package ua.yuris.restaurant.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ua.yuris.restaurant.model.Account;
import ua.yuris.restaurant.repository.AccountRepository;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 29.05.14
 * Time: 19:46
 * To change this template use File | Settings | File Templates.
 */
@Named("accountService")
@Transactional(readOnly = true)
public class AccountServiceImpl
        implements UserDetailsService, AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Inject
    private AccountRepository accountRepository;

    public AccountServiceImpl() {
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Assert.notNull(username, "username can't be null");

        Account account;

/*
        try {
            account = accountRepository.findByUsername(username);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("Username don't exists!");
        }
*/
        account = accountRepository.findByUsername(username);

        if (account == null) {
            throw new UsernameNotFoundException("Username don't exists!");
        }

        return account;
    }

    @Override
    public boolean checkAvailableUsername(String username) {

        Assert.notNull(username, "username can't be null");

        Integer nAccounts = 0;
        nAccounts = accountRepository.countByUsername(username);

        return nAccounts < 1;
    }

    @Override
    public boolean checkAvailableUsername(String username, Account currentAccount) {

        Assert.notNull(username, "username can't be null");

        Account account = accountRepository.findByUsername(username);
        if (account != null && !account.equals(currentAccount)) {
            return true;
        }
        return false;
    }

    @Override
    public Account findAccount(Long id) {
        return accountRepository.findOne(id);
    }

    @Override
    public List<Account> findAllAccounts() {
        return accountRepository.findByOrderByActiveDesc();
    }

    @Override
    public List<Account> findAllActiveAccounts() {
        return accountRepository.findByActiveTrue();
    }

    @Override
    public List<Account> findAllAccountByRoleDescription(String role) {
        return accountRepository.findByRoleDescription(role);
    }

    @Override
    @Transactional(readOnly = false)
    public Account saveAccount(Account account) {
        Assert.notNull(account, "The entity must not be null!");

        account = accountRepository.save(account);
        return account;
    }
}
