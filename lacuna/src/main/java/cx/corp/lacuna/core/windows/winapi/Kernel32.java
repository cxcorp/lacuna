package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
    boolean closeHandle(int handle);

    int getProcessId(int handle);

    int openProcess(int processAccessFlags, boolean bInheritHandle, int processId);

    boolean queryFullProcessImageNameW(int hProcess,
                                       int dwFlags,
                                       char[] lpExeName,
                                       IntByReference lpdwSize);

    boolean readProcessMemory(int processHandle,
                              int baseAddress,
                              Memory buffer,
                              int bufferSize,
                              IntByReference bytesRead);
}