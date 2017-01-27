package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Set;

public class MockKernel32 implements Kernel32 {

    private int openProcessReturnValue;
    private boolean readProcessMemoryReturnValue;

    public void setOpenProcessReturnValue(int returnValue) {
        openProcessReturnValue = returnValue;
    }

    public void setReadProcessMemoryReturnValue(boolean val) {
        readProcessMemoryReturnValue = val;
    }

    @Override
    public boolean closeHandle(int handle) {
        return true;
    }

    @Override
    public int openProcess(int processAccessFlags, boolean bInheritHandle, int processId) {
        return openProcessReturnValue;
    }

    @Override
    public boolean queryFullProcessImageNameW(int hProcess, int dwFlags, char[] lpExeName, IntByReference lpdwSize) {
        return false;
    }

    @Override
    public boolean readProcessMemory(int processHandle,
                                     int baseAddress,
                                     Memory buffer,
                                     int bufferSize,
                                     IntByReference bytesRead) {
        return readProcessMemoryReturnValue;
    }
}
