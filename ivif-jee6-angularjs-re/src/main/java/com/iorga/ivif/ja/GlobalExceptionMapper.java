package com.iorga.ivif.ja;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    public static final String HEADER_PREFIX = HeaderUtil.HEADER_PREFIX + "Exception";

    @Inject
    private ClientMessages clientMessages;

    @Inject
    private HeaderUtil headerUtil;

    @Override
    public Response toResponse(Throwable throwable) {
        String uuid = UUID.randomUUID().toString();

        swallowClientMessages(throwable, uuid);

        final Response response = selectHandleMethod(throwable, uuid);
        if (response == null) {
            return handleThrowable(throwable, uuid, null);
        } else {
            return response;
        }
    }

    private void swallowClientMessages(Throwable exception, String uuid) {

        final List<Message> messages = clientMessages.getMessages();
        if (!messages.isEmpty()) {
            final String endMessage = " for exception #" + uuid + " (" + exception.getClass().getName() + ": " + exception.getMessage() + ").";
            try {
                LOG.warn("Swallowing client messages " + headerUtil.toJsonString(messages) + endMessage);
            } catch (IOException e) {
                LOG.warn("Swallowing client messages " + endMessage);
                LOG.error("Problem while rendering client messages to json string", e);
            }
        }
    }

    private Response selectHandleMethod(Throwable throwable, String uuid) {
        if (throwable instanceof EJBException) {
            return selectHandleMethod(throwable.getCause(), uuid);
        } else if (throwable instanceof FunctionalException) {
            return handleFunctionalException((FunctionalException) throwable, uuid);
        } else if (throwable instanceof OptimisticLockException) {
            return handleOptimisticLockException((OptimisticLockException)throwable, uuid);
        } else if (throwable instanceof WebApplicationException) {
            return handleThrowable(throwable, uuid, ((WebApplicationException) throwable).getResponse());
        } else {
            return null;
        }
    }

    private Response handleFunctionalException(FunctionalException exception, String uuid) {
        String base64ExceptionTemplate;
        try {
            base64ExceptionTemplate = headerUtil.toBase64JsonString(exception.getFunctionalMessage());
        } catch (IOException e) {
            LOG.error("Problem while rendering the exception to base64 (#" + uuid + ")", e);
            base64ExceptionTemplate = "";
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .header(MessageUtils.MESSAGES_HEADER, base64ExceptionTemplate)
                .build();
    }

    private Response handleOptimisticLockException(OptimisticLockException exception, String uuid) {
        final Object entity = exception.getEntity();
        final String message;
        if (entity instanceof IEntity) {
            message = ((IEntity) entity).displayName() + " has been saved by another user since you have clicked on 'Edit' button. Please click on 'Cancel' in order to refresh all the data (be careful, your current modifications will be lost).";
        } else {
            message = exception.getMessage();
        }
        return handleFunctionalException(new FunctionalException(message), uuid);
    }

    private static class ExceptionTemplate {
        private final Throwable exception;
        private final String uuid;

        public ExceptionTemplate(Throwable exception, String uuid) {
            this.exception = exception;
            this.uuid = uuid;
        }

        public String getMessage() {
            return exception.getMessage();
        }

        public String getClassName() {
            return exception.getClass().getName();
        }

        public String getUuid() {
            return uuid;
        }
    }
    private Response handleThrowable(Throwable throwable, String uuid, Response responseTemplate) {
        LOG.error("Handling unexpected throwable #" + uuid, throwable);
        final ExceptionTemplate exceptionTemplate = new ExceptionTemplate(throwable, uuid);
        String base64ExceptionTemplate;
        try {
            base64ExceptionTemplate = headerUtil.toBase64JsonString(exceptionTemplate);
        } catch (IOException e) {
            LOG.error("Problem while rendering the throwable to base64", e);
            base64ExceptionTemplate = "";
        }
        ResponseBuilder responseBuilder;
        if (responseTemplate != null) {
            responseBuilder = Response.fromResponse(responseTemplate);
        } else {
            responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Throwables.getStackTraceAsString(throwable));
        }
        return responseBuilder
                .header(HEADER_PREFIX, base64ExceptionTemplate)
                .build();
    }
}
