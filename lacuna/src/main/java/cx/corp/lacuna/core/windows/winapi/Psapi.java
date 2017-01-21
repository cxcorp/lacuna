package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Psapi extends StdCallLibrary {
    boolean EnumProcesses(int[] pids, int pidsLength, IntByReference bytesReturned);
    int GetModuleFileNameExW(int hProcess, int hModule, char[] charBuf, int bufSize);
}
