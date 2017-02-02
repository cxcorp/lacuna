package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.windows.winapi.MockAdvapi32;
import cx.corp.lacuna.core.windows.winapi.MockKernel32;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WinApiNativeProcessCollectorTest {

    private static final int LEGAL_PROCESS_HANDLE = 0x5;
    private static final int LEGAL_PROCESS_TOKEN = 123;

    private MockKernel32 kernel;
    private MockAdvapi32 advapi;
    private WinApiNativeProcessCollector collector;
    private MockProcessOpener processOpener;

    @Before
    public void setUp() {
        kernel = new MockKernel32();
        advapi = new MockAdvapi32();
        processOpener = new MockProcessOpener();
        processOpener.setOpenReturnValue(new MockProcessHandle(LEGAL_PROCESS_HANDLE));
        collector = new WinApiNativeProcessCollector(processOpener, kernel, advapi);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfProcessOpenerIsNull() {
        new WinApiNativeProcessCollector(null, kernel, advapi);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfKernelIsNull() {
        new WinApiNativeProcessCollector(processOpener, null, advapi);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfAdvapiIsNull() {
        new WinApiNativeProcessCollector(processOpener, kernel, null);
    }

    @Test
    public void collectGetsCorrectPidEvenIfProcessCannotBeOpenedForDetails() {
        processOpener.makeOpenThrowException();
        int pid = 555;

        NativeProcess process = collector.collect(pid);
        assertEquals(pid, process.getPid());
    }

    @Test
    public void collectGetsUnknownDescriptionIfProcessCannotBeOpened() {
        processOpener.makeOpenThrowException();
        NativeProcess proc = collector.collect(123);

        assertEquals(NativeProcess.UNKNOWN_DESCRIPTION, proc.getDescription());
    }

    @Test
    public void collectGetsUnknownProcessNameIfImageNameCannotBeQueried() {
        kernel.setQueryFullProcessImageSuccess(false);

        NativeProcess process = collector.collect(123);

        assertEquals(NativeProcess.UNKNOWN_DESCRIPTION, process.getDescription());
    }

    @Test
    public void collectGetsCorrectProcessNameIfImageNameCanBeQueried() {
        String description = "toaster.exe";
        kernel.setOpenProcessReturnValue(123);
        kernel.setQueryFullProcessImageSuccess(true);
        kernel.setQueryFullProcessImageNameExeName(description);

        NativeProcess process = collector.collect(321);

        assertEquals(description, process.getDescription());
    }

    @Test
    public void collectGetsUnknownOwnerIfProcessCannotBeOpened() {
        processOpener.makeOpenThrowException();

        NativeProcess proc = collector.collect(123);

        assertEquals(NativeProcess.UNKNOWN_OWNER, proc.getOwner());
    }
}
