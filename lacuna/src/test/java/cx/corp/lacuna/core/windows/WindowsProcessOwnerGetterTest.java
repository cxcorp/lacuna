package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.windows.winapi.MockAdvapi32;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertFalse;

public class WindowsProcessOwnerGetterTest {

    private MockAdvapi32 advapi;
    private WindowsProcessOwnerGetter getter;
    private MockProcessHandle handle;

    @Before
    public void setUp() {
        advapi = new MockAdvapi32();
        getter = new WindowsProcessOwnerGetter(advapi);
        handle = new MockProcessHandle(123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfNullAdvapiPassed() {
        new WindowsProcessOwnerGetter(null);
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
}
