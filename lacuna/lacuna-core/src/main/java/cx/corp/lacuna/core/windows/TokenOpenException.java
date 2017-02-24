package cx.corp.lacuna.core.windows;

/**
 * Thrown to indicate that opening an access token for a process failed.
 */
public class TokenOpenException extends RuntimeException {
    /**
     * Constructs a new {@code TokenOpenException} with the specified detail message.
     * @param message the detail message.
     */
    public TokenOpenException(String message) {
        super(message);
    }
}
