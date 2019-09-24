package com.payments.account.web;

import com.payments.account.model.Account;
import com.payments.account.repo.AccountDao;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@RequestScoped
@Path("accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {
    @Inject
    private AccountDao accountDao;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response register(@Valid Account newAccount) {
        newAccount.setId(0);
        accountDao.create(newAccount);
        return Response.ok(newAccount).build();
    }

    @GET
    @Path("{id}")
    public Response find(@PathParam("id") int accountId) {
        return Response.ok(accountDao.find(accountId)).build();
    }

    @GET
    public Response findAll() {
        return Response.ok(accountDao.getAll()).build();
    }

    @PUT
    @Path("{id}/deposit/{amount}")
    @Transactional
    public Response deposit(@PathParam("id") int accountId, @PathParam("amount") BigDecimal amount) {
        return Response.ok().build();
    }

    @PUT
    @Path("{id}/withdrawal/{amount}")
    @Transactional
    public Response withdrawal(@PathParam("id") int accountId, @PathParam("amount") BigDecimal amount) {
        return Response.ok().build();
    }
}
