package com.iorga.ivif.ja;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Provider
public class FunctionalExceptionMapper implements ExceptionMapper<FunctionalException> {
    private static final Logger LOG = LoggerFactory.getLogger(FunctionalExceptionMapper.class);

    @Inject
    private ClientMessages clientMessages;

    @Inject
    private HeaderUtil headerUtil;

    @Override
    public Response toResponse(FunctionalException exception) {
        final List<Message> messages = clientMessages.getMessages();
        if (!messages.isEmpty()) {
            try {
                LOG.error("FunctionalException will swallow client messages " + headerUtil.toJsonString(messages), exception);
            } catch (IOException e) {
                LOG.error("FunctionalException will swallow client messages");
                LOG.error("Problem while rendering client messages to json string", e);
            }
        }

        String base64ExceptionTemplate;
        try {
            base64ExceptionTemplate = headerUtil.toBase64JsonString(exception.getFunctionalMessage());
        } catch (IOException e) {
            LOG.error("Problem while rendering the exception to base64", e);
            base64ExceptionTemplate = "";
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .header(MessageUtils.MESSAGES_HEADER, base64ExceptionTemplate)
                .build();
    }
}
