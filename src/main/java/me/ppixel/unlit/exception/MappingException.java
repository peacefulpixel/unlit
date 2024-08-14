package me.ppixel.unlit.exception;

/**
 * Component field mapping exception.<br>
 * This exception may happen during mapping of internal field of your MappedComponent (e.g. with @Id or @MarkupField).
 * It's being caused by multiple different reasons, including:<br>
 *   - Wrong name of setter in the XML template (it's not exists, or not applicable with provided value)<br>
 *   - Its parent doesn't implement {@link com.vaadin.flow.component.HasComponents} interface, so it can't be added as a
 *     child.<br>
 *   - Child with provided ID is not found.
 * <br>
 * All the exception constructors are directly froward to the RuntimeException ones
 * @see <a href="https://github.com/peacefulpixel/unlit/tree/master>Official README</a>
 */
public class MappingException extends RuntimeException {
    public MappingException() {
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(Throwable cause) {
        super(cause);
    }

    public MappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
