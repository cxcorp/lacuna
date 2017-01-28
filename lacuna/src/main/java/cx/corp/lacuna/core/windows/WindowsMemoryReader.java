package cx.corp.lacuna.core.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.ProcessAccessException;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

/** {@inheritDoc}
 * Provides native process memory reading functionality on Windows platforms.
 */
public class WindowsMemoryReader implements MemoryReader {

    private static final int FLAGS_READMEMORY =
            ProcessAccessFlags.QUERY_INFORMATION | ProcessAccessFlags.VIRTUAL_MEMORY_READ;

    private final Kernel32 kernel;

    /** Instantiates a new {@link WindowsMemoryReader} instance using the specified
     * Kernel32 WindowsAPI proxy.
     *
     * @param kernel The Kernel32 WindowsAPI proxy used for process memory reading.
     */
    public WindowsMemoryReader(Kernel32 kernel) {
        this.kernel = kernel;
    }

    /** {@inheritDoc}
     *
     * <p>This method implementation first attempts to open a handle for the specified {@code process}
     * using the Windows API {@link Kernel32#openProcess(int, boolean, int)} method,
     * then reads the specified memory segment using the
     * {@link Kernel32#readProcessMemory(int, int, Memory, int, IntByReference)} method.
     *
     * <p>In order to retrieve a {@link NativeProcess} instance, the
     * {@link cx.corp.lacuna.core.NativeProcessEnumerator} classes can be used.
     *
     * <blockquote><pre>{@code
     * NativeProcessEnumerator enumerator = new WindowsNativeProcessEnumerator(...);
     * List<NativeProcess> processes = enumerator.getProcesses();
     * MemoryReader reader = new WindowsMemoryReader(...);
     *
     * try {
     *     byte[] bytes = reader.read(processes[0], 0x1C5B100, 16);
     *     for (byte b : bytes) {
     *         System.out.println("%X ", b);
     *     }
     * } catch (ProcessAccessException | MemoryReadException ex) {
     *     System.err.println(ex.toString());
     *     if (ex.getCause() != null) {
     *         System.err.println("Cause: " + ex.getCause());
     *     }
     * }
     * }</pre></blockquote>
     *
     * @throws ProcessAccessException if the specified process could not be opened for reading.
     * @throws MemoryReadException if reading the process memory fails.
     */
    @Override
    public byte[] read(NativeProcess process, int offset, int bytesToRead)
            throws ProcessAccessException, MemoryReadException {

        int handle = kernel.openProcess(FLAGS_READMEMORY, false, process.getPid());
        if (handle == WinApiConstants.NULLPTR) {
            throw new ProcessAccessException(
                    "Process " + process + " could not be opened for reading! System error: "
                    + Native.getLastError());
        }

        Memory buffer = new Memory(bytesToRead);
        IntByReference bytesRead = new IntByReference(0);
        boolean success = kernel.readProcessMemory(
                handle,
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
