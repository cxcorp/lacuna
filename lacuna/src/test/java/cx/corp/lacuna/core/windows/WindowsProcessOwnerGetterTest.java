package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.windows.winapi.CloseHandle;
import cx.corp.lacuna.core.windows.winapi.MockAdvapi32;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WindowsProcessOwnerGetterTest {

    private MockAdvapi32 advapi;
    private CloseHandle handleCloser;
    private WindowsProcessOwnerGetter getter;
    private MockProcessHandle handle;

    @Before
    public void setUp() {
        advapi = new MockAdvapi32();
        handleCloser = null;
        // capture handleCloser in closure so we can change it
        getter = new WindowsProcessOwnerGetter(advapi, handle -> handleCloser.closeHandle(handle));
        handle = new MockProcessHandle(123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfNullAdvapiPassed() {
        CloseHandle nonNullHandleCloser = handle -> true;
        new WindowsProcessOwnerGetter(null, nonNullHandleCloser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfNullHandleCloserPassed() {
        new WindowsProcessOwnerGetter(advapi, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getThrowsIfPassedNullProcessHandle() {
        getter.get(null);
    }

    @Test
    public void getReturnsEmptyIfItCannotOpenProcessToken() {
        advapi.setOpenProcessTokenReturnValue(false);

        handle.setNativeHandle(123);
        Optional<String> ret =  getter.get(handle);

        assertFalse(ret.isPresent());
    }

    @Test
    public void tokenIsFreedEvenIfOtherCallsFail() {
        int token = 321;
        advapi.setOpenProcessTokenReturnValue(true);
        advapi.setOpenProcessTokenTokenHandle(token);
        ArrayList<Integer> closedHandles = new ArrayList<>();
        handleCloser = closedHandles::add;

        handle.setNativeHandle(4512);
        Optional<String> ret = getter.get(handle);

        assertTrue(closedHandles.contains(token));
    }
}
