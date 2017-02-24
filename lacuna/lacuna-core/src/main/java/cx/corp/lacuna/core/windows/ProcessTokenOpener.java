package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CloseHandle;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.Objects;

/**
 * Opens the access token associated with a native process.
 * @cx.useswinapi
 */
public class ProcessTokenOpener {

    private final Advapi32 advapi;
    private final CloseHandle handleCloser;

    /**
     * Constructs a new {@code ProcessTokenOpener} with the specified Windows
     * API {@code Advapi32} and {@code Kernel32 CloseHandle} proxy.
     * @param advapi the Windows API {@code Advapi32} proxy.
     * @param closeHandle the Windows API {@code Kernel32} proxy.
     * @throws NullPointerException if {@code advapi} or {@code closeHandle}
     *                              is null.
     * @cx.useswinapi
     */
    public ProcessTokenOpener(Advapi32 advapi, CloseHandle closeHandle) {
        Objects.requireNonNull(advapi, "advapi cannot be null!");
        Objects.requireNonNull(closeHandle, "closeHandle cannot be null!");
        this.advapi = advapi;
        this.handleCloser = closeHandle;
    }

    /**
     * Opens the access token associated with a native process.
     * <p>Opening the process token is handled by the Windows API
     * {@link Advapi32#openProcessToken} method.
     * @param processHandle a handle to the native process.
     * @return the access token associated with the native process.
     * @throws TokenOpenException if opening the access token fails.
     * @throws NullPointerException if {@code processHandle} is null.
     */
    public ProcessToken openToken(ProcessHandle processHandle) throws TokenOpenException {
        Objects.requireNonNull(processHandle, "processHandle cannot be null!");
        IntByReference token = new IntByReference(0);
        boolean success =
            advapi.openProcessToken(
                processHandle.getNativeHandle(),
                WinApiConstants.OPENPROCESSTOKEN_TOKEN_QUERY,
                token);

        if (!success) {
            Integer lastError = Native.getLastError();
            SystemErrorCode error = SystemErrorCode.fromId(lastError);
            String message = String.format(
                "Failed to open process token for process %d! System error: %s",
                processHandle.getNativeHandle(),
                error == null ? lastError.toString() : error.toString()
            );
            throw new TokenOpenException(message);
        }

        return new WinApiProcessToken(token.getValue());
    }

    private class WinApiProcessToken implements ProcessToken {

        private final int token;

        public WinApiProcessToken(int token) {
            this.token = token;
        }

        @Override
        public int getToken() {
            return token;
        }

        @Override
        public void close() {
            handleCloser.closeHandle(token);
        }
    }
}
