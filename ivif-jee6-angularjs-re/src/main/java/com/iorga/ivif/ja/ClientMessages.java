package com.iorga.ivif.ja;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class ClientMessages {

    private List<Message> messages = new ArrayList<>();


    public static enum Level {
        SUCCESS, INFO, WARNING, ERROR
    }

    public static enum Type {
        MODAL, MESSAGE, FIELD
    }

    public static class Message {
        private String title;
        private Level level;
        private Type type;
        private String messageTemplate;
        private Object[] messageParameters;
        private String message;

        public Message(Level level, Type type, String title, String messageTemplate, Object... messageParameters) {
            this.level = level;
            this.type = type;
            this.title = title;
            this.messageTemplate = messageTemplate;
            this.messageParameters = messageParameters;
        }

        public Level getLevel() {
            return level;
        }

        public Type getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            if (message == null) {
                message = String.format(messageTemplate, messageParameters);
            }
            return message;
        }

        @JsonIgnore
        public String getMessageTemplate() {
            return messageTemplate;
        }

        @JsonIgnore
        public Object[] getMessageParameters() {
            return messageParameters;
        }
    }

    public void addMessage(Level level, String title, String messageTemplate, Object... messageParameters) {
        messages.add(new Message(level, Type.MESSAGE, title, messageTemplate, messageParameters));
    }

    public void addModalMessage(Level level, String title, String messageTemplate, Object... messageParameters) {
        messages.add(new Message(level, Type.MODAL, title, messageTemplate, messageParameters));
    }

    public ClientMessages addSuccess(String messageTemplate, Object... messageParameters) {
        return addSuccessWithTitle(null, messageTemplate, messageParameters);
    }

    public ClientMessages addSuccessWithTitle(String title, String messageTemplate, Object... messageParameters) {
        addMessage(Level.SUCCESS, title, messageTemplate, messageParameters);
        return this;
    }

    public ClientMessages addInfo(String messageTemplate, Object... messageParameters) {
        return addInfoWithTitle(null, messageTemplate, messageParameters);
    }

    public ClientMessages addInfoWithTitle(String title, String messageTemplate, Object... messageParameters) {
        addMessage(Level.INFO, title, messageTemplate, messageParameters);
        return this;
    }

    public ClientMessages addWarning(String messageTemplate, Object... messageParameters) {
        return addWarningWithTitle(null, messageTemplate, messageParameters);
    }

    public ClientMessages addWarningWithTitle(String title, String messageTemplate, Object... messageParameters) {
        addMessage(Level.WARNING, title, messageTemplate, messageParameters);
        return this;
    }

    public ClientMessages addError(String messageTemplate, Object... messageParameters) {
        return addErrorWithTitle(null, messageTemplate, messageParameters);
    }

    public ClientMessages addErrorWithTitle(String title, String messageTemplate, Object... messageParameters) {
        addMessage(Level.ERROR, title, messageTemplate, messageParameters);
        return this;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
