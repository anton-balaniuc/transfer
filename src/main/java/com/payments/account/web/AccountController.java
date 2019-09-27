package com.payments.account.web;

import com.payments.account.model.Account;
import com.payments.account.service.AccountService;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@RequestScoped
@Path("accounts")
public class AccountController {

    private AccountService accountService;

    @Inject
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary = "Registers new account")
    public Response register(@Valid Account newAccount) {
        accountService.create(newAccount);
        return Response.status(Response.Status.CREATED).entity(newAccount.getId()).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@PathParam("id") @Positive int accountId) {
        return Response.ok(accountService.find(accountId)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        return Response.ok(accountService.getAll()).build();
    }

    @PUT
    @Path("{id}/deposit/{amount}")
    @Transactional
    public Response deposit(@PathParam("id") @Positive int accountId, @PathParam("amount") @Positive BigDecimal amount) {
        accountService.deposit(accountId, amount);
        return Response.ok().build();
    }

    @PUT
    @Path("{id}/withdrawal/{amount}")
    @Transactional
    public Response withdrawal(@PathParam("id") @Positive int accountId, @PathParam("amount") @Positive BigDecimal amount) {

        accountService.withdrawal(accountId, amount);
        return Response.ok().build();
    }

    @PUT
    @Path("{from}/transfer/{to}/{amount}")
    @Transactional
    public Response transfer(@PathParam("from") @Positive int from,
                             @PathParam("to") @Positive int to,
                             @PathParam("amount") @Positive BigDecimal amount) {
        accountService.transfer(from, to, amount);
        return Response.ok().build();
    }
}
