package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Proxy interface to the Windows API {@code psapi.dll} library.
 *
 * <p>In order to map the Java-style camelCase method names to the correct
 * PascalCase names, {@link CamelToPascalCaseFunctionMapper} can be used.
 * @see WinApiBootstrapper
 * @cx.winapiinterface
 */
public interface Psapi extends StdCallLibrary {
    /**
     * See <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms682629(v=vs.85).aspx">EnumProcesses function</a>.
     */
    boolean enumProcesses(int[] pids, int pidsLength, IntByReference bytesReturned);

    int getModuleFileNameExW(int hProcess, int hModule, char[] charBuf, int bufSize);
}
