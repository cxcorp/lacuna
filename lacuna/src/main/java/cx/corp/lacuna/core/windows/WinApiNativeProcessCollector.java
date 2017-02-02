package cx.corp.lacuna.core.windows;

import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.Optional;

public class WinApiNativeProcessCollector implements NativeProcessCollector {

    private final Kernel32 kernel;
    private final ProcessOpener processOpener;
    private final ProcessOwnerGetter ownerGetter;

    public WinApiNativeProcessCollector(ProcessOpener processOpener,
                                        ProcessOwnerGetter ownerGetter,
                                        Kernel32 kernel) {
        if (processOpener == null || ownerGetter == null || kernel == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        this.processOpener = processOpener;
        this.ownerGetter = ownerGetter;
        this.kernel = kernel;
    }

    public NativeProcess collect(int pid) {
        NativeProcess process = new NativeProcessImpl(
            pid,
            NativeProcess.UNKNOWN_DESCRIPTION,
            NativeProcess.UNKNOWN_OWNER);

        try (ProcessHandle handle = processOpener.open(pid, ProcessAccessFlags.QUERY_INFORMATION)) {
            process.setDescription(getProcessDescription(handle));
            process.setOwner(ownerGetter.get(handle).orElse(NativeProcess.UNKNOWN_OWNER));
        } catch (ProcessOpenException ex) {
            // TODO: log
        }

        return process;
    }

    private String getProcessDescription(ProcessHandle processHandle) {
        return getProcessImageName(processHandle.getNativeHandle())
            .orElse(NativeProcess.UNKNOWN_DESCRIPTION);
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
