package cx.corp.lacuna.core.linux;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.concurrent.ThreadLocalRandom;

@RunWith(Parameterized.class)
public class ProcPathFilterTest {

    @Parameterized.Parameters(name = "{index}: pid_max = {0}")
    public static Object[] data() {
        return new Object[] {
            10,
            LinuxConstants.FALLBACK_PID_MAX,
            1 << 22 // pid_max can be as high as PID_MAX_LIMIT (2^22)
        };
    }

    @Parameterized.Parameter
    public int pidMax;

    private ProcPathFilter procPathFilter;

    @Before
    public void setUp() {
        pidMax = LinuxConstants.FALLBACK_PID_MAX;
        procPathFilter = new ProcPathFilter(pidMax);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfPassingPidMaxLowerThanLowestLegalPid() {
        new ProcPathFilter(LinuxConstants.LOWEST_LEGAL_PID - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfPassingPidMaxEqualToLowestLegalPid() {
        new ProcPathFilter(LinuxConstants.LOWEST_LEGAL_PID);
    }

    @Test
    public void declinesNonDirectoryFiles() {
        String procName = generateRandomValidProcName();
        boolean result = procPathFilter.accept(false, procName);
        assertFalse("Expected filter to decline non-directory named " + procName, result);
    }

    @Test
    public void declinesDirectoriesWithNonIntegerName() {
        String[] illegalNames = {
            "/proc/sys/kernel",
            "/",
            "..",
            "-ölok",
            "1241abc",
            "asb5123",
            "XIV",
            "24F2A"
        };

        for (String illegalName : illegalNames) {
            boolean result = procPathFilter.accept(true, illegalName);
            assertFalse("Expected filter to decline file \"" + illegalName + "\"", result);
        }
    }

    @Test
    public void declinesDirectoriesWithNamesLowerThanLowestLegalPid() {
        int[] illegalPids = {
            Short.MIN_VALUE,
            -1,
            0
        };

        for (Integer illegalPid : illegalPids) {
            boolean result = procPathFilter.accept(true, illegalPid.toString());
            assertFalse(result);
        }
    }

    @Test
    public void declinesDirectoriesWithNumbersEqualToPidMax() {
        boolean result = procPathFilter.accept(true, Integer.toString(pidMax));
        assertFalse(result);
    }

    @Test
    public void declinesDirectoriesWithNumbersGreaterThanPidMax() {
        String[] illegalNames = {
            (pidMax + 1) + "",
            (Integer.MAX_VALUE) + "",
            "1581293129481285417512948151274129849812"
        };

        for (String illegalName : illegalNames) {
            boolean result = procPathFilter.accept(true, illegalName);
            assertFalse(result);
        }
    }

    @Test
    public void acceptsDirectoryWithLowerBoundIntegerName() {
        boolean result = procPathFilter.accept(true, Integer.toString(LinuxConstants.LOWEST_LEGAL_PID));
        assertTrue(result);
    }

    @Test
    public void acceptsDirectoriesWithLegalIntegerName() {
        String procName = generateRandomValidProcName();
        boolean result = procPathFilter.accept(true, procName);
        assertTrue("Expected name " + procName + " to be accepted!", result);
    }

    private String generateRandomValidProcName() {
        return Integer.toString(ThreadLocalRandom.current().nextInt(1, pidMax));
    }
}