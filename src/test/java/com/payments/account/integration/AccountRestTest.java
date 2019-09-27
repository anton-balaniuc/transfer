package com.payments.account.integration;

import com.payments.account.model.Account;
import com.payments.account.model.Transaction;
import com.payments.account.model.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountRestTest {

    protected static Client client;
    protected Response response;
    protected static String baseUrl = "http://localhost:8181/";
    protected static String ACCOUNTS = "accounts";
    protected static String TRANSACTIONS = "transactions";
    private static final Jsonb jsonb = JsonbBuilder.create();


    /**
     * Makes a GET request to the /accounts/{id} endpoint and returns a Response
     */
    protected Response getAccount(int accountId) {
        WebTarget webTarget = client.target(baseUrl + ACCOUNTS + "/" + accountId);
        return webTarget.request().get();
    }

    /**
     * Makes a GET request to the /accounts endpoint and returns a Response
     */
    protected Response getAll() {
        WebTarget webTarget = client.target(baseUrl + ACCOUNTS);
        return webTarget.request().get();
    }

    /**
     * Makes a POST request to the /accounts endpoint and returns a Response
     */
    protected Response createAccount(String email, BigDecimal amount) {
        Account account = new Account(email, amount);
        WebTarget webTarget = client.target(baseUrl + ACCOUNTS);
        return webTarget.request().post(Entity.json(jsonb.toJson(account)));
    }

    /**
     * Makes a PUT request to the /accounts/{id}/deposit/{amount} endpoint and returns a Response
     */
    protected Response deposit(Integer id, BigDecimal amount) {
        WebTarget webTarget = client.target(baseUrl + ACCOUNTS + "/" + id + "/deposit/" + amount);
        return webTarget.request().put(Entity.text(""));
    }

    /**
     * Makes a PUT request to the /accounts/{id}/withdrawal/{amount} endpoint and returns a Response
     */
    protected Response withdrawal(Integer id, BigDecimal amount) {
        WebTarget webTarget = client.target(baseUrl + ACCOUNTS + "/" + id + "/withdrawal/" + amount);
        return webTarget.request().put(Entity.text(""));
    }

    /**
     * Makes a PUT request to the /accounts/{id}/withdrawal/{amount} endpoint and returns a Response
     */
    protected Response transfer(Integer from, Integer to, BigDecimal amount) {
        WebTarget webTarget = client.target(baseUrl + ACCOUNTS + "/" + from + "/transfer/" + to + "/" + amount);
        return webTarget.request().put(Entity.text(""));
    }

    /**
     * Makes a GET request to the /transactions/{accountId} endpoint and returns a Response
     */
    protected Response getTransactions(Integer accountId) {
        WebTarget webTarget = client.target(baseUrl + TRANSACTIONS + "/" + accountId);
        return webTarget.request().get();
    }


    @BeforeAll
    public static void setUp() {
        client = ClientBuilder.newClient();
    }

    @Test
    public void getAccount_notExists_404() {
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getAccount(100100).getStatus(), "Retrieving an " +
                "account that does not exist should return 404");
    }

    @Test
    public void getAccount_NegativeId_400() {
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), getAccount(-1).getStatus(), "Retrieving an account " +
                "with negative id should return 400");
    }

    @Test
    public void createAccount_notValidEmail_400() {
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                createAccount("not_an_email", new BigDecimal(100)).getStatus(), "Creating an account with not valid " +
                        "email should return 400");
    }

    @Test
    public void createAccount_negativeAmount_400() {
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), createAccount("email@example.com",
                new BigDecimal(-100)).getStatus(), "Creating an account with negative amount id should return 400");
    }

    @Test
    public void createAccount_emialAreadyExist_400() {
        String email = "email_3@example.com";
        BigDecimal amount = new BigDecimal(100);
        createAccount(email, amount);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), createAccount(email, amount).getStatus(), "Creating" +
                " an account with existing email should return 400");
    }

    @Test
    public void getAccount_existing_201() {
        assertEquals(Response.Status.CREATED.getStatusCode(),
                createAccount("email@example.com", new BigDecimal(100)).getStatus(), "Creating a valid account should" +
                        " return 201");
    }

    @Test
    public void createAccount_returns_id() {
        String email = "email_2@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer id = createAccount(email, amount).readEntity(Integer.class);
        Account account = jsonb.fromJson(getAccount(id).readEntity(String.class), Account.class);

        assertEquals(id, account.getId(), "account must have the same id");
        assertEquals(amount, account.getBalance(), "account must have the same balance");
        assertEquals(email, account.getEmail(), "account must have the same email");
    }

    @Test
    public void getAll_accounts_200() {
        String email = "email_4@example.com";
        BigDecimal amount = new BigDecimal(100);
        createAccount(email, amount);
        List<Account> accounts = jsonb.fromJson(getAll().readEntity(String.class), new ArrayList<Account>() {
        }.getClass().getGenericSuperclass());

        assertTrue(!accounts.isEmpty(), "accounts list should not be empty");
    }

    @Test
    public void deposit_negativeAmount_400() {
        String email = "email_deposit_negative@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer id = createAccount(email, amount).readEntity(Integer.class);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                deposit(id, new BigDecimal(-12)).getStatus(), "Deposit with negative amount should return 400");

    }

    @Test
    public void deposit_accountNotFound_404() {

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                deposit(1235124123, new BigDecimal(120)).getStatus(), "Deposit with negative amount should return 400");

    }

    @Test
    public void deposit_checkBalance() {
        String email = "email_deposit_checkBalance@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer id = createAccount(email, amount).readEntity(Integer.class);

        BigDecimal deposit = new BigDecimal(12);
        deposit(id, deposit);
        Account account = jsonb.fromJson(getAccount(id).readEntity(String.class), Account.class);

        assertEquals(amount.add(deposit),
                account.getBalance(), "Balance must be equal");

    }

    @Test
    public void deposit_checkTransaction() {
        String email = "email_deposit_checkTransaction@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer id = createAccount(email, amount).readEntity(Integer.class);

        BigDecimal deposit = new BigDecimal(12);
        deposit(id, deposit);

        List<Transaction> transactions = jsonb.fromJson(getTransactions(id).readEntity(String.class), new ArrayList<Transaction>() {
        }.getClass().getGenericSuperclass());

        Transaction transaction = transactions.get(0);

        assertEquals(id, transaction.getTo().getId());
        assertEquals(email, transaction.getTo().getEmail());
        assertEquals(amount.add(deposit), transaction.getTo().getBalance());

        assertEquals(deposit, transaction.getAmount());
        assertEquals(TransactionType.DEPOSIT, transaction.getTransactionType());

    }

    @Test
    public void withdrawal_negativeAmount_400() {
        String email = "withdrawal_negative@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer id = createAccount(email, amount).readEntity(Integer.class);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                withdrawal(id, new BigDecimal(-12)).getStatus(), "Withdrawal with negative amount should return 400");

    }

    @Test
    public void withdrawal_insufficientFunds_400() {
        String email = "withdrawal_insufficientFunds@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer id = createAccount(email, amount).readEntity(Integer.class);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                withdrawal(id, new BigDecimal(1000)).getStatus(), "Withdrawal with insufficient funds should return 400");

    }

    @Test
    public void withdrawal_accountNotFound_404() {

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                withdrawal(1235124123, new BigDecimal(120)).getStatus(), "Withdrawal with negative amount should return 400");

    }

    @Test
    public void withdrawal_checkBalance() {
        String email = "withdrawal_checkBalance@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer id = createAccount(email, amount).readEntity(Integer.class);

        BigDecimal withdrawal = new BigDecimal(40);
        withdrawal(id, withdrawal);
        Account account = jsonb.fromJson(getAccount(id).readEntity(String.class), Account.class);

        assertEquals(amount.subtract(withdrawal),
                account.getBalance(), "Balance must be equal");

    }

    @Test
    public void withdrawal_checkTransaction() {
        String email = "email_withdrawal_checkTransaction@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer id = createAccount(email, amount).readEntity(Integer.class);

        BigDecimal withdrawal = new BigDecimal(80);
        withdrawal(id, withdrawal);

        List<Transaction> transactions = jsonb.fromJson(getTransactions(id).readEntity(String.class), new ArrayList<Transaction>() {
        }.getClass().getGenericSuperclass());

        Transaction transaction = transactions.get(0);

        assertEquals(id, transaction.getFrom().getId());
        assertEquals(email, transaction.getFrom().getEmail());
        assertEquals(amount.subtract(withdrawal), transaction.getFrom().getBalance());

        assertEquals(withdrawal, transaction.getAmount());
        assertEquals(TransactionType.WITHDRAW, transaction.getTransactionType());

    }

    @Test
    public void transfer_fromNotFound_404() {

        String email = "transfer_fromNotFound@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer to = createAccount(email, amount).readEntity(Integer.class);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                transfer(21312313, to, new BigDecimal(100)).getStatus());

    }

    @Test
    public void transfer_toNotFound_404() {

        String email = "transfer_toNotFound@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer from = createAccount(email, amount).readEntity(Integer.class);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                transfer(from, from + 1000, new BigDecimal(100)).getStatus());

    }

    @Test
    public void transfer_negativeAmount_400() {

        String email = "transfer_from_negative@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer from = createAccount(email, amount).readEntity(Integer.class);

        email = "transfer_to_negative@example.com";
        amount = new BigDecimal(100);
        Integer to = createAccount(email, amount).readEntity(Integer.class);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                transfer(from, to, new BigDecimal(-100)).getStatus());

    }

    @Test
    public void transfer_insufficient_400() {

        String email = "transfer_from_insufficien@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer from = createAccount(email, amount).readEntity(Integer.class);

        email = "transfer_to_insufficien@example.com";
        amount = new BigDecimal(100);
        Integer to = createAccount(email, amount).readEntity(Integer.class);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                transfer(from, to, new BigDecimal(300)).getStatus());

    }


    @Test
    public void transfer_checkTransaction() {

        String email = "transfer_from@example.com";
        BigDecimal amount = new BigDecimal(100);
        Integer from = createAccount(email, amount).readEntity(Integer.class);

        String emailTo = "transfer_to@example.com";
        BigDecimal amountTo = new BigDecimal(100);
        Integer to = createAccount(emailTo, amountTo).readEntity(Integer.class);


        BigDecimal transferAmount = new BigDecimal(20);
        transfer(from, to, transferAmount);
        List<Transaction> transactions = jsonb.fromJson(getTransactions(from).readEntity(String.class), new ArrayList<Transaction>() {
        }.getClass().getGenericSuperclass());

        Transaction transaction = transactions.get(0);

        assertEquals(from, transaction.getFrom().getId());
        assertEquals(email, transaction.getFrom().getEmail());
        assertEquals(amount.subtract(transferAmount), transaction.getFrom().getBalance());

        assertEquals(transferAmount, transaction.getAmount());
        assertEquals(TransactionType.TRANSFER, transaction.getTransactionType());

        assertEquals(to, transaction.getTo().getId());
        assertEquals(emailTo, transaction.getTo().getEmail());
        assertEquals(amount.add(transferAmount), transaction.getTo().getBalance());



    }


}
