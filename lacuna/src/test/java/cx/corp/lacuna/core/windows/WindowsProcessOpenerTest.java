package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.windows.ProcessHandle;
import cx.corp.lacuna.core.windows.ProcessOpenException;
import cx.corp.lacuna.core.windows.WindowsProcessOpener;
import cx.corp.lacuna.core.windows.winapi.MockKernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WindowsProcessOpenerTest {

    private MockKernel32 kernel;
    private WindowsProcessOpener opener;

    @Before
    public void setUp() {
        kernel = new MockKernel32();
        opener = new WindowsProcessOpener(kernel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfKernelIsNull() {
        new WindowsProcessOpener(null);
    }

    @Test(expected = ProcessOpenException.class)
    public void openThrowsIfKernelCannotOpenProcess() throws Exception {
        kernel.setOpenProcessReturnValue(WinApiConstants.NULLPTR);
        opener.open(123, ProcessAccessFlags.QUERY_INFORMATION);
    }

    @Test
    public void openOpensHandleCorrectly() throws Exception {
        int nativeHandle = 5192;
        kernel.setOpenProcessReturnValue(nativeHandle);

        ProcessHandle handle = opener.open(111, ProcessAccessFlags.VIRTUAL_MEMORY_READ);

        assertEquals(nativeHandle, handle.getNativeHandle());
    }

    @Test
    public void openedProcessHandleCloseClosesHandle() throws Exception {
        int nativeHandle = 999;
        kernel.setOpenProcessReturnValue(nativeHandle);

        ProcessHandle handle = opener.open(123, ProcessAccessFlags.ALL);
        handle.close();

        assertTrue(
            "ProcessHandle didn't close the handle via the kernel!",
            kernel.getClosedHandles().contains(nativeHandle));
    }

    @Test
    public void openedProcessHandleClosesItselfAfterTryWithResourcesBlock() throws Exception {
        int nativeHandle = 123;
        kernel.setOpenProcessReturnValue(nativeHandle);

        try (ProcessHandle handle = opener.open(1555, ProcessAccessFlags.ALL)) {
            assertEquals(nativeHandle, handle.getNativeHandle());
        }

        assertTrue(
            "ProcessHandle didn't close after the try-with-resources block!",
            kernel.getClosedHandles().contains(nativeHandle));
    }
}
