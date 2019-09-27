package com.payments.account.utils;

import com.payments.account.exception.InsufficientFundsException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InsufficientFundsExceptionMapper implements ExceptionMapper<InsufficientFundsException> {

    public Response toResponse(InsufficientFundsException e) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
