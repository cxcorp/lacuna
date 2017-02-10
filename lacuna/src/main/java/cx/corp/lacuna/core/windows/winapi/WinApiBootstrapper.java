package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;

import java.util.HashMap;
import java.util.Map;

/**
 * Bootstraps Windows API proxy interfaces for use.
 *
 * <p>This class configures a function name mapper using {@link CamelToPascalCaseFunctionMapper}
 * and lazily loads the native DLLs when called.
 */
public class WinApiBootstrapper {

    private final Map<String, Object> loadLibraryOptions;

    public WinApiBootstrapper() {
        loadLibraryOptions = new HashMap<>();
        // Use a mapper so that we can use Java-style function names in the interfaces
        FunctionMapper nameMapper = new CamelToPascalCaseFunctionMapper();
        loadLibraryOptions.put(Library.OPTION_FUNCTION_MAPPER, nameMapper);
    }

    public Kernel32 getKernel32() {
        return Native.loadLibrary("Kernel32", Kernel32.class, loadLibraryOptions);
    }

    public Advapi32 getAdvapi32() {
        return Native.loadLibrary("Advapi32", Advapi32.class, loadLibraryOptions);
    }

    public Psapi getPsapi() {
        return Native.loadLibrary("Psapi", Psapi.class, loadLibraryOptions);
    }
}
