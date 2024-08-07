package me.ppixel.unlit.exception;

// TODO: Doc
public class NoSuchSourceException extends RuntimeException {
    public NoSuchSourceException() {
    }

    public NoSuchSourceException(String message) {
        super(message);
    }

    public NoSuchSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchSourceException(Throwable cause) {
        super(cause);
    }

    public NoSuchSourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
