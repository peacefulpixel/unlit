package me.ppixel.unlit.exception;

/**
 * Source processing exception.<br>
 * This exception indicates that provided source is invalid, which is usually caused by wrong XML syntax.
 * <br>
 * All the exception constructors are directly froward to the RuntimeException ones
 * @see <a href="https://github.com/peacefulpixel/unlit/tree/master>Official README</a>
 */
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
