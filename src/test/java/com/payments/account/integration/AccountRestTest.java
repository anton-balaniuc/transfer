package com.payments.account.integration;

import com.payments.account.model.Account;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountRestTest {

    protected static Client client;
    protected Response response;
    protected static String baseUrl = "http://localhost:8181/";
    protected static String port;
    protected static String ACCOUNTS = "accounts";
    private static final Jsonb jsonb = JsonbBuilder.create();


    /**
     * Makes a GET request to the /accounts/{id} endpoint and returns a Response
     */
    protected Response getAccount(int accountId) {
        WebTarget webTarget = client.target(baseUrl + ACCOUNTS + "/" + accountId);
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


    @BeforeAll
    public static void setUp() {
        client = ClientBuilder.newClient();
    }

    @Test
    public void getAccount_notExists_404() {
        assertEquals(
                Response.Status.NOT_FOUND.getStatusCode(), getAccount(100100).getStatus(), "Retrieving an account that does not exist should return 404");
    }

    @Test
    public void getAccount_NegativeId_500() {
        assertEquals(
                Response.Status.BAD_REQUEST.getStatusCode(), getAccount(-1).getStatus(), "Retrieving an account with negative id should return 500");
    }

    @Test
    public void createAccount_notValidEmail_500() {
        assertEquals(
                Response.Status.BAD_REQUEST.getStatusCode(), createAccount("not_an_email", new BigDecimal(100)).getStatus(), "Creating an account with not valid email should return 500");
    }

    @Test
    public void createAccount_negativeAmount_500() {
        assertEquals(
                Response.Status.BAD_REQUEST.getStatusCode(), createAccount("email@example.com", new BigDecimal(-100)).getStatus(), "Creating an account with negative amount id should return 500");
    }

    @Test
    public void createAccount_emialAreadyExist_500() {
        String email = "email_3@example.com";
        BigDecimal amount = new BigDecimal(100);
        createAccount(email, amount);
        assertEquals(
                Response.Status.BAD_REQUEST.getStatusCode(), createAccount(email, amount).getStatus(), "Creating an account with existing email should return 500");
    }

    @Test
    public void getAccount_existing_201() {
        assertEquals(
                Response.Status.CREATED.getStatusCode(), createAccount("email@example.com", new BigDecimal(100)).getStatus(), "Creating a valid account should return 201");
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

}
