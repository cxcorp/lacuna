package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
    boolean closeHandle(int handle);

    int openProcess(int processAccessFlags, boolean bInheritHandle, int processId);

    boolean queryFullProcessImageNameW(int hProcess,
                                      int dwFlags,
                                      char[] lpExeName,
                                      IntByReference lpdwSize);
}