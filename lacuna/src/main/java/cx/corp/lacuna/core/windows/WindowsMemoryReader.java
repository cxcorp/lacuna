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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;

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

    @Override
    public boolean readBoolean(NativeProcess process, int offset) {
        byte readByte = readBuffer(process, offset, 1).get();
        return readByte != 0;
    }

    @Override
    public byte readByte(NativeProcess process, int offset) {
        return readBuffer(process, offset, 1).get();
    }

    @Override
    public char readChar(NativeProcess process, int offset) {
        return (char) readBuffer(process, offset, 1).get();
    }

    @Override
    public char readWChar(NativeProcess process, int offset) {
        return readBuffer(process, offset, 2).getChar();
    }

    @Override
    public short readShort(NativeProcess process, int offset) {
        return readBuffer(process, offset, 2).getShort();
    }

    @Override
    public int readInt(NativeProcess process, int offset) {
        return readBuffer(process, offset, 4).getInt();
    }

    @Override
    public float readFloat(NativeProcess process, int offset) {
        return readBuffer(process, offset, 4).getFloat();
    }

    @Override
    public long readLong(NativeProcess process, int offset) {
        return readBuffer(process, offset, 8).getLong();
    }

    @Override
    public double readDouble(NativeProcess process, int offset) {
        return readBuffer(process, offset, 8).getDouble();
    }

    @Override
    public String readString(NativeProcess process, int offset, int maxByteLength) {
        byte[] buffer = new byte[maxByteLength];
        int bytesRead = 0;

        for (int i = 0; i < maxByteLength; i++) {
            byte readByte = readByte(process, offset + i);
            if (readByte == 0) {
                // read until null character is met or maxLength is met
                break;
            }
            buffer[i] = readByte;
            bytesRead++;
        }

        return new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
    }

    @Override
    public String readWString(NativeProcess process, int offset, int maxByteLength) {
        if (maxByteLength % 2 != 0) { // TODO: byte sizes to own file
            throw new IllegalArgumentException("Maximum byte length must be divisible by the size of wchar!");
        }

        ByteBuffer buffer = ByteBuffer.allocate(maxByteLength);

        for (int i = 0; i < maxByteLength; i++) {
            short readShort = readShort(process, offset + (i * 2));
            if (readShort == 0) {
                // read until null character is met or maxLength is met
                break;
            }
            buffer.putShort(readShort);
        }

        buffer.flip();
        byte[] truncatedBuf = new byte[buffer.remaining()];
        buffer.get(truncatedBuf);
        return new String(truncatedBuf, StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     * <p>This method implementation first attempts to open a handle for the specified {@code process},
     * then reads the specified memory segment using the
     * {@link Kernel32#readProcessMemory(int, int, Memory, int, IntByReference)} method.
     *
     * @throws ProcessOpenException     if the specified process could not be opened for reading.
     * @throws MemoryReadException      if reading the process memory fails.
     * @throws IllegalArgumentException if attempting to read less than one byte.
     */
    @Override
    public byte[] read(NativeProcess process, int offset, int bytesToRead) {
        validateArguments(process, offset);
        if (bytesToRead < 1) {
            throw new IllegalArgumentException("Number of bytes to read must be greater than zero");
        }

        ByteBuffer buffer = readBuffer(process, offset, bytesToRead);
        byte[] ret = new byte[buffer.remaining()];
        buffer.get(ret);
        return ret;
    }

    private ByteBuffer readBuffer(NativeProcess process, int offset, int bytesToRead)
            throws ProcessOpenException, MemoryReadException {

        // open might throw ProcessOpenException
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

    private static void validateArguments(NativeProcess process, int offset) {
        if (process == null) {
            throw new IllegalArgumentException("process cannot be null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
    }
}
