package cx.corp.lacuna.core;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
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

public final class LacunaBootstrap {

    private final MemoryReader memoryReader;
    private final MemoryWriter memoryWriter;
    private final NativeProcessEnumerator nativeProcessEnumerator;
    private final NativeProcessCollector nativeProcessCollector;

    private LacunaBootstrap(MemoryReader memoryReader,
                           MemoryWriter memoryWriter,
                           NativeProcessEnumerator nativeProcessEnumerator,
                           NativeProcessCollector nativeProcessCollector) {
        this.memoryReader = memoryReader;
        this.memoryWriter = memoryWriter;
        this.nativeProcessEnumerator = nativeProcessEnumerator;
        this.nativeProcessCollector = nativeProcessCollector;
    }

    public static LacunaBootstrap forLinux() {
        Path procRoot = LinuxConstants.DEFAULT_PROC_ROOT;
        PidEnumerator enumerator = new LinuxPidEnumerator(procRoot, LinuxConstants.DEFAULT_PID_MAX);
        NativeProcessCollector collector = new LinuxNativeProcessCollector(procRoot);

        NativeProcessEnumerator processEnumerator = new NativeProcessEnumeratorImpl(enumerator, collector);

        FileMemoryProvider memProvider = new FileMemoryProvider(Paths.get("/proc"));
        RawMemoryReader rawMemoryReader = new LinuxRawMemoryReader(memProvider);
        MemoryReader memoryReader = new MemoryReaderImpl(rawMemoryReader);

        RawMemoryWriter rawMemoryWriter = new LinuxRawMemoryWriter(memProvider);
        MemoryWriter memoryWriter = new MemoryWriterImpl(rawMemoryWriter);

        return new LacunaBootstrap(
            memoryReader,
            memoryWriter,
            processEnumerator,
            collector
        );
    }

    public static LacunaBootstrap forWindows() {
        Map<String, Object> options = new HashMap<>();
        // Use a mapper so that we can use Java-style function names in the interfaces
        FunctionMapper nameMapper = new CamelToPascalCaseFunctionMapper();
        options.put(Library.OPTION_FUNCTION_MAPPER, nameMapper);

        Kernel32 kernel = Native.loadLibrary("Kernel32", Kernel32.class, options);
        Psapi psapi = Native.loadLibrary("Psapi", Psapi.class, options);
        Advapi32 advapi = Native.loadLibrary("Advapi32", Advapi32.class, options);

        ProcessOpener procOpener = new WindowsProcessOpener(kernel);

        ProcessOwnerGetter ownerGetter = new WindowsProcessOwnerGetter(
            new ProcessTokenOpener(advapi, kernel),
            new TokenUserFinder(advapi),
            new TokenOwnerNameFinder(advapi)
        );
        ProcessDescriptionGetter descriptionGetter = new WindowsProcessDescriptionGetter(kernel);
        NativeProcessCollector collector =
            new WindowsNativeProcessCollector(procOpener, ownerGetter, descriptionGetter);

        PidEnumerator enumerator = new WindowsPidEnumerator(psapi);
        NativeProcessEnumerator processEnumerator = new NativeProcessEnumeratorImpl(enumerator, collector);

        RawMemoryReader rawMemoryReader = new WindowsRawMemoryReader(procOpener, kernel);
        MemoryReader memoryReader = new MemoryReaderImpl(rawMemoryReader);

        RawMemoryWriter rawWriter = new WindowsRawMemoryWriter(procOpener, kernel);
        MemoryWriter memoryWriter = new MemoryWriterImpl(rawWriter);
        return new LacunaBootstrap(
            memoryReader,
            memoryWriter,
            processEnumerator,
            collector
        );
    }

    public MemoryReader getMemoryReader() {
        return memoryReader;
    }

    public MemoryWriter getMemoryWriter() {
        return memoryWriter;
    }

    public NativeProcessEnumerator getNativeProcessEnumerator() {
        return nativeProcessEnumerator;
    }

    public NativeProcessCollector getNativeProcessCollector() {
        return nativeProcessCollector;
    }
}
