package com.iorga.ivif.ja;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    public static final String HEADER_PREFIX = HeaderUtil.HEADER_PREFIX + "Exception";

    @Inject
    private ClientMessages clientMessages;

    @Inject
    private HeaderUtil headerUtil;

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
    @Override
    public Response toResponse(Throwable exception) {
        String uuid = UUID.randomUUID().toString();

        try {
            LOG.error("Catching global exception #" + uuid + ", swallowing client messages " + headerUtil.toJsonString(clientMessages.getMessages()), exception);
        } catch (IOException e) {
            LOG.error("Catching global exception #" + uuid + ", swallowing client messages", exception);
            LOG.error("Problem while rendering client messages to json string", e);
        }

        final ExceptionTemplate exceptionTemplate = new ExceptionTemplate(exception, uuid);
        String base64ExceptionTemplate;
        try {
            base64ExceptionTemplate = headerUtil.toBase64JsonString(exceptionTemplate);
        } catch (IOException e) {
            LOG.error("Problem while rendering the exception to base64", e);
            base64ExceptionTemplate = "";
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .header(HEADER_PREFIX, base64ExceptionTemplate)
                .entity(Throwables.getStackTraceAsString(exception))
                .build();
    }
}
