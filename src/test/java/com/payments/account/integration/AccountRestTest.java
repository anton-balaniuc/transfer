package com.payments.account.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class AccountRestTest {

    protected static Client client;
    protected Response response;
    protected static String baseUrl;
    protected static String port;
    protected static String ACCOUNTS = "accounts";


    /**
     *  Makes a GET request to the /accounts/{id} endpoint and returns a JsonObject
     */
    protected JsonObject getAccount(int accountId) {
        WebTarget webTarget = client.target(baseUrl + ACCOUNTS + "/" + accountId);
        response = webTarget.request().get();
        return response.readEntity(JsonObject.class);
    }


    @BeforeAll
    public static void setUp() {
        client = ClientBuilder.newClient();
    }

    @Test
    public void testInvalidRead() {
        Assertions.assertEquals(
                true, getAccount(-1).isEmpty(), "Reading an event that does not exist should return an empty list");
    }

}
