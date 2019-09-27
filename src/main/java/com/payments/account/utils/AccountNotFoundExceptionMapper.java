package com.payments.account.utils;

import com.payments.account.exception.AccountNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccountNotFoundExceptionMapper  implements ExceptionMapper<AccountNotFoundException> {

    public Response toResponse(AccountNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
