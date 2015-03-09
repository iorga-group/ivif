package com.iorga.ivif.ja;

import com.iorga.ivif.ja.Message.Level;
import com.iorga.ivif.ja.Message.Type;

public class MessageUtils {
    public static String MESSAGES_HEADER_SUFFIX = "Messages";
    public static String MESSAGES_HEADER = HeaderUtil.HEADER_PREFIX + MESSAGES_HEADER_SUFFIX;

    public static Message createMessage(Level level, String title, String messageTemplate, Object... messageParameters) {
        return new Message(level, Type.MESSAGE, title, messageTemplate, messageParameters);
    }

    public static Message createModalMessage(Level level, String title, String messageTemplate, Object... messageParameters) {
        return new Message(level, Type.MODAL, title, messageTemplate, messageParameters);
    }

    public static Message createSuccessWithTitle(String title, String messageTemplate, Object... messageParameters) {
        return createMessage(Level.SUCCESS, title, messageTemplate, messageParameters);
    }

    public static Message createSuccess(String messageTemplate, Object... messageParameters) {
        return createSuccessWithTitle(null, messageTemplate, messageParameters);
    }

    public static Message createInfoWithTitle(String title, String messageTemplate, Object... messageParameters) {
        return createMessage(Level.INFO, title, messageTemplate, messageParameters);
    }

    public static Message createInfo(String messageTemplate, Object... messageParameters) {
        return createInfoWithTitle(null, messageTemplate, messageParameters);
    }

    public static Message createWarningWithTitle(String title, String messageTemplate, Object... messageParameters) {
        return createMessage(Level.WARNING, title, messageTemplate, messageParameters);
    }

    public static Message createWarning(String messageTemplate, Object... messageParameters) {
        return createWarningWithTitle(null, messageTemplate, messageParameters);
    }

    public static Message createErrorWithTitle(String title, String messageTemplate, Object... messageParameters) {
        return createMessage(Level.ERROR, title, messageTemplate, messageParameters);
    }

    public static Message createError(String messageTemplate, Object... messageParameters) {
        return createErrorWithTitle(null, messageTemplate, messageParameters);
    }
}
