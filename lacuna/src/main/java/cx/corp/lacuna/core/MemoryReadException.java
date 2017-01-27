package cx.corp.lacuna.core;

public class MemoryReadException extends RuntimeException {
    public MemoryReadException(String msg) {
        super(msg);
    }
    public MemoryReadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
