package cx.corp.lacuna.core;

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

    /**
     * Constructs a new {@code ProcessOpenException} with the specified detail
     * message and cause.
     * @param message the detail message.
     * @param cause the cause.
     */
    public ProcessOpenException(String message, Throwable cause) {
        super(message, cause);
    }
}
