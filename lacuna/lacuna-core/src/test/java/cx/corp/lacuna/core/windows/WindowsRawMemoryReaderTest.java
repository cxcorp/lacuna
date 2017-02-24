package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.ProcessOpenException;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.windows.winapi.ReadProcessMemory;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;

public class WindowsRawMemoryReaderTest {

    private ProcessOpener processOpener;
    private ReadProcessMemory apiMemoryReader;
    private WindowsRawMemoryReader reader;
    private NativeProcess process;

    @Before
    public void setUp() {
        processOpener = null;
        apiMemoryReader = null;
        // create another lambda so that we can change the fields in the tests and still maintain the reference
        ProcessOpener proxyOpener = (pid, processAccessFlags) -> processOpener.open(pid, processAccessFlags);
        ReadProcessMemory proxyReader = (handle, addr, buff, size, read) -> apiMemoryReader.readProcessMemory(handle, addr, buff, size, read);
        reader = new WindowsRawMemoryReader(proxyOpener, proxyReader);
        process = new NativeProcessImpl();
    }

    @Test(expected = NullPointerException.class)
    public void constructorThrowsIfNullProcessOpenerPassed() {
        apiMemoryReader = (a, b, c, d, e) -> true;
        new WindowsRawMemoryReader(null, apiMemoryReader);
    }

    @Test(expected = NullPointerException.class)
    public void constructorThrowsIfNullKernelPassed() {
        new WindowsRawMemoryReader(processOpener, null);
    }

    @Test(expected = NullPointerException.class)
    public void readThrowsIfProcessIsNull() {
        reader.read(null, 0, 10);
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

    @Test(expected = MemoryAccessException.class)
    public void readThrowsIfReadingFails() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(321);
        apiMemoryReader = (handle, addr, buf, len, read) -> false; // reading fails
        // emulate readProcessBytes fail due to unreadable memory address, e.g. out of bounds
        Native.setLastError(SystemErrorCode.PARTIAL_COPY.getSystemErrorId());

        reader.read(process, 0, 16);
    }

    @Test(expected = MemoryAccessException.class)
    public void readThrowsIfUnexpectedSystemErrorOccurs() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(456);
        // class will check system error code only if win api returns false
        apiMemoryReader = (handle, addr, buf, len, read) -> false;
        int unexpectedSystemError = -1; // literally will never be set to -1 by WinApi
        Native.setLastError(unexpectedSystemError);

        reader.read(process, 0, 16);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsWhenReadingZeroBytes() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(321);
        apiMemoryReader = (handle, addr, buf, len, read) -> true;
        reader.read(process, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsWhenReadingNegativeAmountOfBytes() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(321);
        apiMemoryReader = (handle, addr, buf, len, read) -> true;

        reader.read(process, 0, -123);
    }

    @Test
    public void readReadsCorrectByteArray() {
        processOpener = (pid, flags) -> new MockProcessHandle(123);
        process.setPid(321);
        byte[] memoryBytes = new byte[]{123, -127, 42, 0, 1, 0, 0, 45};
        apiMemoryReader = (handle, address, buffer, bufferLength, readBytes) -> {
            buffer.write(0, memoryBytes, 0, memoryBytes.length);
            readBytes.setValue(memoryBytes.length);
            return true;
        };

        ByteBuffer readBuffer = reader.read(process, 0, memoryBytes.length);
        byte[] readBytes = new byte[readBuffer.remaining()];
        readBuffer.get(readBytes);

        assertArrayEquals(memoryBytes, readBytes);
    }
}
