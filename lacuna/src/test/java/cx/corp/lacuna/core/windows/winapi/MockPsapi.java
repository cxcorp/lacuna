package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.ptr.IntByReference;

public class MockPsapi implements Psapi {

    private boolean enumProcessesReturnValue = true;
    private int[] enumProcessesPids = new int[0];
    private int enumProcessesBytesReturned = 0;

    public void setEnumProcessesPids(int[] pids) {
        this.enumProcessesPids = pids;
    }

    public void setEnumProcessesReturnValue(boolean val) {
        this.enumProcessesReturnValue = val;
    }

    public void setEnumProcessesBytesReturned(int bytesReturned) {
        this.enumProcessesBytesReturned = bytesReturned;
    }

    @Override
    public boolean enumProcesses(int[] pids, int pidsLength, IntByReference bytesReturned) {
        if (!enumProcessesReturnValue) {
            return false;
        }
        int limit = Math.min(this.enumProcessesPids.length, pidsLength);
        System.arraycopy(this.enumProcessesPids, 0, pids, 0, limit);
        bytesReturned.setValue(enumProcessesBytesReturned);
        return true;
    }

    @Override
    public int getModuleFileNameExW(int hProcess, int hModule, char[] charBuf, int bufSize) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
