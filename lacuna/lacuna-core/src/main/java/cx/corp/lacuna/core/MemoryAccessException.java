package cx.corp.lacuna.core;

/**
 * Thrown to indicate that reading or writing to a native process's memory failed.
 */
public class MemoryAccessException extends RuntimeException {

    /**
     * Constructs an {@code MemoryAccessException} with the specified detail message.
     * @param msg the detail message.
     */
    public MemoryAccessException(String msg) {
        super(msg);
    }


    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param msg the detail message.
     * @param cause the cause.
     */
    public MemoryAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
