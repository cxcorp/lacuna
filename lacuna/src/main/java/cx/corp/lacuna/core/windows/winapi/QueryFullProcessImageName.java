package cx.corp.lacuna.core.windows.winapi;

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
public interface QueryFullProcessImageName extends StdCallLibrary {
    /**
     * See <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms684919(v=vs.85).aspx">QueryFullProcessImageName function</a>.
     */
    boolean queryFullProcessImageNameW(int hProcess,
                                       int dwFlags,
                                       char[] lpExeName,
                                       IntByReference lpdwSize);
}
