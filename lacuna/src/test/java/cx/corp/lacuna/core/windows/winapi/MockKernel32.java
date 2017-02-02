package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import org.junit.Before;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockKernel32 implements Kernel32 {

    private List<Integer> closedHandles = new ArrayList<>();

    private int openProcessReturnValue = WinApiConstants.NULLPTR;
    private boolean readProcessMemoryReturnValue = false;
    private byte[] readProcessReadBytes = new byte[0];
    private String queryFullProcessImageNameExeName = "";
    private boolean queryFullProcessImageSuccess = false;

    public void setOpenProcessReturnValue(int returnValue) {
        openProcessReturnValue = returnValue;
    }

    public void setReadProcessMemoryReturnValue(boolean val) {
        readProcessMemoryReturnValue = val;
    }

    public void setReadProcessReadMemory(byte[] bytes) {
        readProcessReadBytes = bytes;
    }

    public void setQueryFullProcessImageNameExeName(String queryFullProcessImageNameExeName) {
        this.queryFullProcessImageNameExeName = queryFullProcessImageNameExeName;
    }

    public void setQueryFullProcessImageSuccess(boolean queryFullProcessImageSuccess) {
        this.queryFullProcessImageSuccess = queryFullProcessImageSuccess;
    }

    public List<Integer> getClosedHandles() {
        return closedHandles;
    }

    @Override
    public boolean closeHandle(int handle) {
        closedHandles.add(handle);
        return true;
    }

    @Override
    public int getProcessId(int handle) {
        return 0;
    }

    @Override
    public int openProcess(int processAccessFlags, boolean bInheritHandle, int processId) {
        return openProcessReturnValue;
    }

    @Override
    public boolean queryFullProcessImageNameW(int hProcess, int dwFlags, char[] lpExeName, IntByReference lpdwSize) {
        if (!queryFullProcessImageSuccess) {
            return false;
        }
        char[] exeName = queryFullProcessImageNameExeName.toCharArray();
        int bufSize = Math.min(exeName.length, lpdwSize.getValue());
        System.arraycopy(exeName, 0, lpExeName, 0, bufSize);
        lpdwSize.setValue(bufSize);
        return true;
    }

    @Override
    public boolean readProcessMemory(int processHandle,
                                     int baseAddress,
                                     Memory buffer,
                                     int bufferSize,
                                     IntByReference bytesRead) {
        if (!readProcessMemoryReturnValue) {
            return false;
        }
        int count = Math.min(bufferSize, readProcessReadBytes.length);
        buffer.write(0, readProcessReadBytes, 0, count);
        bytesRead.setValue(count);
        return true;
    }
}
