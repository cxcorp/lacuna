package cx.corp.lacuna.core.windows;

import com.sun.jna.Platform;
import cx.corp.lacuna.core.IntegrationTestConstants;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.PidEnumerator;
import cx.corp.lacuna.core.TestTargetLauncher;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;
import cx.corp.lacuna.core.windows.winapi.WinApiBootstrapper;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WindowsNativeProcessEnumeratorIT {

    private final WinApiBootstrapper winapi;
    private Kernel32 kernel32;
    private TestTargetLauncher launcher;
    private WindowsNativeProcessEnumerator enumerator;

    public WindowsNativeProcessEnumeratorIT() {
        winapi = new WinApiBootstrapper();
    }

    @Before
    public void setUp() {
        // Don't run platform specific integration tests on
        // wrong platform
        Assume.assumeTrue(Platform.isWindows());

        Path testTargetPath = IntegrationTestConstants.getTestTargetUrlForWindows();
        launcher = new TestTargetLauncher(testTargetPath);

        kernel32 = winapi.getKernel32();
        Advapi32 advapi32 = winapi.getAdvapi32();
        Psapi psapi = winapi.getPsapi();
        PidEnumerator pidEnumerator = new WinApiPidEnumerator(psapi);
        NativeProcessCollector collector = new WinApiNativeProcessCollector(kernel32, advapi32);
        enumerator = new WindowsNativeProcessEnumerator(pidEnumerator, collector);
    }

    @Test
    public void getProcessesFindsCorrectTestTargetPid() throws Exception {
        List<NativeProcess> processesBefore = enumerator.getProcesses();

        ProcessBuilder builder = launcher.createBuilder();
        Process process = builder.start();

        try {
            int actualPid = getPid(process);

            List<NativeProcess> list = enumerator.getProcesses();
            list.removeAll(processesBefore);

            Optional<NativeProcess> result = list
                .stream()
                .filter(p -> p.getPid() == actualPid)
                .findFirst();

            assertTrue(result.isPresent());
            assertEquals(actualPid, result.get().getPid());
        } finally {
            process.destroyForcibly();
        }
    }

    @Test
    public void getProcessesContainsTestTargetDescription() throws Exception {
        List<NativeProcess> processesBefore = enumerator.getProcesses();

        ProcessBuilder builder = launcher.createBuilder();
        Process process = builder.start();

        try {
            List<NativeProcess> list = enumerator.getProcesses();
            list.removeAll(processesBefore);

            Optional<NativeProcess> result = list
                .stream()
                .filter(p -> p.getDescription().contains(launcher.getExecutableName()))
                .findFirst();

            assertTrue(result.isPresent());
            assertTrue(result.get().getDescription().contains(launcher.getExecutableName()));
        } finally {
            process.destroyForcibly();
        }
    }

    /**
     * Uses reflection to get the private handle id if this is indeed a Windows platform!
     * Tested on JDK 8!
     */
    private int getPid(Process process) throws Exception {
        Class<? extends Process> clazz = process.getClass();
        Field handleField = clazz.getDeclaredField("handle");
        handleField.setAccessible(true);
        long handle = (Long) handleField.get(process);
        handleField.setAccessible(false);
        return kernel32.getProcessId((int) handle);
    }
}
