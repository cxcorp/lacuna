package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import cx.corp.lacuna.core.ProcessOpenException;
import cx.corp.lacuna.core.windows.winapi.CloseHandle;
import cx.corp.lacuna.core.windows.winapi.OpenProcess;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.Objects;

/**
 * {@inheritDoc}
 * @cx.useswinapi
 */
public class WindowsProcessOpener implements ProcessOpener {

    private final OpenProcess openProcess;
    private final CloseHandle closeHandle;

    /**
     * Constructs a new {@code WindowsProcessOpener} with the specified
     * {@code OpenProcess} and {@code CloseHandle} Windows API proxy. A
     * {@link cx.corp.lacuna.core.windows.winapi.Kernel32} instance may be
     * used.
     * @param processOpenerAndHandleCloser the Windows API {@code OpenProcess}
     *                                     and {@code CloseHandle} proxy.
     * @param <T> the type which implements both {@link OpenProcess} and
     *            {@link CloseHandle}.
     * @see cx.corp.lacuna.core.windows.winapi.Kernel32
     * @cx.useswinapi
     */
    public <T extends OpenProcess & CloseHandle> WindowsProcessOpener(T processOpenerAndHandleCloser) {
        this(processOpenerAndHandleCloser, processOpenerAndHandleCloser);
    }

    WindowsProcessOpener(OpenProcess openProcess, CloseHandle closeHandle) {
        Objects.requireNonNull(openProcess, "openProcess cannot be null!");
        Objects.requireNonNull(closeHandle, "closeHandle cannot be null!");
        this.openProcess = openProcess;
        this.closeHandle = closeHandle;
    }

    @Override
    public ProcessHandle open(int pid, int processAccessFlags) throws ProcessOpenException {
        int handle = openProcess.openProcess(processAccessFlags, false, pid);

        if (handle == WinApiConstants.NULL) {
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
            closeHandle.closeHandle(this.nativeHandle);
        }
    }
}
