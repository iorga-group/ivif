package com.iorga.ivif.ja;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Message {

    public static enum Level {
        SUCCESS, INFO, WARNING, ERROR
    }

    public static enum Type {
        MODAL, MESSAGE, FIELD
    }

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


    public String getMessage() {
        if (message == null) {
            message = String.format(messageTemplate, messageParameters);
        }
        return message;
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

    @JsonIgnore
    public String getMessageTemplate() {
        return messageTemplate;
    }

    @JsonIgnore
    public Object[] getMessageParameters() {
        return messageParameters;
    }
}
