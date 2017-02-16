package cx.corp.lacuna.core;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import cx.corp.lacuna.core.linux.FileMemoryProvider;
import cx.corp.lacuna.core.linux.LinuxConstants;
import cx.corp.lacuna.core.linux.LinuxNativeProcessCollector;
import cx.corp.lacuna.core.linux.LinuxPidEnumerator;
import cx.corp.lacuna.core.linux.LinuxRawMemoryReader;
import cx.corp.lacuna.core.linux.LinuxRawMemoryWriter;
import cx.corp.lacuna.core.windows.ProcessDescriptionGetter;
import cx.corp.lacuna.core.windows.ProcessOpener;
import cx.corp.lacuna.core.windows.ProcessOwnerGetter;
import cx.corp.lacuna.core.windows.ProcessTokenOpener;
import cx.corp.lacuna.core.windows.TokenOwnerNameFinder;
import cx.corp.lacuna.core.windows.TokenUserFinder;
import cx.corp.lacuna.core.windows.WindowsNativeProcessCollector;
import cx.corp.lacuna.core.windows.WindowsPidEnumerator;
import cx.corp.lacuna.core.windows.WindowsProcessDescriptionGetter;
import cx.corp.lacuna.core.windows.WindowsProcessOpener;
import cx.corp.lacuna.core.windows.WindowsProcessOwnerGetter;
import cx.corp.lacuna.core.windows.WindowsRawMemoryReader;
import cx.corp.lacuna.core.windows.WindowsRawMemoryWriter;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CamelToPascalCaseFunctionMapper;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Lacuna {
    private static MemoryReader memoryReader;
    private static MemoryWriter memoryWriter;
    private static NativeProcessEnumerator nativeProcessEnumerator;
    private static NativeProcessCollector nativeProcessCollector;
    private static PidEnumerator pidEnumerator;

    static {
        bootstrap();
    }

    public static MemoryReader getMemoryReader() {
        return memoryReader;
    }

    public static MemoryWriter getMemoryWriter() {
        return memoryWriter;
    }

    public static NativeProcessEnumerator getNativeProcessEnumerator() {
        return nativeProcessEnumerator;
    }

    public static NativeProcessCollector getNativeProcessCollector() {
        return nativeProcessCollector;
    }

    public static PidEnumerator getPidEnumerator() {
        return pidEnumerator;
    }

    private static void bootstrap() {
        if (Platform.isWindows()) {
            setupForWindows();
        } else {
            setupForLinux();
        }
    }

    private static void setupForWindows() {
        Map<String, Object> options = new HashMap<>();
        // Use a mapper so that we can use Java-style function names in the interfaces
        FunctionMapper nameMapper = new CamelToPascalCaseFunctionMapper();
        options.put(Library.OPTION_FUNCTION_MAPPER, nameMapper);

        Kernel32 kernel = Native.loadLibrary("Kernel32", Kernel32.class, options);
        Psapi psapi = Native.loadLibrary("Psapi", Psapi.class, options);
        Advapi32 advapi = Native.loadLibrary("Advapi32", Advapi32.class, options);

        pidEnumerator = new WindowsPidEnumerator(psapi);
        ProcessOpener procOpener = new WindowsProcessOpener(kernel);

        ProcessOwnerGetter ownerGetter = new WindowsProcessOwnerGetter(
            new ProcessTokenOpener(advapi, kernel),
            new TokenUserFinder(advapi),
            new TokenOwnerNameFinder(advapi)
        );
        ProcessDescriptionGetter descriptionGetter = new WindowsProcessDescriptionGetter(kernel);
        nativeProcessCollector =
            new WindowsNativeProcessCollector(procOpener, ownerGetter, descriptionGetter);

        nativeProcessEnumerator = new NativeProcessEnumeratorImpl(pidEnumerator, nativeProcessCollector);

        RawMemoryReader rawMemoryReader = new WindowsRawMemoryReader(procOpener, kernel);
        memoryReader = new MemoryReaderImpl(rawMemoryReader);

        RawMemoryWriter rawWriter = new WindowsRawMemoryWriter(procOpener, kernel);
        memoryWriter = new MemoryWriterImpl(rawWriter);
    }

    private static void setupForLinux() {
        Path procRoot = LinuxConstants.DEFAULT_PROC_ROOT;
        pidEnumerator = new LinuxPidEnumerator(procRoot, LinuxConstants.DEFAULT_PID_MAX);
        nativeProcessCollector = new LinuxNativeProcessCollector(procRoot);

        nativeProcessEnumerator = new NativeProcessEnumeratorImpl(pidEnumerator, nativeProcessCollector);

        FileMemoryProvider memProvider = new FileMemoryProvider(Paths.get("/proc"));

        RawMemoryReader rawMemoryReader = new LinuxRawMemoryReader(memProvider);
        memoryReader = new MemoryReaderImpl(rawMemoryReader);

        RawMemoryWriter rawMemoryWriter = new LinuxRawMemoryWriter(memProvider);
        memoryWriter = new MemoryWriterImpl(rawMemoryWriter);
    }
}
