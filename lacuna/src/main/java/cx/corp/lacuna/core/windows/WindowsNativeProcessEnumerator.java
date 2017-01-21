package cx.corp.lacuna.core.windows;

import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.ProcessEnumerationException;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WindowsNativeProcessEnumerator implements NativeProcessEnumerator {

    private final Kernel32 kernel;
    private final Psapi psapi;

    public WindowsNativeProcessEnumerator(Kernel32 kernel, Psapi psapi) {
        this.kernel = kernel;
        this.psapi = psapi;
    }

    @Override
    public List<NativeProcess> getProcesses() {
        int[] pids = getPids();

        ArrayList<NativeProcess> procs = new ArrayList<>();
        for (int pid : pids) {
            NativeProcess proc = new NativeProcess();
            proc.setPid(pid);
            procs.add(proc);
        }
        return procs;
    }

    private int[] getPids() {
        int[] pids = new int[WinApiConstants.MAX_PROCESSES_SUPPORTED];
        IntByReference bytesReturned = new IntByReference(0);

        if (!psapi.EnumProcesses(pids, pids.length, bytesReturned)) {
            throw new ProcessEnumerationException("EnumProcesses failed!");
        }

        int pidCount = bytesReturned.getValue() / WinApiConstants.SIZEOF_INT;
        return Arrays.copyOf(pids, pidCount);
    }
}
