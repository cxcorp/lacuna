package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.win32.StdCallLibrary;

public interface OpenProcess extends StdCallLibrary {
    int openProcess(int processAccessFlags, boolean bInheritHandle, int processId);
}
