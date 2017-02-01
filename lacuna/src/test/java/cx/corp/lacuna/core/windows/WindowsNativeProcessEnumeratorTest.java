package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.PidEnumerator;
import cx.corp.lacuna.core.ProcessEnumerationException;
import cx.corp.lacuna.core.TestUtils;
import cx.corp.lacuna.core.windows.winapi.MockKernel32;
import cx.corp.lacuna.core.windows.winapi.MockPsapi;
import org.junit.Before;
import org.junit.Test;

import java.io.IOError;
import java.io.IOException;
import java.lang.annotation.Native;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class WindowsNativeProcessEnumeratorTest {

    private WindowsNativeProcessEnumerator enumerator;
    private PidEnumerator pidEnumerator;
    private NativeProcessCollector collector;

    @Before
    public void setUp() {
        pidEnumerator = () -> Arrays.stream(new int[] {1});
        collector = pid -> new NativeProcess(pid, "description", "owner");
        enumerator = new WindowsNativeProcessEnumerator(pidEnumerator, collector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsWhenPassedNullPidEnumerator() {
        new WindowsNativeProcessEnumerator(null, collector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsWhenPassedNullCollector() {
        new WindowsNativeProcessEnumerator(pidEnumerator, null);
    }

    @Test
    public void returnsEmptyListWhenNoProcessesFound() {
        PidEnumerator pidEnumerator = IntStream::empty;
        enumerator = new WindowsNativeProcessEnumerator(pidEnumerator, collector);

        List<NativeProcess> processes = enumerator.getProcesses();

        assertEquals("Expected enumerator to return zero processes!", 0, processes.size());
    }

    @Test
    public void getsCorrectPids() {
        int[] expectedPids = { 0, 1, 23, 502, 5992, 120, 235, 599, 4003 };
        PidEnumerator pidEnumerator = () -> Arrays.stream(expectedPids);
        enumerator = new WindowsNativeProcessEnumerator(pidEnumerator, collector);

        List<NativeProcess> processes = enumerator.getProcesses();
        int[] pids = processes.stream().mapToInt(NativeProcess::getPid).toArray();

        // whooee O(n^2) * 2
        assertTrue(TestUtils.containsAll(expectedPids, pids));
        assertTrue(TestUtils.containsAll(pids, expectedPids));
    }

    @Test
    public void getsCorrectProcessDescriptions() {
        Map<Integer, String> pidDescriptions = new HashMap<>();
        pidDescriptions.put(123, "toaster");
        pidDescriptions.put(1, null);
        pidDescriptions.put(3848, "ayy");
        IntStream pids = pidDescriptions.keySet().stream().mapToInt(p -> p);
        pidEnumerator = () -> pids;
        collector = pid -> new NativeProcess(pid, pidDescriptions.get(pid), null);
        enumerator = new WindowsNativeProcessEnumerator(pidEnumerator, collector);

        List<NativeProcess> processes = enumerator.getProcesses();
        for (NativeProcess process : processes) {
            int pid = process.getPid();
            String description = process.getDescription();
            assertEquals(pidDescriptions.get(pid), description);
        }
    }

    @Test
    public void getsCorrectProcessOwners() {
        Map<Integer, String> pidOwners = new HashMap<>();
        pidOwners.put(123, "me");
        pidOwners.put(3848, "you");
        pidOwners.put(1, null);
        IntStream pids = pidOwners.keySet().stream().mapToInt(p -> p);
        pidEnumerator = () -> pids;
        collector = pid -> new NativeProcess(pid, null, pidOwners.get(pid));
        enumerator = new WindowsNativeProcessEnumerator(pidEnumerator, collector);


        List<NativeProcess> processes = enumerator.getProcesses();
        for (NativeProcess process : processes) {
            int pid = process.getPid();
            String owner = process.getOwner();
            assertEquals(pidOwners.get(pid), owner);
        }
    }
}
