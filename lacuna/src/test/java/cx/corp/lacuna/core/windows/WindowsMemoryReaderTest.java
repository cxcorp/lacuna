package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.ProcessAccessException;
import cx.corp.lacuna.core.windows.winapi.MockKernel32;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;
import org.junit.Before;
import org.junit.Test;

public class WindowsMemoryReaderTest {

    private MockKernel32 kernel;
    private WindowsMemoryReader reader;
    private NativeProcess process;

    @Before
    public void setUp() {
        kernel = new MockKernel32();
        reader = new WindowsMemoryReader(kernel);
        process = new NativeProcess();
    }

    @Test(expected = ProcessAccessException.class)
    public void readThrowsIfProcessCannotBeOpened() {
        // WinAPI docs state that OpenProcess returns NULL if it fails
        kernel.setOpenProcessReturnValue(WinApiConstants.NULLPTR);
        process.setPid(1234);

        reader.read(process, 0, 123);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfReadingFails() {
        int notNullHandle = 123;
        kernel.setOpenProcessReturnValue(notNullHandle);
        process.setPid(321);
        kernel.setReadProcessMemoryReturnValue(false);

        reader.read(process, 0, 16);
    }
}
