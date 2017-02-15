package cx.corp.lacuna.core.windows;

/**
 * Thrown to indicate that opening a handle to a native process failed.
 */
public class ProcessOpenException extends RuntimeException {
    public ProcessOpenException(String message) {
        super(message);
    }
}
