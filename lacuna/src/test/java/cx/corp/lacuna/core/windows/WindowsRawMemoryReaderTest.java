package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.MemoryReaderImpl;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.windows.winapi.MockKernel32;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WindowsRawMemoryReaderTest {

    private ProcessOpener processOpener;
    private MockKernel32 kernel;
    private WindowsRawMemoryReader reader;
    private NativeProcess process;

    @Before
    public void setUp() {
        kernel = new MockKernel32();
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        // create another lambda so that we can change processOpener in the tests and still maintain the reference
        ProcessOpener proxyOpener = (pid, processAccessFlags) -> processOpener.open(pid, processAccessFlags);
        reader = new WindowsRawMemoryReader(proxyOpener, kernel);
        process = new NativeProcessImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfNullProcessOpenerPassed() {
        new WindowsRawMemoryReader(null, kernel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfNullKernelPassed() {
        new WindowsRawMemoryReader(processOpener, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsIfProcessIsNull() {
        reader.read(null, 0, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsIfOffsetIsNegative() {
        reader.read(process, -100, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsIfReadNegativeBytes() {
        reader.read(process, 100, -100);
    }

    @Test(expected = ProcessOpenException.class)
    public void readThrowsIfProcessCannotBeOpened() {
        processOpener = (pid, flags) -> {
            throw new ProcessOpenException("fail");
        };
        process.setPid(1234);

        reader.read(process, 0, 123);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfReadingFails() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(321);
        kernel.setReadProcessMemoryReturnValue(false);
        // emulate readProcessBytes fail due to unreadable memory address, e.g. out of bounds
        Native.setLastError(SystemErrorCode.PARTIAL_COPY.getSystemErrorId());

        reader.read(process, 0, 16);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfUnexpectedSystemErrorOccurs() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(456);
        // class will check system error code only if set to false
        kernel.setReadProcessMemoryReturnValue(false);
        int unexpectedSystemError = -1; // literally will never be set to -1 by WinApi
        Native.setLastError(unexpectedSystemError);

        reader.read(process, 0, 16);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsWhenReadingZeroBytes() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(321);
        kernel.setReadProcessMemoryReturnValue(true);
        byte[] memoryBytes = new byte[]{123, -127, 42, 0, 1, 0, 0, 45};
        kernel.setReadProcessReadMemory(memoryBytes);

        reader.read(process, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsWhenReadingNegativeAmountOfBytes() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(321);
        kernel.setReadProcessMemoryReturnValue(true);
        byte[] memoryBytes = new byte[]{123, -127, 42, 0, 1, 0, 0, 45};
        kernel.setReadProcessReadMemory(memoryBytes);

        reader.read(process, 0, -123);
    }

    @Test
    public void readReadsCorrectByteArray() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(321);
        kernel.setReadProcessMemoryReturnValue(true);
        byte[] memoryBytes = new byte[]{123, -127, 42, 0, 1, 0, 0, 45};
        kernel.setReadProcessReadMemory(memoryBytes);

        ByteBuffer readBuffer = reader.read(process, 0, memoryBytes.length);
        byte[] readBytes = new byte[readBuffer.remaining()];
        readBuffer.get(readBytes);

        assertArrayEquals(memoryBytes, readBytes);
    }
}
