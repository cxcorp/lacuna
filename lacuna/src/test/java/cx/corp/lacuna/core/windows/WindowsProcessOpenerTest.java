package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.windows.winapi.CloseHandle;
import cx.corp.lacuna.core.windows.winapi.OpenProcess;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WindowsProcessOpenerTest {

    private OpenProcess openProcess;
    private CloseHandle closeHandle;
    private WindowsProcessOpener opener;

    @Before
    public void setUp() {
        openProcess = null;
        closeHandle = null;
        OpenProcess proxyOpener = (flags, inheritHandle, pid) -> openProcess.openProcess(flags, inheritHandle, pid);
        CloseHandle proxyCloser = handle -> closeHandle.closeHandle(handle);
        opener = new WindowsProcessOpener(proxyOpener, proxyCloser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unifiedConstructorThrowsIfArgumentIsNull() {
        new WindowsProcessOpener(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfOpenProcessIsNull() {
        new WindowsProcessOpener(null, arg -> true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfCloseHandleIsNull() {
        new WindowsProcessOpener((a, b, c) -> 0, null);
    }

    @Test(expected = ProcessOpenException.class)
    public void openThrowsIfProcessCannotBeOpened() throws Exception {
        // Kernel returns NULL if opening the process fails
        openProcess = (flags, inherit, pid) -> WinApiConstants.NULL;
        opener.open(123, ProcessAccessFlags.QUERY_INFORMATION);
    }

    @Test
    public void openOpensHandleCorrectly() throws Exception {
        int nativeHandle = 5192;
        openProcess = (flags, inherit, pid) -> nativeHandle;

        ProcessHandle handle = opener.open(111, ProcessAccessFlags.VIRTUAL_MEMORY_READ);

        assertEquals(nativeHandle, handle.getNativeHandle());
    }

    @Test
    public void openedProcessHandleCloseClosesHandle() throws Exception {
        int nativeHandle = 999;
        openProcess = (flags, inherit, pid) -> nativeHandle;
        List<Integer> closedHandles = new ArrayList<>();
        closeHandle = closedHandles::add;

        ProcessHandle handle = opener.open(123, ProcessAccessFlags.ALL);
        handle.close();

        assertTrue(
            "ProcessHandle didn't close the process handle!",
            closedHandles.contains(nativeHandle));
    }

    @Test
    public void openedProcessHandleClosesItselfAfterTryWithResourcesBlock() throws Exception {
        int nativeHandle = 123;
        openProcess = (flags, inherit, pid) -> nativeHandle;
        List<Integer> closedHandles = new ArrayList<>();
        closeHandle = closedHandles::add;

        try (ProcessHandle handle = opener.open(1555, ProcessAccessFlags.ALL)) {
            assertEquals(nativeHandle, handle.getNativeHandle());
        }

        assertTrue(
            "ProcessHandle didn't close after the try-with-resources block!",
            closedHandles.contains(nativeHandle));
    }
}
