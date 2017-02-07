package cx.corp.lacuna.core.windows;

import com.sun.jna.Platform;
import cx.corp.lacuna.core.IntegrationTestConstants;
import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.RawMemoryReader;
import cx.corp.lacuna.core.TestTargetLauncher;
import cx.corp.lacuna.core.MemoryReaderImpl;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;
import cx.corp.lacuna.core.windows.winapi.WinApiBootstrapper;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WindowsMemoryReaderIT {

    private static final int TESTTARGET_STRUCT_SIZE = 0x10;

    private final WinApiBootstrapper winapi;
    private MemoryReaderImpl reader;
    private Kernel32 kernel32;
    private TestTargetLauncher testTargetLauncher;

    public WindowsMemoryReaderIT() {
        winapi = new WinApiBootstrapper();
    }

    @Before
    public void setUp() {
        // Don't run platform specific integration tests on
        // wrong platform
        Assume.assumeTrue(Platform.isWindows());

        kernel32 = winapi.getKernel32();

        ProcessOpener opener = new WindowsProcessOpener(kernel32);
        RawMemoryReader rawReader = new WindowsRawMemoryReader(opener, kernel32);
        reader = new MemoryReaderImpl(rawReader);
        Path testTargetPath = IntegrationTestConstants.getTestTargetUrlForWindows();
        testTargetLauncher = new TestTargetLauncher(testTargetPath);
    }

    @Test
    public void correctValuesAreReadFromTestTargetMemory() throws Exception {
        int arg1 = 0x12345678;
        int arg2 = 0xFFFFFFFF;
        boolean arg3 = true;
        boolean arg4 = false;
        short arg5 = 12345;
        String arg6 = "yyyisss";

        ProcessBuilder builder = testTargetLauncher
            .withFirstArg(arg1)
            .withSecondArg(arg2)
            .withThirdArg(arg3)
            .withFourthArg(arg4)
            .withFifthArg(arg5)
            .withSixthArg(arg6)
            .createBuilder();

        Process targetProcess = builder.start();
        try {
            // Our TestTarget program outputs the address of the data structure
            // in hexadecimal to stdout after it starts.
            Scanner processOutput = new Scanner(new InputStreamReader(targetProcess.getInputStream()));

            // note to self: if execution gets stuck here, you gotta add a fflush(stdout) call to TestTarget
            int targetAddress = processOutput.nextInt(16);

            int pid = IntegrationTestUtils.getPid(kernel32, targetProcess);
            NativeProcess process = new NativeProcessImpl(pid, null, null);

            int readArg1 = reader.readInt(process, targetAddress + 0);
            int readArg2 = reader.readInt(process, targetAddress + 4);
            boolean readArg3 = reader.readBoolean(process, targetAddress + 8);
            boolean readArg4 = reader.readBoolean(process, targetAddress + 9);
            short readArg5 = reader.readShort(process, targetAddress + 10);

            int readArg6Pointer = reader.readInt(process, targetAddress + 12);
            String readArg6 = reader.readString(process, readArg6Pointer, arg6.length());

            assertEquals(arg1, readArg1);
            assertEquals(arg2, readArg2);
            assertEquals(arg3, readArg3);
            assertEquals(arg4, readArg4);
            assertEquals(arg5, readArg5);
            assertEquals(arg6, readArg6);
        } finally {
            targetProcess.destroyForcibly();
        }
    }
}
