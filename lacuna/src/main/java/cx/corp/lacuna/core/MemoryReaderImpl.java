package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
        byte readByte =
            rawMemoryReader.read(process, offset, TypeSize.BOOLEAN.getSize()).get();
        return readByte != 0;
    }

    @Override
    public byte readByte(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, TypeSize.BYTE.getSize()).get();
    }

    @Override
    public char readCharUTF8(NativeProcess process, int offset) {
        return (char) rawMemoryReader.read(process, offset, TypeSize.CHAR_UTF8.getSize()).get();
    }

    @Override
    public char readCharUTF16LE(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, TypeSize.CHAR_UTF16LE.getSize()).getChar();
    }

    @Override
    public short readShort(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, TypeSize.SHORT.getSize()).getShort();
    }

    @Override
    public int readInt(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, TypeSize.INT.getSize()).getInt();
    }

    @Override
    public float readFloat(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, TypeSize.FLOAT.getSize()).getFloat();
    }

    @Override
    public long readLong(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, TypeSize.LONG.getSize()).getLong();
    }

    @Override
    public double readDouble(NativeProcess process, int offset) {
        return rawMemoryReader.read(process, offset, TypeSize.DOUBLE.getSize()).getDouble();
    }

    @Override
    public String readStringUTF8(NativeProcess process, int offset, int characterCount) {
        if (characterCount < 1) {
            throw new IllegalArgumentException("Cannot read strings shorter than 1 character!");
        }

        byte[] buffer = new byte[characterCount];
        int bytesRead = 0;

        for (int i = 0; i < characterCount; i++) {
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
    public String readStringUTF16LE(NativeProcess process, int offset, int characterCount) {
        if (characterCount < 1) {
            throw new IllegalArgumentException("Cannot read strings shorter than 1 character!");
        }

        final int charSize = TypeSize.CHAR_UTF16LE.getSize();
        int totalByteSize = characterCount * charSize;
        ByteBuffer buffer = ByteBuffer.allocate(totalByteSize);

        for (int i = 0; i < totalByteSize; i += charSize) {
            short readShort = readShort(process, offset + i);
            if (readShort == 0) {
                // read until null character is met or maxLength is met
                break;
            }
            buffer.putShort(readShort);
        }

        buffer.flip();
        byte[] truncatedBuf = new byte[buffer.remaining()];
        buffer.get(truncatedBuf);
        return new String(truncatedBuf, StandardCharsets.UTF_16LE);
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
