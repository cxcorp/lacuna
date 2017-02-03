package cx.corp.lacuna.core.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

/**
 * {@inheritDoc}
 * Provides native process memory reading functionality on Windows platforms.
 */
public class WindowsMemoryReader implements MemoryReader {

    private static final int FLAGS_READMEMORY =
        ProcessAccessFlags.QUERY_INFORMATION | ProcessAccessFlags.VIRTUAL_MEMORY_READ;

    private final Kernel32 kernel;
    private final ProcessOpener processOpener;

    /**
     * Instantiates a new {@link WindowsMemoryReader} instance using the specified
     * Kernel32 WindowsAPI proxy.
     *
     * @param kernel The Kernel32 WindowsAPI proxy used for process memory reading.
     */
    public WindowsMemoryReader(ProcessOpener processOpener, Kernel32 kernel) {
        if (kernel == null || processOpener == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        this.processOpener = processOpener;
        this.kernel = kernel;
    }

    /**
     * {@inheritDoc}
     * <p>This method implementation first attempts to open a handle for the specified {@code process},
     * then reads the specified memory segment using the
     * {@link Kernel32#readProcessMemory(int, int, Memory, int, IntByReference)} method.
     * <p>In order to retrieve a {@link NativeProcess} instance, the
     * {@link cx.corp.lacuna.core.NativeProcessEnumerator} classes can be used.
     * <blockquote><pre>{@code
     * NativeProcessEnumerator enumerator = new WindowsNativeProcessEnumerator(...);
     * List<NativeProcess> processes = enumerator.getProcesses();
     * MemoryReader reader = new WindowsMemoryReader(...);
     * <p>
     * try {
     *     byte[] bytes = reader.read(processes[0], 0x1C5B100, 16);
     *     for (byte b : bytes) {
     *         System.out.println("%X ", b);
     *     }
     * } catch (ProcessOpenException | MemoryReadException ex) {
     *     System.err.println(ex.toString());
     *     if (ex.getCause() != null) {
     *         System.err.println("Cause: " + ex.getCause());
     *     }
     * }
     * }</pre></blockquote>
     *
     * @throws ProcessOpenException   if the specified process could not be opened for reading.
     * @throws MemoryReadException    if reading the process memory fails.
     */
    @Override
    public byte[] read(NativeProcess process, int offset, int bytesToRead)
        throws ProcessOpenException, MemoryReadException {

        try (ProcessHandle handle = processOpener.open(process.getPid(), FLAGS_READMEMORY)) {
            Memory buffer = new Memory(bytesToRead);
            IntByReference bytesRead = new IntByReference(0);
            boolean success = kernel.readProcessMemory(
                handle.getNativeHandle(),
                offset,
                buffer,
                bytesToRead,
                bytesRead);

            if (!success) {
                int errorCode = Native.getLastError();
                throw createReadExceptionFromErrorCode(errorCode);
            }

            byte[] ret;
            try {
                ret = buffer.getByteArray(0, bytesToRead);
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

            return ret;
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
