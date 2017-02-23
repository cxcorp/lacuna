package cx.corp.lacuna.core;

/**
 * Thrown to indicate that an error occurred while enumerating the running processes.
 */
public class ProcessEnumerationException extends RuntimeException {

    /**
     * Constructs an {@code ProcessEnumerationException} with the specified detail message.
     * @param msg the detail message.
     */
    public ProcessEnumerationException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param msg the detail message.
     * @param cause the cause.
     */
    public ProcessEnumerationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
