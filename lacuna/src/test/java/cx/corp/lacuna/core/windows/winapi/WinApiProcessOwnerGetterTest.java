package cx.corp.lacuna.core.windows.winapi;

import cx.corp.lacuna.core.windows.MockProcessHandle;
import cx.corp.lacuna.core.windows.winapi.MockAdvapi32;
import cx.corp.lacuna.core.windows.winapi.WinApiProcessOwnerGetter;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertFalse;

public class WinApiProcessOwnerGetterTest {

    private MockAdvapi32 advapi;
    private WinApiProcessOwnerGetter getter;
    private MockProcessHandle handle;

    @Before
    public void setUp() {
        advapi = new MockAdvapi32();
        getter = new WinApiProcessOwnerGetter(advapi);
        handle = new MockProcessHandle(123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfNullAdvapiPassed() {
        new WinApiProcessOwnerGetter(null);
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
