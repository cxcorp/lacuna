package cx.corp.lacuna.core.windows;

import com.sun.jna.Platform;
import cx.corp.lacuna.core.IntegrationTestConstants;
import cx.corp.lacuna.core.TestTargetLauncher;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.serialization.Boolean8Serializer;
import cx.corp.lacuna.core.serialization.Int32Serializer;
import cx.corp.lacuna.core.serialization.TypeSerializer;
import cx.corp.lacuna.core.serialization.TypeSerializers;
import cx.corp.lacuna.core.serialization.TypeSerializersImpl;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;
import cx.corp.lacuna.core.windows.winapi.WinApiBootstrapper;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WindowsMemoryReaderIT {

    private static final int TESTTARGET_STRUCT_SIZE = 0x10;

    private final TypeSerializers serializers;
    private final WinApiBootstrapper winapi;
    private WindowsMemoryReader reader;
    private WindowsNativeProcessCollector processCollector;
    private Kernel32 kernel32;
    private TestTargetLauncher testTargetLauncher;

    public WindowsMemoryReaderIT() {
        winapi = new WinApiBootstrapper();
        serializers = new TypeSerializersImpl();
        setupSerializers();
    }

    private void setupSerializers() {
        serializers.register(Boolean.class, new Boolean8Serializer());
        serializers.register(Integer.class, new Int32Serializer());
    }

    @Before
    public void setUp() {
        // Don't run platform specific integration tests on
        // wrong platform
        Assume.assumeTrue(Platform.isWindows());

        kernel32 = winapi.getKernel32();
        Advapi32 advapi32 = winapi.getAdvapi32();
        Psapi psapi = winapi.getPsapi();

        ProcessOpener opener = new WindowsProcessOpener(kernel32);
        ProcessOwnerGetter ownerGetter = new WindowsProcessOwnerGetter(advapi32);
        ProcessDescriptionGetter descriptionGetter = new WindowsProcessDescriptionGetter(kernel32);

        processCollector = new WindowsNativeProcessCollector(opener, ownerGetter, descriptionGetter);
        reader = new WindowsMemoryReader(opener, kernel32);

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
            NativeProcess process = processCollector.collect(pid);

            byte[] bytes = reader.read(process, targetAddress, TESTTARGET_STRUCT_SIZE);

            TypeSerializer<Integer> intSerializer = serializers.find(Integer.class);
            TypeSerializer<Boolean> boolSerializer = serializers.find(Boolean.class);

            int readArg1 = intSerializer.deserialize(Arrays.copyOfRange(bytes, 0, 4));
            int readArg2 = intSerializer.deserialize(Arrays.copyOfRange(bytes, 4, 8));
            boolean readArg3 = boolSerializer.deserialize(Arrays.copyOfRange(bytes, 8, 9));
            boolean readArg4 = boolSerializer.deserialize(Arrays.copyOfRange(bytes, 9, 10));
            // short readArg5 = shortSerializer.deserialize(Arrays.copyOfRange(bytes, 10, 12));
            // int arg6ptr = intSerializer.deserialize(Arrays.copyOfRange(bytes, 12, 16));
            // byte[] arg6bytes = reader.read(process, arg6ptr, arg6.length());
            // String readArg6 = stringSerializer.deserialize(arg6bytes);

            assertEquals(arg1, readArg1);
            assertEquals(arg2, readArg2);
            assertEquals(arg3, readArg3);
            assertEquals(arg4, readArg4);
        } finally {
            targetProcess.destroyForcibly();
        }
    }
}
