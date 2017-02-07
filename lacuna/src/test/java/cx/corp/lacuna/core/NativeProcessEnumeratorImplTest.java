package cx.corp.lacuna.core;

import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.NativeProcessEnumeratorImpl;
import cx.corp.lacuna.core.PidEnumerator;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class NativeProcessEnumeratorImplTest {

    private NativeProcessEnumeratorImpl enumerator;
    private PidEnumerator pidEnumerator;
    private NativeProcessCollector collector;

    @Before
    public void setUp() {
        // create proxy pidenumerator & collector to use in the ctor so we can change this.pidEnumerator
        // and this.collector in the tests without having to reconstruct the enumerator instance
        this.pidEnumerator = ArrayList::new;
        PidEnumerator proxyEnumerator = () -> this.pidEnumerator.getPids();
        this.collector = pid -> new NativeProcessImpl(pid, "description", "owner");
        NativeProcessCollector proxyCollector = pid -> this.collector.collect(pid);

        this.enumerator = new NativeProcessEnumeratorImpl(proxyEnumerator, proxyCollector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsWhenPassedNullPidEnumerator() {
        new NativeProcessEnumeratorImpl(null, collector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsWhenPassedNullCollector() {
        new NativeProcessEnumeratorImpl(pidEnumerator, null);
    }

    @Test
    public void returnsEmptyListWhenNoProcessesFound() {
        pidEnumerator = ArrayList::new;
        List<NativeProcess> processes = enumerator.getProcesses();

        assertEquals("Expected enumerator to return zero processes!", 0, processes.size());
    }

    @Test
    public void getsCorrectPids() {
        List<Integer> expectedPids = Arrays.asList(0, 1, 23, 502, 5992, 120, 235, 599, 4003);
        // pass `new` list so that `enumerator` can't break the test by mutating expectedPids
        pidEnumerator = () -> new ArrayList<>(expectedPids);

        List<NativeProcess> processes = enumerator.getProcesses();
        List<Integer> pids = processes.stream().map(NativeProcess::getPid).collect(Collectors.toList());

        assertTrue(expectedPids.containsAll(pids));
        assertTrue(pids.containsAll(expectedPids));
    }

    @Test
    public void getsCorrectProcessDescriptions() {
        Map<Integer, String> pidDescriptions = new HashMap<>();
        pidDescriptions.put(123, "toaster");
        pidDescriptions.put(1, null);
        pidDescriptions.put(3848, "ayy");

        List<Integer> expectedPids = new ArrayList<>(pidDescriptions.keySet());
        // pass `new` list so that `enumerator` can't break the test by mutating expectedPids
        pidEnumerator = () -> new ArrayList<>(expectedPids);
        collector = pid -> new NativeProcessImpl(pid, pidDescriptions.get(pid), null);

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

        List<Integer> expectedPids = new ArrayList<>(pidOwners.keySet());
        pidEnumerator = () -> new ArrayList<>(expectedPids);
        collector = pid -> new NativeProcessImpl(pid, null, pidOwners.get(pid));

        List<NativeProcess> processes = enumerator.getProcesses();

        for (NativeProcess process : processes) {
            int pid = process.getPid();
            String owner = process.getOwner();
            assertEquals(pidOwners.get(pid), owner);
        }
    }
}
