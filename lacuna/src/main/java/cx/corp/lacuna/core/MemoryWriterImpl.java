package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class MemoryWriterImpl implements MemoryWriter {

    private final RawMemoryWriter writer;

    public MemoryWriterImpl(RawMemoryWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }

        this.writer = writer;
    }

    @Override
    public void writeBoolean(NativeProcess process, int offset, boolean value) throws MemoryAccessException {
        ByteBuffer buffer = createLEBuffer(TypeSize.BOOLEAN.getSize());
        buffer.put(value ? 1 : (byte) 0);
        writer.write(process, offset, buffer.array());
    }

    @Override
    public void writeByte(NativeProcess process, int offset, byte value) throws MemoryAccessException {
        ByteBuffer buffer = createLEBuffer(TypeSize.BYTE.getSize());
        buffer.put(value);
        writer.write(process, offset, buffer.array());
    }

    @Override
    public void writeCharUTF8(NativeProcess process, int offset, char value) throws MemoryAccessException {
        ByteBuffer buffer = createLEBuffer(TypeSize.CHAR_UTF8.getSize());
        byte[] bytes = Character.toString(value).getBytes(StandardCharsets.UTF_8);
        buffer.put(bytes[0]);
        writer.write(process, offset, buffer.array());
    }

    @Override
    public void writeCharUTF16LE(NativeProcess process, int offset, char value) throws MemoryAccessException {
        ByteBuffer buffer = createLEBuffer(TypeSize.CHAR_UTF16LE.getSize());
        byte[] bytes = Character.toString(value).getBytes(StandardCharsets.UTF_16LE);
        buffer.put(bytes[0]);
        buffer.put(bytes[1]);
        writer.write(process, offset, buffer.array());
    }

    @Override
    public void writeShort(NativeProcess process, int offset, short value) throws MemoryAccessException {
        ByteBuffer buffer = createLEBuffer(TypeSize.SHORT.getSize());
        buffer.putShort(value);
        writer.write(process, offset, buffer.array());
    }

    @Override
    public void writeInt(NativeProcess process, int offset, int value) throws MemoryAccessException {
        ByteBuffer buffer = createLEBuffer(TypeSize.INT.getSize());
        buffer.putInt(value);
        writer.write(process, offset, buffer.array());
    }

    @Override
    public void writeFloat(NativeProcess process, int offset, float value) throws MemoryAccessException {
        ByteBuffer buffer = createLEBuffer(TypeSize.FLOAT.getSize());
        buffer.putFloat(value);
        writer.write(process, offset, buffer.array());
    }

    @Override
    public void writeLong(NativeProcess process, int offset, long value) throws MemoryAccessException {
        ByteBuffer buffer = createLEBuffer(TypeSize.LONG.getSize());
        buffer.putLong(value);
        writer.write(process, offset, buffer.array());
    }

    @Override
    public void writeDouble(NativeProcess process, int offset, double value) throws MemoryAccessException {
        ByteBuffer buffer = createLEBuffer(TypeSize.DOUBLE.getSize());
        buffer.putDouble(value);
        writer.write(process, offset, buffer.array());
    }

    @Override
    public void writeStringUTF8(NativeProcess process, int offset, String value) throws MemoryAccessException {
        if (value.length() < 1) {
            throw new IllegalArgumentException("Cannot write empty string!");
        }
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writer.write(process, offset, bytes);
    }

    @Override
    public void writeStringUTF16LE(NativeProcess process, int offset, String value) throws MemoryAccessException {
        if (value.length() < 1) {
            throw new IllegalArgumentException("Cannot write empty string!");
        }
        byte[] bytes = value.getBytes(StandardCharsets.UTF_16LE);
        writer.write(process, offset, bytes);
    }

    @Override
    public void write(NativeProcess process, int offset, byte[] values) throws MemoryAccessException {
        if (values.length < 1) {
            throw new IllegalArgumentException("Cannot write empty array!");
        }
        writer.write(process, offset, values);
    }

    private ByteBuffer createLEBuffer(int length) {
        ByteBuffer buf = ByteBuffer.allocate(length);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf;
    }
}
