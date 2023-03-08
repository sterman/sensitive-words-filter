package com.cnblogs.hoojo.sensitivewords.exception;

public class CreateWordsFilterException extends Exception {
    public CreateWordsFilterException() {
    }

    public CreateWordsFilterException(String message) {
        super(message);
    }

    public CreateWordsFilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateWordsFilterException(Throwable cause) {
        super(cause);
    }

    public CreateWordsFilterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
