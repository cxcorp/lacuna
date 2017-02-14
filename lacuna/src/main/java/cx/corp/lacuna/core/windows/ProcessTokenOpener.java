package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CloseHandle;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

public class ProcessTokenOpener {

    private final Advapi32 advapi;
    private final CloseHandle handleCloser;

    public ProcessTokenOpener(Advapi32 advapi, CloseHandle closeHandle) {
        if (advapi == null || closeHandle == null) {
            throw new IllegalArgumentException("Args cannot be null!");
        }
        this.advapi = advapi;
        this.handleCloser = closeHandle;
    }

    public ProcessToken openToken(ProcessHandle processHandle) throws TokenOpenException {
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
