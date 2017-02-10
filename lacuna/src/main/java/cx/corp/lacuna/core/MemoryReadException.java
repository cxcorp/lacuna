package cx.corp.lacuna.core;

/**
 * Thrown to indicate that reading a native process's memory failed.
 */
public class MemoryReadException extends RuntimeException {
    public MemoryReadException(String msg) {
        super(msg);
    }

    public MemoryReadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
