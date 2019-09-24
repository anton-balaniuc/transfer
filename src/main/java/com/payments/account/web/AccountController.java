package com.payments.account.web;

import com.payments.account.model.Account;
import com.payments.account.model.Error;
import com.payments.account.model.Transaction;
import com.payments.account.model.TransactionType;
import com.payments.account.repo.AccountDao;
import com.payments.account.repo.TransactionDao;
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
    @Inject
    private AccountDao accountDao;
    @Inject
    private TransactionDao transactionDao;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
            summary = "Registers new account")

    public Response register(@Valid Account newAccount) {
        if (!accountDao.findByEmail(newAccount.getEmail()).isEmpty()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(new Error(Error.ACCOUNT_EXISTS)).build());
        }
        accountDao.create(newAccount);
        return Response.status(Response.Status.CREATED).entity(newAccount.getId()).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@PathParam("id") @Positive int accountId) {
        Account account = accountDao.find(accountId);

        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(account).build();


    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        return Response.ok(accountDao.getAll()).build();
    }

    @PUT
    @Path("{id}/deposit/{amount}")
    @Transactional
    public Response deposit(@PathParam("id") @Positive int accountId, @PathParam("amount") @Positive BigDecimal amount) {
        Account account = accountDao.find(accountId);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setCredit(account);
        transaction.setDebit(account);

        account.deposit(amount);
        accountDao.update(account);
        transactionDao.create(transaction);
        return Response.ok().build();
    }

    @PUT
    @Path("{id}/withdrawal/{amount}")
    @Transactional
    public Response withdrawal(@PathParam("id") @Positive int accountId, @PathParam("amount") @Positive BigDecimal amount) {
        return Response.ok().build();
    }
}
