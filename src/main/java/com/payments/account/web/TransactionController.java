package com.payments.account.web;

import com.payments.account.model.Account;
import com.payments.account.model.Error;
import com.payments.account.model.Transaction;
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
@Path("transactions")
public class TransactionController {
    @Inject
    private TransactionDao transactionDao;


    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@PathParam("id") @Positive int transactionId) {
        Transaction transaction = transactionDao.find(transactionId);

        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(transaction).build();


    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        return Response.ok(transactionDao.getAll()).build();
    }

}
