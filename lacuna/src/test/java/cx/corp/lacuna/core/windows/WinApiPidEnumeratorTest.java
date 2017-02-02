package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.ProcessEnumerationException;
import cx.corp.lacuna.core.windows.WinApiPidEnumerator;
import cx.corp.lacuna.core.windows.winapi.MockPsapi;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WinApiPidEnumeratorTest {

    private MockPsapi psapi;
    private WinApiPidEnumerator enumerator;

    @Before
    public void setUp() {
        psapi = new MockPsapi();
        enumerator = new WinApiPidEnumerator(psapi);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfNullPsapiPassed() {
        new WinApiPidEnumerator(null);
    }

    @Test(expected = ProcessEnumerationException.class)
    public void getPidsThrowsIfPsapiCallFails() {
        psapi.setEnumProcessesReturnValue(false);
        enumerator.getPids();
    }

    @Test
    public void getPidsReturnsCorrectlySizedArray() {
        psapi.setEnumProcessesReturnValue(true);
        int[] input = new int[] {123, 321, 5, 19, 0, 0, 0, 0, 0};
        psapi.setEnumProcessesPids(input);
        int pidsToReturn = 2;
        psapi.setEnumProcessesBytesReturned(pidsToReturn * WinApiConstants.SIZEOF_INT);
        int[] expectedReturn = Arrays.copyOf(input, pidsToReturn);

        int[] returned = enumerator.getPids().toArray();
        assertArrayEquals(expectedReturn, returned);
    }

    @Test
    public void getPidsReturnsNoPidsWhenPsapiSaysNoBytesReturned() {
        psapi.setEnumProcessesReturnValue(true);
        psapi.setEnumProcessesPids(new int[] {123, 321});
        psapi.setEnumProcessesBytesReturned(0);

        long count = enumerator.getPids().count();
        assertEquals(0, count);
    }

    @Test
    public void getPidsReturnsAllPids() {
        psapi.setEnumProcessesReturnValue(true);
        int[] input = IntStream.range(1, 7500).toArray();
        psapi.setEnumProcessesPids(input);
        psapi.setEnumProcessesBytesReturned(input.length * WinApiConstants.SIZEOF_INT);

        int[] pids = enumerator.getPids().toArray();
        assertArrayEquals(input, pids);
    }
}
