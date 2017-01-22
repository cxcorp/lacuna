package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.ptr.IntByReference;

public class MockPsapi implements Psapi {

    private boolean enumProcessesReturnValue = true;
    private int[] pids = new int[0];

    public void setPids(int[] pids) {
        this.pids = pids;
    }

    public void setEnumProcessesReturnValue(boolean val) {
        this.enumProcessesReturnValue = val;
    }

    @Override
    public boolean enumProcesses(int[] pids, int pidsLength, IntByReference bytesReturned) {
        int valuesCopied = copyTo(this.pids, pids, pidsLength);
        bytesReturned.setValue(valuesCopied * WinApiConstants.SIZEOF_INT);
        return enumProcessesReturnValue;
    }

    @Override
    public int getModuleFileNameExW(int hProcess, int hModule, char[] charBuf, int bufSize) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private static int copyTo(int[] source, int[] target, int maxCount) {
        // copy everything from source to target, limited by maxCount
        int limit = Math.min(source.length, maxCount);
        for (int i = 0; i < limit; i++) {
            target[i] = source[i];
        }
        return limit;
    }
}
