package cx.corp.lacuna.core.windows.winapi;

import cx.corp.lacuna.core.windows.MockProcessHandle;
import cx.corp.lacuna.core.windows.winapi.MockKernel32;
import cx.corp.lacuna.core.windows.winapi.WinApiProcessDescriptionGetter;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WinApiProcessDescriptionGetterTest {

    private MockKernel32 kernel;
    private WinApiProcessDescriptionGetter getter;
    private MockProcessHandle handle;

    @Before
    public void setUp() {
        kernel = new MockKernel32();
        getter = new WinApiProcessDescriptionGetter(kernel);
        handle = new MockProcessHandle(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsWhenPassedNullKernel() {
        new WinApiProcessDescriptionGetter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getThrowsWhenPassedNullHandle() {
        getter.get(null);
    }

    @Test
    public void getReturnsEmptyIfKernelQueryFails() {
        kernel.setQueryFullProcessImageSuccess(false);

        Optional<String> result = getter.get(handle);

        assertFalse(result.isPresent());
    }

    @Test
    public void getReturnsEmptyStringIfKernelSaysItWroteZeroBytes() {
        kernel.setQueryFullProcessImageSuccess(true);
        kernel.setQueryFullProcessImageNameExeName("not zero length");
        kernel.setQueryFullProcessImageNameBytesWritten(0);

        Optional<String> result = getter.get(handle);

        assertEquals(0, result.get().length());
    }

    @Test
    public void getReturnsCorrectlyTrimmedString() {
        kernel.setQueryFullProcessImageSuccess(true);
        String description = "toasters.exe";
        kernel.setQueryFullProcessImageNameExeName(description);
        kernel.setQueryFullProcessImageNameBytesWritten(description.length());

        Optional<String> result = getter.get(handle);

        assertEquals(description, result.get());
    }
}
