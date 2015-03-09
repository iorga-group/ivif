package com.iorga.ivif.ja;

import com.iorga.ivif.ja.Message.Level;

import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class ClientMessages {

    private List<Message> messages = new ArrayList<>();


    public ClientMessages addMessage(Level level, String title, String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createMessage(level, title, messageTemplate, messageParameters));
        return this;
    }

    public ClientMessages addModalMessage(Level level, String title, String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createModalMessage(level, title, messageTemplate, messageParameters));
        return this;
    }

    public ClientMessages addSuccess(String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createSuccess(messageTemplate, messageParameters));
        return this;
    }

    public ClientMessages addSuccessWithTitle(String title, String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createSuccessWithTitle(title, messageTemplate, messageParameters));
        return this;
    }

    public ClientMessages addInfo(String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createInfo(messageTemplate, messageParameters));
        return this;
    }

    public ClientMessages addInfoWithTitle(String title, String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createInfoWithTitle(title, messageTemplate, messageParameters));
        return this;
    }

    public ClientMessages addWarning(String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createWarning(messageTemplate, messageParameters));
        return this;
    }

    public ClientMessages addWarningWithTitle(String title, String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createWarningWithTitle(title, messageTemplate, messageParameters));
        return this;
    }

    public ClientMessages addError(String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createError(messageTemplate, messageParameters));
        return this;
    }

    public ClientMessages addErrorWithTitle(String title, String messageTemplate, Object... messageParameters) {
        messages.add(MessageUtils.createErrorWithTitle(title, messageTemplate, messageParameters));
        return this;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
