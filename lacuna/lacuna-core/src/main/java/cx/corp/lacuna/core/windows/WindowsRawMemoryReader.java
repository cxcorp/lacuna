package cx.corp.lacuna.core.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.RawMemoryReader;
import cx.corp.lacuna.core.MemoryReaderImpl;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.ReadProcessMemory;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * {@inheritDoc}
 * @cx.useswinapi
 */
public class WindowsRawMemoryReader implements RawMemoryReader {

    private static final int FLAGS_READMEMORY =
        ProcessAccessFlags.QUERY_INFORMATION | ProcessAccessFlags.VIRTUAL_MEMORY_READ;

    private final ProcessOpener processOpener;
    private final ReadProcessMemory memoryReader;

    /**
     * Constructs a new {@code WindowsRawMemoryReader} using the specified
     * process opener and Kernel32 WindowsAPI proxy.
     *
     * @param processOpener The process opener used to open a handle to the target process.
     * @param memoryReader The WindowsAPI proxy used for process memory reading.
     * @cx.useswinapi
     */
    public WindowsRawMemoryReader(ProcessOpener processOpener, ReadProcessMemory memoryReader) {
        Objects.requireNonNull(processOpener, "processOpener cannot be null!");
        Objects.requireNonNull(memoryReader, "memoryReader cannot be null!");
        this.processOpener = processOpener;
        this.memoryReader = memoryReader;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation is largely implemented on top of the Windows
     * API. First, a handle is opened to the target process, then the
     * memory is read with the Kernel32 {@code ReadProcessMemory} function.
     * @throws ProcessOpenException if a handle for the specified process
     *                              cannot be opened.
     */
    @Override
    public ByteBuffer read(NativeProcess process, int offset, int bytesToRead) {
        Objects.requireNonNull(process, "process cannot be null!");
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
                throw new MemoryAccessException(
                    "An error occurred while reading memory. Use getCause() to get the cause.",
                    err);
            }

            buf.order(ByteOrder.LITTLE_ENDIAN);
            return buf;
        }
    }

    private MemoryAccessException createReadExceptionFromErrorCode(int nativeError) {
        SystemErrorCode error = SystemErrorCode.fromId(nativeError);
        String message;
        if (error == null) {
            message = "System error " + Native.getLastError() + " occurred.";
        } else {
            message = error.toString();
        }
        return new MemoryAccessException(message);
    }
}
