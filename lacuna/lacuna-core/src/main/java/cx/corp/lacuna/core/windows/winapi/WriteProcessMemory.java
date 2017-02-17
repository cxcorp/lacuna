package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.nio.ByteBuffer;

/**
 * Proxy interface to the Windows API {@code kernel32.dll} library.
 * <p>
 * <p>In order to map the Java-style camelCase method names to the correct
 * PascalCase names, {@link CamelToPascalCaseFunctionMapper} can be used.
 *
 * @cx.winapiinterface
 * @see WinApiBootstrapper
 * @see Kernel32
 */
@FunctionalInterface
public interface WriteProcessMemory extends StdCallLibrary {
    /**
     * See <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms681674(v=vs.85).aspx">WriteProcessMemory function</a>.
     */
    boolean writeProcessMemory(int handle,
                               int offset,
                               byte[] data,
                               int numberOfBytes,
                               IntByReference bytesWritten);
}
