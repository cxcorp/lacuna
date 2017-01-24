package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.ProcessEnumerationException;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.ArrayList;
import java.util.List;

public class WindowsNativeProcessEnumerator implements NativeProcessEnumerator {

    private final WindowsNativeProcessInfoGatherer infoGatherer;
    private final Psapi psapi;

    public WindowsNativeProcessEnumerator(Kernel32 kernel, Psapi psapi, Advapi32 advapi) {
        this.infoGatherer = new WindowsNativeProcessInfoGatherer(kernel, advapi); // todo: inject this some day
        this.psapi = psapi;
    }

    @Override
    public List<NativeProcess> getProcesses() {
        int[] pids = getProcessIds();
        return constructProcessModels(pids);
    }

    private int[] getProcessIds() {
        int[] pidBuffer = createMaxSizePidBuffer();
        int pidCount = enumerateProcesses(pidBuffer);
        // Trim the unused array values. Well, copy them to a smaller array.
        return copyToFittedArray(pidBuffer, pidCount);
    }

    private int[] createMaxSizePidBuffer() {
        return new int[WinApiConstants.MAX_PROCESSES_SUPPORTED];
    }

    private int enumerateProcesses(int[] pidBuffer) {
        IntByReference bytesReturned = new IntByReference(0);
        if (!psapi.enumProcesses(pidBuffer, pidBuffer.length, bytesReturned)) {
            throw new ProcessEnumerationException(
                    "Kernel32 EnumProcesses failed with error code " + Native.getLastError());
        }
        return byteCountToIntCount(bytesReturned.getValue());
    }

    private int byteCountToIntCount(int bytes) {
        return bytes / WinApiConstants.SIZEOF_INT;
    }

    private int[] copyToFittedArray(int[] array, int actualCount) {
        int[] fittedArray = new int[actualCount];
        System.arraycopy(array, 0, fittedArray, 0, actualCount);
        return fittedArray;
    }

    private List<NativeProcess> constructProcessModels(int[] pids) {
        ArrayList<NativeProcess> processes = new ArrayList<>();
        for (int pid : pids) {
            NativeProcess process = infoGatherer.gather(pid);
            processes.add(process);
        }
        return processes;
    }








}
