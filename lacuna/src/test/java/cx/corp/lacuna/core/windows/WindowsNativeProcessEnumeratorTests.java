package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.ProcessEnumerationException;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.MockKernel32;
import cx.corp.lacuna.core.windows.winapi.MockPsapi;
import cx.corp.lacuna.core.windows.winapi.Psapi;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.annotation.Native;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WindowsNativeProcessEnumeratorTests {

    private MockKernel32 kernel;
    private MockPsapi psapi;
    private WindowsNativeProcessEnumerator enumerator;

    @Before
    public void setUp() {
        kernel = new MockKernel32();
        psapi = new MockPsapi();
        enumerator = new WindowsNativeProcessEnumerator(kernel, psapi);
    }

    @Test
    public void enumeratorGetsCorrectAmountOfProcesses() {
        int[] pids = { 0, 1, 23, 502, 5992, 120, 235, 599, 4003 };
        psapi.setPids(pids);

        List<NativeProcess> processes = enumerator.getProcesses();
        assertEquals("Enumerator found the wrong amount of processes!", pids.length, processes.size());
    }

    @Test
    public void enumeratorGetsCorrectPids() {
        int[] pids = { 1, 0, 23, 1337, 555, 901, 391, 553, 6603 };
        psapi.setPids(pids);

        List<NativeProcess> processes = enumerator.getProcesses();
        for (int i = 0; i < pids.length; i++) {
            final int pid = pids[i];
            Optional<NativeProcess> matchingProc =
                    processes.stream().filter(p -> p.getPid() == pid).findAny();
            if (!matchingProc.isPresent()) {
                fail("PID " + pids[i] + " was not found from the generated process list!");
            }
        }
    }

    @Test(expected = ProcessEnumerationException.class)
    public void enumeratorThrowsIfEnumerateProcessesFails() {
        psapi.setEnumProcessesReturnValue(false);
        enumerator.getProcesses();
    }
}
