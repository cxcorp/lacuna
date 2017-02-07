package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.windows.winapi.QueryFullProcessImageName;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WindowsProcessDescriptionGetterTest {

    private QueryFullProcessImageName nameQuerier;
    private WindowsProcessDescriptionGetter getter;
    private MockProcessHandle handle;

    @Before
    public void setUp() {
        nameQuerier = null;
        // capture local field via closure so tests can modify it
        getter = new WindowsProcessDescriptionGetter((a, b, c, d) -> nameQuerier.queryFullProcessImageNameW(a, b, c, d));
        handle = new MockProcessHandle(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsWhenPassedNullKernel() {
        new WindowsProcessDescriptionGetter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getThrowsWhenPassedNullHandle() {
        getter.get(null);
    }

    @Test
    public void getReturnsEmptyIfKernelQueryFails() {
        nameQuerier = (a, b, c, d) -> false;

        Optional<String> result = getter.get(handle);

        assertFalse(result.isPresent());
    }

    @Test
    public void getReturnsEmptyStringIfKernelSaysItWroteZeroBytes() {
        nameQuerier = (procHandle, flags, exeName, bufSize) -> {
            char[] name = "Image name that isn not empty.exe".toCharArray();
            // copy the value to the receiving array
            System.arraycopy(name, 0, exeName, 0, name.length);
            // but claim that zero bytes were copied
            bufSize.setValue(0);
            return true;
        };

        Optional<String> result = getter.get(handle);

        assertEquals(0, result.get().length());
    }

    @Test
    public void getReturnsCorrectlyTrimmedString() {
        final String description = "toasters.exe";
        final int howManyBytesWeClaimToHaveCopied = description.length() - 1;
        final String expected = description.substring(0, howManyBytesWeClaimToHaveCopied);

        nameQuerier = (procHandle, flags, exeName, bufSize) -> {
            char[] name = description.toCharArray();
            System.arraycopy(name, 0, exeName, 0, howManyBytesWeClaimToHaveCopied);
            bufSize.setValue(howManyBytesWeClaimToHaveCopied);
            return true;
        };

        Optional<String> result = getter.get(handle);

        assertEquals(expected, result.get());
    }
}
