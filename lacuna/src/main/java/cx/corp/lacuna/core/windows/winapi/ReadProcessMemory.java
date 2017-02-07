package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

@FunctionalInterface
public interface ReadProcessMemory extends StdCallLibrary {
    boolean readProcessMemory(int processHandle,
                              int baseAddress,
                              Memory buffer,
                              int bufferSize,
                              IntByReference bytesRead);
}
