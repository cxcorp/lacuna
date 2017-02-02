package cx.corp.lacuna.core.windows;

public interface ProcessHandle extends AutoCloseable {
    int getNativeHandle();

    @Override
    void close(); // no exception here
}
