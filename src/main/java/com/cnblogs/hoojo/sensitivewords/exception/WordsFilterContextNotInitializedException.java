package com.cnblogs.hoojo.sensitivewords.exception;

public class WordsFilterContextNotInitializedException extends Exception {
    public WordsFilterContextNotInitializedException() {
    }

    public WordsFilterContextNotInitializedException(String message) {
        super(message);
    }

    public WordsFilterContextNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public WordsFilterContextNotInitializedException(Throwable cause) {
        super(cause);
    }

    public WordsFilterContextNotInitializedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
