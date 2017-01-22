package cx.corp.lacuna.core.windows.winapi;

public class MockKernel32 implements Kernel32 {
    @Override
    public int getLastError() {
        return 0;
    }

    @Override
    public int openProcess(int processAccessFlags, boolean bInheritHandle, int processId) {
        return 0;
    }
}
