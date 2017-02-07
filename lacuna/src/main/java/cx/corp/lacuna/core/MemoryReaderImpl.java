package cx.corp.lacuna.core;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.windows.ProcessOpenException;
import cx.corp.lacuna.core.windows.ProcessOpener;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * {@inheritDoc}
 * Provides native process memory reading functionality on Windows platforms.
 */
public class MemoryReaderImpl implements MemoryReader {

    private final RawMemoryReader rawMemoryReader;

    public MemoryReaderImpl(RawMemoryReader rawMemoryReader) {
        if (rawMemoryReader == null) {
            throw new IllegalArgumentException("Argument cannot be null!");
        }
        this.rawMemoryReader = rawMemoryReader;
    }

    @Override
    public boolean readBoolean(NativeProcess process, int offset) {
        byte readByte = rawMemoryReader.read(process, offset, 1).get();
        return readByte != 0;
    }

    @Override
    public byte readByte(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, 1).get();
    }

    @Override
    public char readChar(NativeProcess process, int offset) {
        return (char) rawMemoryReader.read(process, offset, 1).get();
    }

    @Override
    public char readWChar(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, 2).getChar();
    }

    @Override
    public short readShort(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, 2).getShort();
    }

    @Override
    public int readInt(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, 4).getInt();
    }

    @Override
    public float readFloat(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, 4).getFloat();
    }

    @Override
    public long readLong(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, 8).getLong();
    }

    @Override
    public double readDouble(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, 8).getDouble();
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

    @Override
    public byte[] read(NativeProcess process, int offset, int bytesToRead) {
        validateArguments(process, offset);
        if (bytesToRead < 1) {
            throw new IllegalArgumentException("Number of bytes to read must be greater than zero");
        }

        ByteBuffer buffer = rawMemoryReader.read(process, offset, bytesToRead);
        byte[] ret = new byte[buffer.remaining()];
        buffer.get(ret);
        return ret;
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
