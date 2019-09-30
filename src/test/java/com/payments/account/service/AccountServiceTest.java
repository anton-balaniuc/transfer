package com.payments.account.service;

import com.payments.account.exception.AccountAlreadyExistsException;
import com.payments.account.exception.AccountNotFoundException;
import com.payments.account.exception.InsufficientFundsException;
import com.payments.account.model.Account;
import com.payments.account.model.Transaction;
import com.payments.account.model.TransactionType;
import com.payments.account.repo.AccountDao;
import com.payments.account.repo.TransactionDao;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountDao accountDao;

    @Mock
    private TransactionDao transactionDao;

    @InjectMocks
    AccountService sut;

    EasyRandom easyRandom = new EasyRandom();
    int accountId = 10;
    private Account randomAccount;
    private Account to;
    private BigDecimal deposit = new BigDecimal(237);
    private BigDecimal withdraw = new BigDecimal(89);
    private BigDecimal transfer = new BigDecimal(12);

    @BeforeEach
    public void setup() {
         randomAccount =  easyRandom.nextObject(Account.class);
         randomAccount.setId(10);
         to =  easyRandom.nextObject(Account.class);
         to.setId(randomAccount.getId() + 200);
    }

    @Test
    void find_noAccount_AccountNotFoundException() {

        when(accountDao.find(anyInt())).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> {
            sut.find(accountId);
        });
    }

    @Test
    void find_accountExists_Account() {


        when(accountDao.find(anyInt())).thenReturn(randomAccount);

        assertEquals(randomAccount, sut.find(accountId));
    }

    @Test
    void create_emailExists_AccountAlreadyExistsException() {
        when(accountDao.findByEmail(anyString())).thenReturn(Collections.singletonList(randomAccount));

        assertThrows(AccountAlreadyExistsException.class, () -> sut.create(easyRandom.nextObject(Account.class)));
    }

    @Test
    void create_newAccount_AccountAlreadyExistsException() {

        Account accountToCreate = easyRandom.nextObject(Account.class);
        when(accountDao.findByEmail(anyString())).thenReturn(Collections.emptyList());

        sut.create(accountToCreate);

    }

    @Test
    void deposit_accountNotFound_AccountNotFoundException() {
        when(accountDao.find(anyInt())).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () -> {
            sut.deposit(accountId, deposit);
        });

    }

    @Test
    void deposit_ok_transaction() {
        when(accountDao.find(anyInt())).thenReturn(randomAccount);

        sut.deposit(accountId, deposit);

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionDao).create(transactionArgumentCaptor.capture());

        Transaction transaction = transactionArgumentCaptor.getValue();

        assertEquals(randomAccount.getId(), transaction.getTo().getId());
        assertEquals(randomAccount.getEmail(), transaction.getTo().getEmail());
        assertEquals(randomAccount.getBalance(), transaction.getTo().getBalance());

        assertEquals(deposit, transaction.getAmount());
        assertEquals(TransactionType.DEPOSIT, transaction.getTransactionType());

        assertNull(transaction.getFrom());
    }

    @Test
    void deposit_ok_account() {
        BigDecimal expectedBalance = randomAccount.getBalance().add(deposit);
        when(accountDao.find(anyInt())).thenReturn(randomAccount);

        sut.deposit(accountId, deposit);

        assertEquals(expectedBalance, randomAccount.getBalance());


    }

    @Test
    void withdrawal_accountNotFound_AccountNotFoundException() {
        when(accountDao.find(anyInt())).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () -> {
            sut.withdrawal(accountId, deposit);
        });
    }

    @Test
    void withdrawal_accountNotFound_InsufficientFundsException() {
        when(accountDao.find(anyInt())).thenReturn(randomAccount);
        assertThrows(InsufficientFundsException.class, () -> {
            sut.withdrawal(accountId, randomAccount.getBalance().add(new BigDecimal(100)));
        });
    }

    @Test
    void withdrawal_ok_transaction() {
        randomAccount.setBalance(withdraw.multiply(BigDecimal.TEN));

        when(accountDao.find(anyInt())).thenReturn(randomAccount);
        sut.withdrawal(accountId, withdraw);

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionDao).create(transactionArgumentCaptor.capture());

        Transaction transaction = transactionArgumentCaptor.getValue();

        assertEquals(randomAccount.getId(), transaction.getFrom().getId());
        assertEquals(randomAccount.getEmail(), transaction.getFrom().getEmail());
        assertEquals(randomAccount.getBalance(), transaction.getFrom().getBalance());

        assertEquals(withdraw, transaction.getAmount());
        assertEquals(TransactionType.WITHDRAW, transaction.getTransactionType());

        assertNull(transaction.getTo());
    }

    @Test
    void withdrawal_ok_account() {
        randomAccount.setBalance(withdraw.multiply(BigDecimal.TEN));
        BigDecimal expectedBalance = randomAccount.getBalance().subtract(withdraw);

        when(accountDao.find(anyInt())).thenReturn(randomAccount);
        sut.withdrawal(accountId, withdraw);

        assertEquals(expectedBalance, randomAccount.getBalance());

    }

    @Test
    void transfer_fromNotfound_AccountNotFoundException() {
        when(accountDao.find(anyInt())).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () -> {
            sut.transfer(accountId, accountId, transfer);
        });

    }

    @Test
    void transfer_toNotfound_AccountNotFoundException() {
        randomAccount.setBalance(transfer.multiply(BigDecimal.TEN));
        when(accountDao.find(eq(randomAccount.getId()))).thenReturn(randomAccount);
        when(accountDao.find(eq(to.getId()))).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () -> {
            sut.transfer(randomAccount.getId(), to.getId(), transfer);
        });
    }

    @Test
    void transfer_insufficientFunds_InsufficientFundsException() {
        randomAccount.setBalance(BigDecimal.ZERO);
        when(accountDao.find(eq(randomAccount.getId()))).thenReturn(randomAccount);
        assertThrows(InsufficientFundsException.class, () -> {
            sut.transfer(randomAccount.getId(), to.getId(), transfer);
        });

    }

    @Test
    void transfer_ok_from() {
        BigDecimal expectedBalance = randomAccount.getBalance().add(BigDecimal.TEN);
        randomAccount.setBalance( randomAccount.getBalance().add(BigDecimal.TEN).add(transfer));
        when(accountDao.find(eq(randomAccount.getId()))).thenReturn(randomAccount);
        when(accountDao.find(eq(to.getId()))).thenReturn(to);
        sut.transfer(randomAccount.getId(), to.getId(), transfer);

        assertEquals(expectedBalance, randomAccount.getBalance());

    }

    @Test
    void transfer_ok_to() {
        BigDecimal expectedBalance = to.getBalance().add(transfer);
        randomAccount.setBalance( randomAccount.getBalance().add(BigDecimal.TEN).add(transfer));
        when(accountDao.find(eq(randomAccount.getId()))).thenReturn(randomAccount);
        when(accountDao.find(eq(to.getId()))).thenReturn(to);
        sut.transfer(randomAccount.getId(), to.getId(), transfer);

        assertEquals(expectedBalance, to.getBalance());

    }

    @Test
    void transfer_ok_transaction() {
        BigDecimal expectedBalance = to.getBalance().add(transfer);
        randomAccount.setBalance( randomAccount.getBalance().add(BigDecimal.TEN).add(transfer));
        when(accountDao.find(eq(randomAccount.getId()))).thenReturn(randomAccount);
        when(accountDao.find(eq(to.getId()))).thenReturn(to);
        sut.transfer(randomAccount.getId(), to.getId(), transfer);

        assertEquals(expectedBalance, to.getBalance());

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionDao).create(transactionArgumentCaptor.capture());

        Transaction transaction = transactionArgumentCaptor.getValue();

        assertEquals(randomAccount.getId(), transaction.getFrom().getId());
        assertEquals(randomAccount.getEmail(), transaction.getFrom().getEmail());
        assertEquals(randomAccount.getBalance(), transaction.getFrom().getBalance());

        assertEquals(to.getId(), transaction.getTo().getId());
        assertEquals(to.getEmail(), transaction.getTo().getEmail());
        assertEquals(to.getBalance(), transaction.getTo().getBalance());

        assertEquals(transfer, transaction.getAmount());
        assertEquals(TransactionType.TRANSFER, transaction.getTransactionType());


    }

}