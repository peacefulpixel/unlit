package me.ppixel.unlit.exception;

/**
 * Unknown type exception.<br>
 * May happen when type provided in the XML template tag name can't be found by Unlit. Please, note that Unlit looking
 * for types only in com.vaadin.flow.component package or withing all the classpath if they're annotated with
 * {@link me.ppixel.unlit.annotation.TemplatableType}. The class responsible for type lookup is
 * {@link me.ppixel.unlit.UnlitTypeProvider}.<br>
 * Also, this exception may happen when there's wrong parameter's value type in the XML template. Please, verify all the
 * supported types and formats they're having to be described in
 * <a href="https://github.com/peacefulpixel/unlit/tree/master>Official README</a>
 * <br>
 * All the exception constructors are directly froward to the RuntimeException ones
 * @see <a href="https://github.com/peacefulpixel/unlit/tree/master>Official README</a>
 */
public class UnknownTypeException extends RuntimeException {
    public UnknownTypeException() {
    }

    public UnknownTypeException(String message) {
        super(message);
    }

    public UnknownTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownTypeException(Throwable cause) {
        super(cause);
    }

    public UnknownTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
