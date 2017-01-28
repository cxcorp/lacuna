package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import org.junit.Before;

import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Set;

public class MockKernel32 implements Kernel32 {

    private int openProcessReturnValue = WinApiConstants.NULLPTR;
    private boolean readProcessMemoryReturnValue = false;
    private byte[] readProcessReadBytes = new byte[0];

    public void setOpenProcessReturnValue(int returnValue) {
        openProcessReturnValue = returnValue;
    }

    public void setReadProcessMemoryReturnValue(boolean val) {
        readProcessMemoryReturnValue = val;
    }

    public void setReadProcessReadMemory(byte[] bytes) {
        readProcessReadBytes = bytes;
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
        int count = Math.min(bufferSize, readProcessReadBytes.length);
        buffer.write(0, readProcessReadBytes, 0, count);
        bytesRead.setValue(count);
        return readProcessMemoryReturnValue;
    }
}
