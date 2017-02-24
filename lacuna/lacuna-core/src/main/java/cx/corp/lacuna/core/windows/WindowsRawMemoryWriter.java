package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.MemoryReaderImpl;
import cx.corp.lacuna.core.RawMemoryWriter;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WriteProcessMemory;

import java.util.Objects;

/**
 * {@inheritDoc}
 * @cx.useswinapi
 */
public class WindowsRawMemoryWriter implements RawMemoryWriter {

    // flags needed to write to the memory
    // https://msdn.microsoft.com/en-us/library/windows/desktop/ms681674(v=vs.85).aspx
    private static final int FLAGS_WRITEMEMORY =
        ProcessAccessFlags.VIRTUAL_MEMORY_OPERATION | ProcessAccessFlags.VIRTUAL_MEMORY_WRITE;

    private final ProcessOpener processOpener;
    private final WriteProcessMemory memoryWriter;

    /**
     * Constructs a new {@code WindowsRawMemoryWriter} using the specified
     * process opener and Kernel32 WindowsAPI proxy.
     *
     * @param processOpener The process opener used to open a handle to the target process.
     * @param memoryWriter The WindowsAPI proxy used for process memory writing.
     * @throws NullPointerException if {@code processOpener} or {@code memoryWriter}
     *                              is null.
     * @cx.useswinapi
     */
    public WindowsRawMemoryWriter(ProcessOpener processOpener, WriteProcessMemory memoryWriter) {
        Objects.requireNonNull(processOpener, "processOpener cannot be null!");
        Objects.requireNonNull(memoryWriter, "memoryWriter cannot be null!");
        this.processOpener = processOpener;
        this.memoryWriter = memoryWriter;
    }

    @Override
    public void write(NativeProcess process, int offset, byte[] buffer) throws MemoryAccessException {
        Objects.requireNonNull(process, "process cannot be null!");
        Objects.requireNonNull(buffer, "buffer cannot be null!");
        if (buffer.length < 1) {
            throw new IllegalArgumentException("Can't write fewer than 1 byte!");
        }

        try (ProcessHandle handle = processOpener.open(process.getPid(), FLAGS_WRITEMEMORY)) {
            boolean success = memoryWriter.writeProcessMemory(
                handle.getNativeHandle(),
                offset,
                buffer,
                buffer.length,
                null);

            if (!success) {
                // if we want to retry by VirtualProtectEx'ing us some rights to the memory, retry here?
                int errorCode = Native.getLastError();
                throw createReadExceptionFromErrorCode(errorCode);
            }
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
