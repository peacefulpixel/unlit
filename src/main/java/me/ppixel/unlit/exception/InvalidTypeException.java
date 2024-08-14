package me.ppixel.unlit.exception;

/**
 * Component type processing exception.<br>
 * Usually happens when the type doesn't have required constructor or its value definition is wrong (like invalid syntax
 * of vaadin icon).
 * <br>
 * All the exception constructors are directly froward to the RuntimeException ones
 * @see <a href="https://github.com/peacefulpixel/unlit/tree/master>Official README</a>
 */
public class InvalidTypeException extends RuntimeException {
    public InvalidTypeException() {
    }

    public InvalidTypeException(String message) {
        super(message);
    }

    public InvalidTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTypeException(Throwable cause) {
        super(cause);
    }

    public InvalidTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
