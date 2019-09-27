package com.payments.account.utils;

import com.payments.account.exception.AccountAlreadyExistsException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccountAlreadyExistsMapper implements ExceptionMapper<AccountAlreadyExistsException> {

    public Response toResponse(AccountAlreadyExistsException e) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}