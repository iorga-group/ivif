package com.iorga.ivif.ja;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class OptimisticLockExceptionMapper implements ExceptionMapper<OptimisticLockException> {
    @Inject
    private FunctionalExceptionMapper functionalExceptionMapper;

    @Override
    public Response toResponse(OptimisticLockException exception) {
        final Object entity = exception.getEntity();
        final String message;
        if (entity instanceof IEntity) {
            message = ((IEntity) entity).displayName() + " has been saved by another user since you have clicked on 'Edit' button. Please click on 'Cancel' in order to refresh all the data (be careful, your current modifications will be lost).";
        } else {
            message = exception.getMessage();
        }
        return functionalExceptionMapper.toResponse(new FunctionalException(message));
    }
}
