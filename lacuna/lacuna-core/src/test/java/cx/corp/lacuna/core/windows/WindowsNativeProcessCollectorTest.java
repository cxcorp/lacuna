package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.domain.NativeProcess;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class WindowsNativeProcessCollectorTest {

    private static final int LEGAL_PROCESS_HANDLE = 0x5;
    private static final int LEGAL_PROCESS_TOKEN = 123;

    private WindowsNativeProcessCollector collector;
    private MockProcessOpener processOpener;
    private ProcessOwnerGetter ownerGetter;
    private ProcessDescriptionGetter descriptionGetter;

    @Before
    public void setUp() {
        processOpener = new MockProcessOpener();
        processOpener.setOpenReturnValue(new MockProcessHandle(LEGAL_PROCESS_HANDLE));
        processOpener.doNotThrowExceptionOnOpen();
        // proxy the handle via another lambda so unit tests can just change the ownerGetter
        // field instead of creating new instances of WindowsNativeProcessCollector
        ownerGetter = handle -> Optional.empty();
        descriptionGetter = handle -> Optional.empty();
        ProcessOwnerGetter ownerProxy = handle -> ownerGetter.get(handle);
        ProcessDescriptionGetter descriptionProxy = handle -> descriptionGetter.get(handle);
        collector = new WindowsNativeProcessCollector(processOpener, ownerProxy, descriptionProxy);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfProcessOpenerIsNull() {
        new WindowsNativeProcessCollector(null, ownerGetter, descriptionGetter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfOwnerGetterIsNull() {
        new WindowsNativeProcessCollector(processOpener, null, descriptionGetter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfDescriptionGetterIsNull() {
        new WindowsNativeProcessCollector(processOpener, ownerGetter, null);
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
    public void collectGetsUnknownDescriptionIfDescriptionGetterReturnsEmpty() {
        processOpener.doNotThrowExceptionOnOpen();
        descriptionGetter = handle -> Optional.empty();

        NativeProcess process = collector.collect(123);

        assertEquals(NativeProcess.UNKNOWN_DESCRIPTION, process.getDescription());
    }

    @Test
    public void collectGetsCorrectDescriptionFromDescriptionGetter() {
        processOpener.doNotThrowExceptionOnOpen();
        String description = "toaster.exe";
        descriptionGetter = handle -> Optional.of(description);

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
        processOpener.doNotThrowExceptionOnOpen();
        final String ownerName = "Lacuna";
        ownerGetter = handle -> Optional.of(ownerName);

        NativeProcess proc = collector.collect(321);

        assertEquals(ownerName, proc.getOwner());
    }

    @Test
    public void collectGetsUnknownOwnerIfOwnerGetterReturnsEmpty() {
        processOpener.doNotThrowExceptionOnOpen();
        ownerGetter = handle -> Optional.empty();

        NativeProcess proc = collector.collect(444);

        assertEquals(NativeProcess.UNKNOWN_OWNER, proc.getOwner());
    }
}
