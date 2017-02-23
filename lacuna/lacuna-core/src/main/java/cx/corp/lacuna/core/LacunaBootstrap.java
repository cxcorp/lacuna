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

/**
 * Provides easy access to the main Lacuna classes, bootstrapped to the
 * correct platform. Contains static factory methods for creating new
 * instances for specific and auto-detected platforms. Windows and Linux
 * bootstraps are provided.
 *
 * <p>The members are initialized in the following manner:
 * <table summary="Members other than NativeProcessCollector are initialized with *Impl classes.">
 *     <tr><th>Type</th><th>Description</th></tr>
 *     <tr>
 *         <td>MemoryReader</td>
 *         <td>
 *             A new {@link MemoryReaderImpl} instance with a
 *             platform-specific {@link RawMemoryReader}
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>MemoryWriter</td>
 *         <td>
 *             A new {@link MemoryWriterImpl} instance with a
 *             platform-specific {@link RawMemoryWriter}
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>NativeProcessEnumerator</td>
 *         <td>
 *             A new {@link NativeProcessEnumeratorImpl} instance with
 *             platform-specific {@link PidEnumerator} and
 *             {@link NativeProcessCollector} instances
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>NativeProcessCollector</td>
 *         <td>
 *             A new platform-specific {@link NativeProcessCollector}
 *         </td>
 *     </tr>
 * </table>
 *
 * @see MemoryReader
 * @see MemoryWriter
 * @see NativeProcessEnumerator
 * @see NativeProcessCollector
 */
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

    /**
     * Gets a new {@link LacunaBootstrap} instance for the current platform.
     *
     * <p>Platform detection is handled with the JNA library's {@link Platform} class
     * in the following manner: if the current platform is a Windows platform,
     * {@link #forWindows()} is called, otherwise {@link #forLinux()} is called.
     * @return a new {@link LacunaBootstrap} instance bootstrapped for Windows if
     * the current platform is Windows, otherwise an instance bootstrapped for
     * Linux is returned.
     */
    public static LacunaBootstrap forCurrentPlatform() {
        return forDetectedPlatform(Platform::isWindows);
    }

    static LacunaBootstrap forDetectedPlatform(PlatformDetector detector) {
        return detector.isWindows() ? forWindows() : forLinux();
    }

    /**
     * Gets a new {@link LacunaBootstrap} instance for the Linux platform.
     * @return a new instance bootstrapped for the Linux platform.
     */
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

    /**
     * Gets a new {@link LacunaBootstrap} instance for the Windows platform.
     *
     * <p>It is important to note that the Windows bootstrap uses native
     * libraries to interact with the processes. These native libraries are
     * not present on other platforms, meaning that this method will throw an
     * exception if used on non-Windows platforms.
     *
     * @return a new instance bootstrapped for the Windows platform.
     * @throws UnsatisfiedLinkError if the required native libraries are not
     *                              found, for example when used on non-Windows
     *                              platforms.
     */
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

    /**
     * Gets the {@link MemoryReader} instance associated with the bootstrapper.
     * @return the {@link MemoryReader} instance associated with the bootstrapper.
     */
    public MemoryReader getMemoryReader() {
        return memoryReader;
    }

    /**
     * Gets the {@link MemoryWriter} instance associated with the bootstrapper.
     * @return the {@link MemoryWriter} instance associated with the bootstrapper.
     */
    public MemoryWriter getMemoryWriter() {
        return memoryWriter;
    }

    /**
     * Gets the {@link NativeProcessEnumerator} instance associated with the bootstrapper.
     * @return the {@link NativeProcessEnumerator} instance associated with the bootstrapper.
     */
    public NativeProcessEnumerator getNativeProcessEnumerator() {
        return nativeProcessEnumerator;
    }

    /**
     * Gets the {@link NativeProcessCollector} instance associated with the bootstrapper.
     * @return the {@link NativeProcessCollector} instance associated with the bootstrapper.
     */
    public NativeProcessCollector getNativeProcessCollector() {
        return nativeProcessCollector;
    }

    @FunctionalInterface
    interface PlatformDetector {
        boolean isWindows();
    }
}
