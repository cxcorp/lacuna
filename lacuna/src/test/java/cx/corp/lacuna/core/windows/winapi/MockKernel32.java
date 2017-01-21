package cx.corp.lacuna.core.windows.winapi;

public class MockKernel32 implements Kernel32 {
    @Override
    public int GetLastError() {
        return 0;
    }

    @Override
    public int OpenProcess(int processAccessFlags, boolean bInheritHandle, int processId) {
        return 0;
    }
}
