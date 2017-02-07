package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.domain.NativeProcess;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class LinuxMemoryReader implements MemoryReader {

    private final MemoryProvider memoryProvider;

    public LinuxMemoryReader(MemoryProvider memoryProvider) {
        if (memoryProvider == null) {
            throw new IllegalArgumentException("memoryProvider cannot be null");
        }
        this.memoryProvider = memoryProvider;
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
        return null;
    }

    @Override
    public String readWString(NativeProcess process, int offset, int maxByteLength) {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MemoryReadException if the virtual memory file for the process cannot be opened,
     *                             seeking to the requested offset fails, or reading the requested
     *                             bytes fails.
     */
    @Override
    public byte[] read(NativeProcess process, int offset, int bytesToRead) {
        validateArguments(process, offset);

        ByteBuffer buffer = readBuffer(process, offset, bytesToRead);
        byte[] ret = new byte[buffer.remaining()];
        buffer.get(ret);
        return ret;
    }

    private ByteBuffer readBuffer(NativeProcess process, int offset, int bytesToRead) {
        ByteBuffer buffer = ByteBuffer.allocate(bytesToRead);

        try (InputStream input = memoryProvider.open(process.getPid())) {
            if (input == null) {
                throw new MemoryReadException("MemoryProvider provided a null stream!");
            }

            long skippedBytes = input.skip(offset);
            if (offset != skippedBytes) {
                throw new MemoryReadException("Failed to seek to offset " + offset + "! Actually skipped " + skippedBytes + " bytes.");
            }

            // write directly to ByteBuffer's backing array
            int bytesRead = input.read(buffer.array(), 0, bytesToRead);

            if (bytesRead == -1) {
                throw new MemoryReadException("Reading process memory failed!");
            }

            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(bytesRead);
            return buffer;
        } catch (IOException ex) {
            throw new MemoryReadException("Reading process memory failed, see getCause()!", ex);
        }
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
