package com.iorga.ivif.ja;

import com.iorga.ivif.ja.ClientMessages.Message;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Provider
@ServerInterceptor
public class AddClientMessagesInterceptor implements PostProcessInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(AddClientMessagesInterceptor.class);

    @Inject
    private ClientMessages clientMessages;

    @Inject
    private HeaderUtil headerUtil;

    @Override
    public void postProcess(ServerResponse response) {
        final List<Message> messages = clientMessages.getMessages();

        if (!messages.isEmpty()) {
            try {
                headerUtil.writeTo(messages, "Messages", response);
            } catch (IOException e) {
                LOG.error("Error while adding client messages to the response", e);
            }
        }
    }
}
