package com.payments.account.web;

import com.payments.account.repo.TransactionDao;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.Positive;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("transactions")
public class TransactionController {
    @Inject
    private TransactionDao transactionDao;


    @GET
    @Path("{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByAccount(@PathParam("accountId") @Positive int transactionId) {

        return Response.ok(transactionDao.findByAccountId(transactionId)).build();


    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllTransactions() {
        return Response.ok(transactionDao.getAll()).build();
    }

}
