package com.iorga.ivif.ja;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EJBTransactionRollbackExceptionMapper implements ExceptionMapper<EJBTransactionRolledbackException> {
    @Inject
    private FunctionalExceptionMapper functionalExceptionMapper;

    @Inject
    private GlobalExceptionMapper globalExceptionMapper;

    @Override
    public Response toResponse(EJBTransactionRolledbackException e) {
        final Throwable cause = e.getCause();
        if (cause instanceof FunctionalException) {
            return functionalExceptionMapper.toResponse((FunctionalException) cause);
        } else {
            return globalExceptionMapper.toResponse(e);
        }
    }
}
