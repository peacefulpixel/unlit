package me.ppixel.unlit.exception;

public class InvalidSourceException extends RuntimeException {

    public InvalidSourceException() {
    }

    public InvalidSourceException(String message) {
        super(message);
    }

    public InvalidSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSourceException(Throwable cause) {
        super(cause);
    }

    public InvalidSourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
