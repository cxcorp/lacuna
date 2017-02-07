package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

public class WindowsProcessOpener implements ProcessOpener {

    private final Kernel32 kernel;

    public WindowsProcessOpener(Kernel32 kernel) {
        if (kernel == null) {
            throw new IllegalArgumentException("kernel cannot be null");
        }
        this.kernel = kernel;
    }

    @Override
    public ProcessHandle open(int pid, int processAccessFlags) throws ProcessOpenException {
        int handle = kernel.openProcess(processAccessFlags, false, pid);

        if (handle == WinApiConstants.NULLPTR) {
            Integer lastError = Native.getLastError();
            SystemErrorCode error = SystemErrorCode.fromId(lastError);
            String message = String.format(
                "Failed to open process %d for reading! System error: %s",
                pid,
                error == null ? lastError.toString() : error.toString()
            );
            throw new ProcessOpenException(message);
        }

        return new WinApiProcessHandle(handle);
    }

    private class WinApiProcessHandle implements ProcessHandle {

        private final int nativeHandle;

        private WinApiProcessHandle(int nativeHandle) {
            this.nativeHandle = nativeHandle;
        }

        @Override
        public int getNativeHandle() {
            return nativeHandle;
        }

        @Override
        public void close() {
            // non-static child classes can access parent **instance**!
            // make use of this to access kernel32
            kernel.closeHandle(this.nativeHandle);
        }
    }
}
