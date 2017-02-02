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

    @Before
    public void setUp() {
        kernel = new MockKernel32();
        advapi = new MockAdvapi32();
        collector = new WinApiNativeProcessCollector(kernel, advapi);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfKernelIsNull() {
        new WinApiNativeProcessCollector(null, advapi);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfAdvapiIsNull() {
        new WinApiNativeProcessCollector(kernel, null);
    }

    @Test
    public void collectGetsCorrectPidEvenIfDetailsFail() {
        kernel.setQueryFullProcessImageSuccess(false);
        advapi.setOpenProcessTokenReturnValue(false);
        int pid = 555;

        NativeProcess process = collector.collect(pid);
        assertEquals(pid, process.getPid());
    }

    @Test
    public void collectGetsUnknownDescriptionIfProcessCannotBeOpened() {
        kernel.setOpenProcessReturnValue(WinApiConstants.NULLPTR);
        // make QueryFullProcessImageName succeed, it shouldn't even be called
        // if the process cannot be opened!
        kernel.setQueryFullProcessImageSuccess(true);
        kernel.setQueryFullProcessImageNameExeName("shouldntgetme.exe");

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
        kernel.setOpenProcessReturnValue(WinApiConstants.NULLPTR);
        // make QueryFullProcessImageName succeed, it shouldn't even be called
        // if the process cannot be opened!
        advapi.setOpenProcessTokenReturnValue(true);
        advapi.setOpenProcessTokenTokenHandle(LEGAL_PROCESS_TOKEN);


        NativeProcess proc = collector.collect(123);

        assertEquals(NativeProcess.UNKNOWN_DESCRIPTION, proc.getDescription());
    }
}
