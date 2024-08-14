package me.ppixel.unlit.exception;

/**
 * Source locating exception.<br>
 * This exception caused by wrong XML template name or wrong template dir path. If XML template name isn't provided
 * explicitly, the class name will be used.<br>
 * Also, you can set template dir path by defining property me.ppixel.unlit.template-dir (by default it's "/").
 * <br>
 * All the exception constructors are directly froward to the RuntimeException ones
 * @see <a href="https://github.com/peacefulpixel/unlit/tree/master>Official README</a>
 */
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
