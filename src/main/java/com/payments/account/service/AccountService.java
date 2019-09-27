package com.payments.account.service;

import com.payments.account.exception.AccountAlreadyExistsException;
import com.payments.account.exception.AccountNotFoundException;
import com.payments.account.exception.InsufficientFundsException;
import com.payments.account.model.Account;
import com.payments.account.model.Transaction;
import com.payments.account.model.TransactionType;
import com.payments.account.repo.AccountDao;
import com.payments.account.repo.TransactionDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AccountService {

    private static Map<Integer, Object> map = new ConcurrentHashMap<>();

    private AccountDao accountDao;

    @Inject
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Inject
    public void setTransactionDao(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    private TransactionDao transactionDao;

    public Account find(int accountId) {
        return Optional.ofNullable(accountDao.find(accountId))
                .orElseThrow(() -> new AccountNotFoundException());
    }

    public void create(Account account) {
        if (!accountDao.findByEmail(account.getEmail()).isEmpty()) {
            throw new AccountAlreadyExistsException();
        }
        accountDao.create(account);
    }

    public void update(Account account) {
        accountDao.update(account);
    }

    public List<Account> getAll() {
        return accountDao.getAll();
    }

    public void deposit(Integer accountId, BigDecimal amount) {
        synchronized (map.computeIfAbsent(accountId, key -> new Object())) {
            Account account = find(accountId);

            account.setBalance(account.getBalance().add(amount));
            Transaction transaction = new Transaction.Builder()
                    .amount(amount)
                    .to(account)
                    .transactionType(TransactionType.DEPOSIT)
                    .build();

            accountDao.update(account);
            transactionDao.create(transaction);

        }
    }

    public void withdrawal(int accountId, BigDecimal amount) {
        synchronized (map.computeIfAbsent(accountId, key -> new Object())) {
            Account account = find(accountId);

            if (account.getBalance().compareTo(amount) < 0 ) {
                throw new InsufficientFundsException();
            }
            account.setBalance(account.getBalance().subtract(amount));

            Transaction transaction = new Transaction.Builder()
                    .amount(amount)
                    .from(account)
                    .transactionType(TransactionType.WITHDRAW)
                    .build();

            accountDao.update(account);
            transactionDao.create(transaction);

        }
    }

    public void transfer(int from, int to, BigDecimal amount) {
        synchronized (map.computeIfAbsent(from, key -> new Object())) {
            Account source = find(from);

            if (source.getBalance().compareTo(amount) < 0 ) {
                throw new InsufficientFundsException();
            }

            synchronized (map.computeIfAbsent(to, key -> new Object())) {

                source.setBalance(source.getBalance().subtract(amount));

                Account destination = find(to);
                destination.setBalance(destination.getBalance().add(amount));

                Transaction transaction = new Transaction.Builder()
                        .from(source)
                        .to(destination)
                        .amount(amount)
                        .transactionType(TransactionType.TRANSFER)
                        .build();
                accountDao.update(source);
                accountDao.update(destination);
                transactionDao.create(transaction);
            }
        }
    }
}
