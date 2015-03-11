package com.iorga.ivif.ja;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EJBTransactionRollbackExceptionMapper implements ExceptionMapper<EJBTransactionRolledbackException> {
    @Inject
    private FunctionalExceptionMapper functionalExceptionMapper;

    @Inject
    private GlobalExceptionMapper globalExceptionMapper;

    @Inject
    private OptimisticLockExceptionMapper optimisticLockExceptionMapper;

    @Override
    public Response toResponse(EJBTransactionRolledbackException e) {
        final Throwable cause = e.getCause();
        if (cause instanceof FunctionalException) {
            return functionalExceptionMapper.toResponse((FunctionalException) cause);
        } else if (cause instanceof OptimisticLockException) {
            return optimisticLockExceptionMapper.toResponse((OptimisticLockException) cause);
        } else {
            return globalExceptionMapper.toResponse(e);
        }
    }
}
