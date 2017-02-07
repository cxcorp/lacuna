package cx.corp.lacuna.core.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.RawMemoryReader;
import cx.corp.lacuna.core.MemoryReaderImpl;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.ReadProcessMemory;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WindowsRawMemoryReader implements RawMemoryReader {

    private static final int FLAGS_READMEMORY =
        ProcessAccessFlags.QUERY_INFORMATION | ProcessAccessFlags.VIRTUAL_MEMORY_READ;

    private final ProcessOpener processOpener;
    private final ReadProcessMemory memoryReader;

    /**
     * Instantiates a new {@link MemoryReaderImpl} instance using the specified
     * process opener and  Kernel32 WindowsAPI proxy.
     *
     * @param processOpener The process opener used to open a handle to the target process.
     * @param memoryReader The WindowsAPI proxy used for process memory reading.
     */
    public WindowsRawMemoryReader(ProcessOpener processOpener, ReadProcessMemory memoryReader) {
        if (processOpener == null || memoryReader == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        this.processOpener = processOpener;
        this.memoryReader = memoryReader;
    }

    @Override
    public ByteBuffer read(NativeProcess process, int offset, int bytesToRead) {
        //throws ProcessOpenException, MemoryReadException {
        if (process == null) {
            throw new IllegalArgumentException("Process cannot be null!");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative!");
        }
        if (bytesToRead < 1) {
            throw new IllegalArgumentException("Cannot read fewer than 1 byte!");
        }

        // open might throw ProcessOpenException
        try (ProcessHandle handle = processOpener.open(process.getPid(), FLAGS_READMEMORY)) {
            Memory buffer = new Memory(bytesToRead);
            IntByReference bytesRead = new IntByReference(0);
            boolean success = memoryReader.readProcessMemory(
                handle.getNativeHandle(),
                offset,
                buffer,
                bytesToRead,
                bytesRead);

            if (!success) {
                int errorCode = Native.getLastError();
                throw createReadExceptionFromErrorCode(errorCode);
            }

            ByteBuffer buf;
            try {
                buf = buffer.getByteBuffer(0, bytesToRead);
            } catch (Error err) {
                // e.g. "Invalid memory access" if reading outside memory bounds.
                // the Memory class does bounds checking but you can never be sure since
                // it ultimately calls the natively implemented Native.read method.
                // This is a bit of a doomsday defensive programming scenario so not sure
                // how to unit test this one.
                throw new MemoryReadException(
                    "An error occurred while reading memory. Use getCause() to get the cause.",
                    err);
            }

            buf.order(ByteOrder.LITTLE_ENDIAN);
            return buf;
        }
    }

    private MemoryReadException createReadExceptionFromErrorCode(int nativeError) {
        SystemErrorCode error = SystemErrorCode.fromId(nativeError);
        String message;
        if (error == null) {
            message = "System error " + Native.getLastError() + " occurred.";
        } else {
            message = error.toString();
        }
        return new MemoryReadException(message);
    }
}
