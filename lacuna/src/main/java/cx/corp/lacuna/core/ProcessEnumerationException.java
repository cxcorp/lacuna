package cx.corp.lacuna.core;

public class ProcessEnumerationException extends RuntimeException {
    public ProcessEnumerationException(String msg) {
        super(msg);
    }

    public ProcessEnumerationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
