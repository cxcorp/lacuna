package cx.corp.lacuna.core;

public class ProcessAccessException extends RuntimeException {

    public ProcessAccessException(String message) {
        super(message);
    }

    public ProcessAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
