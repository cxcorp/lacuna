package cx.corp.lacuna.core.windows;

public class MockProcessHandle implements ProcessHandle {

    private int nativeHandle;
    private boolean wasClosed;

    public MockProcessHandle() {}
    public MockProcessHandle(int handleValue) {
        nativeHandle = handleValue;
    }

    public void setNativeHandle(int handle) {
        this.nativeHandle = handle;
    }

    public boolean wasClosed() {
        return wasClosed;
    }

    @Override
    public int getNativeHandle() {
        return nativeHandle;
    }

    @Override
    public void close() {
        wasClosed = true;
    }
}
