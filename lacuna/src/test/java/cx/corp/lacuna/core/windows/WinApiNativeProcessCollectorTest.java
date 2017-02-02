package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.windows.winapi.MockKernel32;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Native;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class WinApiNativeProcessCollectorTest {

    private static final int LEGAL_PROCESS_HANDLE = 0x5;
    private static final int LEGAL_PROCESS_TOKEN = 123;

    private MockKernel32 kernel;
    private WinApiNativeProcessCollector collector;
    private MockProcessOpener processOpener;
    private ProcessOwnerGetter ownerGetter;

    @Before
    public void setUp() {
        kernel = new MockKernel32();
        processOpener = new MockProcessOpener();
        processOpener.setOpenReturnValue(new MockProcessHandle(LEGAL_PROCESS_HANDLE));
        processOpener.doNotThrowExceptionOnOpen();
        // proxy the handle via another lambda so unit tests can just change the ownerGetter
        // field instead of creating new instances of WinApiNativeProcessCollector
        ownerGetter = handle -> Optional.of("owner");
        ProcessOwnerGetter ownerProxy = handle -> ownerGetter.get(handle);
        collector = new WinApiNativeProcessCollector(processOpener, ownerProxy, kernel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfProcessOpenerIsNull() {
        new WinApiNativeProcessCollector(null, ownerGetter, kernel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfOwnerGetterIsNull() {
        new WinApiNativeProcessCollector(processOpener, null, kernel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfKernelIsNull() {
        new WinApiNativeProcessCollector(processOpener, ownerGetter, null);
    }

    @Test
    public void collectGetsCorrectPidEvenIfProcessCannotBeOpenedForDetails() {
        processOpener.throwExceptionOnOpen();
        int pid = 555;

        NativeProcess process = collector.collect(pid);
        assertEquals(pid, process.getPid());
    }

    @Test
    public void collectGetsUnknownDescriptionIfProcessCannotBeOpened() {
        processOpener.throwExceptionOnOpen();
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
        processOpener.throwExceptionOnOpen();

        NativeProcess proc = collector.collect(123);

        assertEquals(NativeProcess.UNKNOWN_OWNER, proc.getOwner());
    }

    @Test
    public void collectGetsOwnerFromOwnerGetterCorrectly() {
        final String ownerName = "Lacuna";
        ownerGetter = handle -> Optional.of(ownerName);

        NativeProcess proc = collector.collect(321);

        assertEquals(ownerName, proc.getOwner());
    }

    @Test
    public void collectGetsUnknownOwnerIfProcessCanBeOpenedButOwnerGetterFails() {
        processOpener.doNotThrowExceptionOnOpen();
        ownerGetter = handle -> Optional.empty();

        NativeProcess proc = collector.collect(444);

        assertEquals(NativeProcess.UNKNOWN_OWNER, proc.getOwner());
    }
}
