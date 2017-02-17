package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.windows.winapi.WriteProcessMemory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WindowsRawMemoryWriterTest {

    private WindowsRawMemoryWriter writer;
    private MockProcessOpener opener;
    private WriteProcessMemory writeProcessMemory;
    private MockProcessHandle handle;
    private NativeProcess process;

    @Before
    public void setUp() {
        handle = new MockProcessHandle(123);
        opener = new MockProcessOpener();
        writeProcessMemory = (handle, offset, data, numberOfBytes, bytesWritten) -> false;
        writer = new WindowsRawMemoryWriter(
            (pid, flags) -> opener.open(pid, flags),
            (h, o, d, n, b) -> writeProcessMemory.writeProcessMemory(h, o, d, n, b)
        );
        process = new NativeProcessImpl(
            12345,
            NativeProcess.UNKNOWN_DESCRIPTION,
            NativeProcess.UNKNOWN_OWNER
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfOpenerIsNull() {
        new WindowsRawMemoryWriter(null, writeProcessMemory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfWriterIsNull() {
        new WindowsRawMemoryWriter(opener, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeThrowsIfProcessIsNull() {
        writer.write(null, 0, new byte[]{1});
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeThrowsIfBufferDoesntHaveElements() {
        writer.write(process, 0, new byte[0]);
    }

    @Test(expected = MemoryAccessException.class)
    public void writeThrowsIfWriteProcessMemoryFails() {
        opener.setOpenReturnValue(handle);
        writeProcessMemory = (handle1, offset, data, numberOfBytes, bytesWritten) -> false;
        writer.write(process, 0, new byte[]{123});
    }

    @Test(expected = ProcessOpenException.class)
    public void writeThrowsIfProcessCannotBeOpened() {
        opener.throwExceptionOnOpen();
        writer.write(process, 0, new byte[]{52, 4});
    }

    @Test
    public void writeUsesCorrectHandleWhenWriting() {
        int expectedHandle = 4125;
        handle.setNativeHandle(expectedHandle);
        opener.setOpenReturnValue(handle);
        opener.doNotThrowExceptionOnOpen();
        writeProcessMemory = (handle1, offset, data, numberOfBytes, bytesWritten) -> {
            assertEquals(expectedHandle, handle1);
            return true;
        };
        writer.write(process, 0, new byte[]{5});
    }

    @Test
    public void writeWritesToRightOffset() {
        int writeOffset = 0xBA1BA1;
        opener.setOpenReturnValue(handle);
        opener.doNotThrowExceptionOnOpen();
        writeProcessMemory = (handle1, offset, data, numberOfBytes, bytesWritten) -> {
            assertEquals(writeOffset, offset);
            return true;
        };
        writer.write(process, writeOffset, new byte[]{5});
    }

    @Test
    public void writeWritesCorrectBytes() {
        byte[] expectedData = { 1, 2, 52, -42, 98, -58, -123 };
        opener.setOpenReturnValue(handle);
        opener.doNotThrowExceptionOnOpen();
        writeProcessMemory = (handle1, offset, data, numberOfBytes, bytesWritten) -> {
            assertArrayEquals(expectedData, data);
            return true;
        };
        writer.write(process, 0, expectedData);
    }

    @Test
    public void writeUsesCorrectAmountOfBytes() {
        byte[] expectedData = { 1, 2, 52, -42, 98, -58, -123 };
        opener.setOpenReturnValue(handle);
        opener.doNotThrowExceptionOnOpen();
        writeProcessMemory = (handle1, offset, data, numberOfBytes, bytesWritten) -> {
            assertEquals(expectedData.length, numberOfBytes);
            return true;
        };
        writer.write(process, 0, expectedData);
    }

    @Test
    public void writeWritesOneByteCorrectly() {
        byte[] expectedData = {1};
        opener.setOpenReturnValue(handle);
        opener.doNotThrowExceptionOnOpen();
        writeProcessMemory = (h, o, data, n, b) -> {
            assertArrayEquals(expectedData, data);
            return true;
        };
        writer.write(process, 0, expectedData);
    }
}
