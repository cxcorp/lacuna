package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.ProcessEnumerationException;
import cx.corp.lacuna.core.PidEnumerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WinApiPidEnumerator implements PidEnumerator {

    private final Psapi psapi;

    public WinApiPidEnumerator(Psapi psapi) {
        if (psapi == null) {
            throw new IllegalArgumentException("psapi cannot be null!");
        }
        this.psapi = psapi;
    }

    @Override
    public List<Integer> getPids() {
        int[] pidBuffer = createMaxSizePidBuffer();
        int pidCount = fillBufferWithProcessIds(pidBuffer);
        return Arrays.stream(pidBuffer, 0, pidCount)
            .boxed()
            .collect(Collectors.toList());
    }

    private int[] createMaxSizePidBuffer() {
        return new int[WinApiConstants.MAX_PROCESSES_SUPPORTED];
    }

    private int fillBufferWithProcessIds(int[] pidBuffer) {
        IntByReference bytesReturned = new IntByReference(0);
        if (!psapi.enumProcesses(pidBuffer, pidBuffer.length, bytesReturned)) {
            throw new ProcessEnumerationException(
                "WinApi EnumProcesses failed with error code " + Native.getLastError());
        }
        return byteCountToIntCount(bytesReturned.getValue());
    }

    private int byteCountToIntCount(int bytes) {
        return bytes / WinApiConstants.SIZEOF_INT;
    }
}
