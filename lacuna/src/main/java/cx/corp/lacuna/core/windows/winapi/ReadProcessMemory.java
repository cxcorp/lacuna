package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Proxy interface to the Windows API {@code kernel32.dll} library.
 *
 * <p>In order to map the Java-style camelCase method names to the correct
 * PascalCase names, {@link CamelToPascalCaseFunctionMapper} can be used.
 * @see WinApiBootstrapper
 * @cx.winapiinterface
 * @see Kernel32
 */
@FunctionalInterface
public interface ReadProcessMemory extends StdCallLibrary {
    /**
     * See <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms680553(v=vs.85).aspx">ReadProcessMemory function</a>.
     */
    boolean readProcessMemory(int processHandle,
                              int baseAddress,
                              Memory buffer,
                              int bufferSize,
                              IntByReference bytesRead);
}
