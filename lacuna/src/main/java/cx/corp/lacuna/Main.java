package cx.corp.lacuna;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.linux.LinuxNativeProcessEnumerator;
import cx.corp.lacuna.core.windows.WindowsNativeProcessEnumerator;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CamelToPascalCaseFunctionMapper;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.Psapi;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        int pid = 16080;
        int handle = kernel.openProcess(
                ProcessAccessFlags.QUERY_INFORMATION | ProcessAccessFlags.VIRTUAL_MEMORY_READ,
                false,
                pid);
        Memory buffer = new Memory(20);
        IntByReference read = new IntByReference(0);
        boolean success = kernel.readProcessMemory(
                handle,
                -1,
                buffer,
                (int) buffer.size(),
                read);

        if (success) {
            System.out.println("Success");
            System.out.println(buffer.getInt(0));
            System.out.println(buffer.getInt(4));
            System.out.println(buffer.getByte(8));
            System.out.println(buffer.getByte(9));
            System.out.println(buffer.getShort(10));

            try {
                System.out.println(buffer.getInt(12));
            } catch (Error error) {
                // Access violation possibly
                System.out.println(error.toString());
            }
        } else {
            System.out.println("success = " + success);
            System.out.println("Native.getLastError() = " + SystemErrorCode.fromId(Native.getLastError()).getError());
        }
        kernel.closeHandle(handle);
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