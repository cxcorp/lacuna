package cx.corp.lacuna;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.NativeProcessEnumeratorImpl;
import cx.corp.lacuna.core.RawMemoryReader;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.linux.LinuxNativeProcessCollector;
import cx.corp.lacuna.core.linux.LinuxPidEnumerator;
import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.PidEnumerator;
import cx.corp.lacuna.core.linux.FileMemoryProvider;
import cx.corp.lacuna.core.linux.LinuxConstants;
import cx.corp.lacuna.core.linux.LinuxRawMemoryReader;
import cx.corp.lacuna.core.windows.ProcessDescriptionGetter;
import cx.corp.lacuna.core.windows.ProcessOpener;
import cx.corp.lacuna.core.windows.ProcessOwnerGetter;
import cx.corp.lacuna.core.windows.WindowsNativeProcessCollector;
import cx.corp.lacuna.core.windows.WindowsProcessDescriptionGetter;
import cx.corp.lacuna.core.windows.WindowsProcessOpener;
import cx.corp.lacuna.core.windows.WindowsProcessOwnerGetter;
import cx.corp.lacuna.core.MemoryReaderImpl;
import cx.corp.lacuna.core.windows.WindowsPidEnumerator;
import cx.corp.lacuna.core.windows.WindowsRawMemoryReader;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CamelToPascalCaseFunctionMapper;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static NativeProcessEnumerator processEnumerator;
    private static MemoryReader memoryReader;

    public static void main(String[] args) throws IOException {
        setupPlatformSpecificStuff();

        List<NativeProcess> processes = processEnumerator.getProcesses();
        for (NativeProcess proc : processes) {
            System.out.printf(
                    "%-5d    %-8s    %s%n",
                    proc.getPid(),
                    proc.getOwner(),
                    proc.getDescription());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter target PID: ");
        final int targetPid = scanner.nextInt();

        Optional<NativeProcess> proc = processes
                .stream()
                .filter(p -> p.getPid() == targetPid)
                .findFirst();

        if (!proc.isPresent()) {
            System.out.println("Process " + targetPid +  " not found!");
            return;
        }

        System.out.print("Enter target offset: 0x");
        int offset = scanner.nextInt(16);
        System.out.print("How many bytes to read? 0x");
        int count = scanner.nextInt(16);

        byte[] bytes = memoryReader.read(
                proc.get(),
                offset,
                count);

        for (int i = 0; i < bytes.length; i++) {
            if (i != 0 && i % 16 == 0) {
                System.out.println();
            }
            System.out.printf("%02X ", bytes[i]);
        }

        if (count >= 0x10) {
            NativeProcess process = proc.get();
            System.out.println(memoryReader.readInt(process, offset + 0));
            System.out.println(memoryReader.readInt(process, offset + 4));
            System.out.println(memoryReader.readBoolean(process, offset + 8));
            System.out.println(memoryReader.readBoolean(process, offset + 9));
            System.out.println(memoryReader.readShort(process, offset + 10));
            System.out.println(memoryReader.readInt(process, offset + 12));
        }

        System.out.println();
    }

    private static void setupPlatformSpecificStuff() {
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

        PidEnumerator enumerator = new WindowsPidEnumerator(psapi);
        ProcessOpener procOpener = new WindowsProcessOpener(kernel);
        ProcessOwnerGetter ownerGetter = new WindowsProcessOwnerGetter(advapi, kernel);
        ProcessDescriptionGetter descriptionGetter = new WindowsProcessDescriptionGetter(kernel);
        NativeProcessCollector collector =
            new WindowsNativeProcessCollector(procOpener, ownerGetter, descriptionGetter);

        processEnumerator = new NativeProcessEnumeratorImpl(enumerator, collector);

        RawMemoryReader rawMemoryReader = new WindowsRawMemoryReader(procOpener, kernel);
        memoryReader = new MemoryReaderImpl(rawMemoryReader);
    }

    private static void setupForLinux() {
        Path procRoot = LinuxConstants.DEFAULT_PROC_ROOT;
        PidEnumerator enumerator = new LinuxPidEnumerator(procRoot, LinuxConstants.DEFAULT_PID_MAX);
        NativeProcessCollector collector = new LinuxNativeProcessCollector(procRoot);

        processEnumerator = new NativeProcessEnumeratorImpl(enumerator, collector);

        FileMemoryProvider memProvider = new FileMemoryProvider(
            Paths.get("/proc"),
            Paths.get("mem"));
        RawMemoryReader rawMemoryReader = new LinuxRawMemoryReader(memProvider);
        memoryReader = new MemoryReaderImpl(rawMemoryReader);
    }
}
