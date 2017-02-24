package cx.corp.lacuna.core.windows;

/**
 * Thrown to indicate that opening a handle to a native process failed.
 */
public class ProcessOpenException extends RuntimeException {
    /**
     * Constructs a new {@code ProcessOpenException} with the specified detail message.
     * @param message the detail message.
     */
    public ProcessOpenException(String message) {
        super(message);
    }
}
