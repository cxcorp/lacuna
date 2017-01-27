package cx.corp.lacuna;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.linux.LinuxNativeProcessEnumerator;
import cx.corp.lacuna.core.windows.WindowsMemoryReader;
import cx.corp.lacuna.core.windows.WindowsNativeProcessEnumerator;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CamelToPascalCaseFunctionMapper;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.Psapi;

import java.util.*;

public class Main {

    public static void main(String[] args) {
       /* NativeProcessEnumerator enumerator = Platform.isWindows()
                ? bootstrapWindowsEnumerator()
                : new LinuxNativeProcessEnumerator();
*/
        Map<String, Object> options = new HashMap<>();
        // Use a mapper so that we can use Java-style function names in the interfaces
        FunctionMapper nameMapper = new CamelToPascalCaseFunctionMapper();
        options.put(Library.OPTION_FUNCTION_MAPPER, nameMapper);

        Kernel32 kernel = Native.loadLibrary("Kernel32", Kernel32.class, options);
        Psapi psapi = Native.loadLibrary("Psapi", Psapi.class, options);
        Advapi32 advapi = Native.loadLibrary("Advapi32", Advapi32.class, options);

        NativeProcessEnumerator enumerator = new WindowsNativeProcessEnumerator(kernel, psapi, advapi);

        List<NativeProcess> processes = enumerator.getProcesses();
        for (NativeProcess proc : processes) {
            System.out.printf(
                    "%-5d    %-8s    %s%n",
                    proc.getPid(),
                    proc.getOwner(),
                    proc.getDescription());
        }

        MemoryReader reader = new WindowsMemoryReader(kernel);
        NativeProcess process =
            processes.stream()
                .filter(proc -> proc.getDescription().contains("TestTarget"))
                .findFirst()
                .get();
        byte[] bytes = reader.read(process, 0x8AAC38, 16);
        System.out.println("Arrays.toString(bytes) = " + Arrays.toString(bytes));
    }

    private static NativeProcessEnumerator bootstrapWindowsEnumerator() {
        Map<String, Object> options = new HashMap<>();
        // Use a mapper so that we can use Java-style function names in the interfaces
        FunctionMapper nameMapper = new CamelToPascalCaseFunctionMapper();
        options.put(Library.OPTION_FUNCTION_MAPPER, nameMapper);

        Kernel32 kernel = Native.loadLibrary("Kernel32", Kernel32.class, options);
        Psapi psapi = Native.loadLibrary("Psapi", Psapi.class, options);
        Advapi32 advapi = Native.loadLibrary("Advapi32", Advapi32.class, options);

        return new WindowsNativeProcessEnumerator(kernel, psapi, advapi);
    }
}