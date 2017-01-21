package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
    int GetLastError();
    int OpenProcess(int processAccessFlags, boolean bInheritHandle, int processId);
}