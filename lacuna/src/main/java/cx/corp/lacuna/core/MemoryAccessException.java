package cx.corp.lacuna.core;

/**
 * Thrown to indicate that reading or writing to a native process's memory failed.
 */
public class MemoryAccessException extends RuntimeException {
    public MemoryAccessException(String msg) {
        super(msg);
    }

    public MemoryAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
