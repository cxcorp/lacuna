package cx.corp.lacuna.core;

/**
 * Thrown to indicate that an error occurred while enumerating the running processes.
 */
public class ProcessEnumerationException extends RuntimeException {
    public ProcessEnumerationException(String msg) {
        super(msg);
    }

    public ProcessEnumerationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
