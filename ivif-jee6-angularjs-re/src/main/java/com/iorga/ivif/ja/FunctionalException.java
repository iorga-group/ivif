package com.iorga.ivif.ja;

public class FunctionalException extends RuntimeException {
    private static final long serialVersionUID = -7126433759117612356L;
    private final Message message;

    public FunctionalException(String message) {
        this(MessageUtils.createError(message));
    }

    public FunctionalException(Message message) {
        super(message.getMessage());
        this.message = message;
    }

    public Message getFunctionalMessage() {
        return message;
    }
}
