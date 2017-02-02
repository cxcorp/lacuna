package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.windows.ProcessDescriptionGetter;
import cx.corp.lacuna.core.windows.ProcessHandle;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.Optional;

public class WinApiProcessDescriptionGetter implements ProcessDescriptionGetter {

    private final Kernel32 kernel;

    public WinApiProcessDescriptionGetter(Kernel32 kernel) {
        if (kernel == null) {
            throw new IllegalArgumentException("kernel cannot be null!");
        }
        this.kernel = kernel;
    }

    @Override
    public Optional<String> get(ProcessHandle processHandle) {
        if (processHandle == null) {
            throw new IllegalArgumentException("processHandle cannot be null!");
        }
        return getProcessImageName(processHandle.getNativeHandle());
    }

    private Optional<String> getProcessImageName(int processHandle) {
        char[] nameBuf = new char[WinApiConstants.MAX_FILENAME_LENGTH];
        IntByReference bufferSize = new IntByReference(nameBuf.length);

        boolean success =
            kernel.queryFullProcessImageNameW(
                processHandle,
                WinApiConstants.QUERYFULLPROCESSIMAGENAME_PATHFORMAT_WIN32,
                nameBuf,
                bufferSize);

        // bufferSize gets updated with the amount of written characters
        // as a consequence of a successful call
        return success
            ? Optional.of(new String(nameBuf, 0, bufferSize.getValue()))
            : Optional.empty();
    }
}
