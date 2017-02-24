package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.ProcessEnumerationException;
import cx.corp.lacuna.core.windows.winapi.MockPsapi;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WindowsPidEnumeratorTest {

    private MockPsapi psapi;
    private WindowsPidEnumerator enumerator;

    @Before
    public void setUp() {
        psapi = new MockPsapi();
        enumerator = new WindowsPidEnumerator(psapi);
    }

    @Test(expected = NullPointerException.class)
    public void constructorThrowsIfNullPsapiPassed() {
        new WindowsPidEnumerator(null);
    }

    @Test(expected = ProcessEnumerationException.class)
    public void getPidsThrowsIfPsapiCallFails() {
        psapi.setEnumProcessesReturnValue(false);
        enumerator.getPids();
    }

    @Test
    public void getPidsReturnsCorrectlySizedArray() {
        int pidsToReturn = 2;
        int[] input = new int[]{123, 321, 5, 19, 0, 0, 0, 0, 0};
        psapi.setEnumProcessesReturnValue(true);
        psapi.setEnumProcessesPids(input);
        psapi.setEnumProcessesBytesReturned(pidsToReturn * WinApiConstants.SIZEOF_INT);
        int[] expectedReturn = Arrays.copyOf(input, pidsToReturn);

        List<Integer> returned = enumerator.getPids();

        int[] returnedAsArray = returned.stream().mapToInt(i -> i).toArray();
        assertArrayEquals(expectedReturn, returnedAsArray);
    }

    @Test
    public void getPidsReturnsNoPidsWhenPsapiSaysNoBytesReturned() {
        psapi.setEnumProcessesReturnValue(true);
        psapi.setEnumProcessesPids(new int[]{123, 321});
        psapi.setEnumProcessesBytesReturned(0);

        int count = enumerator.getPids().size();
        assertEquals(0, count);
    }

    @Test
    public void getPidsReturnsAllPids() {
        psapi.setEnumProcessesReturnValue(true);
        int[] input = IntStream.range(1, 7500).toArray();
        psapi.setEnumProcessesPids(input);
        psapi.setEnumProcessesBytesReturned(input.length * WinApiConstants.SIZEOF_INT);

        int[] pids = enumerator.getPids().stream().mapToInt(i -> i).toArray();
        assertArrayEquals(input, pids);
    }
}
