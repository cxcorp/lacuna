package cx.corp.lacuna.core.windows;

import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.nio.CharBuffer;

public class WindowsNativeProcessInfoGatherer {

    private final static String NAME_FOR_UNREADABLE_PROCESS = "";

    private final Kernel32 kernel;

    public WindowsNativeProcessInfoGatherer(Kernel32 kernel) {
        this.kernel = kernel;
    }

    public NativeProcess gather(int pid) {
        NativeProcess proc = new NativeProcess();
        proc.setPid(pid);
        proc.setDescription(getProcessName(pid));
        return proc;
    }

    private String getProcessName(int pid) {
        int processHandle = openProcessForInformationReading(pid);
        String name = queryProcessName(processHandle);
        freeProcessHandle(processHandle);
        return name;
    }

    private int openProcessForInformationReading(int pid) {
        return kernel.openProcess(ProcessAccessFlags.QUERY_INFORMATION, false, pid);
    }

    private String queryProcessName(int processHandle) {
        char[] nameBuf = new char[WinApiConstants.MAX_FILENAME_LENGTH];
        IntByReference bufferSize = new IntByReference(nameBuf.length);

        boolean success =
                kernel.queryFullProcessImageNameW(
                        processHandle,
                        WinApiConstants.QUERYFULLPROCESSIMAGENAME_PATHFORMAT_WIN32,
                        nameBuf,
                        bufferSize);

        if (!success) {
            return NAME_FOR_UNREADABLE_PROCESS;
        }

        // bufferSize gets updated with the amount of written characters
        // as a consequence of a successful call
        return new String(nameBuf, 0, bufferSize.getValue());
    }

    private void freeProcessHandle(int handle) {
        if (handle != WinApiConstants.NULLPTR) {
            kernel.closeHandle(handle);
        }
    }
}
