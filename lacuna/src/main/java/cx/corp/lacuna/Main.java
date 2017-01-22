package cx.corp.lacuna;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.windows.WindowsNativeProcessEnumerator;
import cx.corp.lacuna.core.windows.winapi.CamelToPascalCaseFunctionMapper;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;
import cx.corp.lacuna.ui.MainWindow;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Use a mapper so that we can use Java-style function names in the interfaces
        FunctionMapper nameMapper = new CamelToPascalCaseFunctionMapper();
        Map<String, Object> options = new HashMap<>();
        options.put(Library.OPTION_FUNCTION_MAPPER, nameMapper);

        Kernel32 kernel = Native.loadLibrary("Kernel32", Kernel32.class, options);
        Psapi psapi = Native.loadLibrary("Psapi", Psapi.class, options);

        NativeProcessEnumerator enumerator = new WindowsNativeProcessEnumerator(kernel, psapi);
        List<NativeProcess> processes = enumerator.getProcesses();

        for (NativeProcess proc : processes) {
            System.out.printf("%-5d %s%n", proc.getPid(), proc.getDescription());
        }
    }
}