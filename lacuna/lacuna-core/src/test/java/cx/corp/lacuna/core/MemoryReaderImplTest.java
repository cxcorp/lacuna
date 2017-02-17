package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MemoryReaderImplTest {

    private static final int BOOLEAN_SIZE = 1;
    private static final int BYTE_SIZE = 1;
    private static final int CHAR_UTF8_SIZE = 1;
    private static final int CHAR_UTF16LE_SIZE = 2;
    private static final int SHORT_SIZE = 2;
    private static final int INT_SIZE = 4;
    private static final int FLOAT_SIZE = 4;
    private static final int LONG_SIZE = 8;
    private static final int DOUBLE_SIZE = 8;

    private RawMemoryReader rawReader;
    private MemoryReaderImpl reader;
    private NativeProcess process;

    private static ByteBuffer toBuffer(byte... bytes) {
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        return wrap;
    }

    private static ByteBuffer floatToLittleEndianBuffer(float value) {
        ByteBuffer buf = ByteBuffer.allocate(TypeSize.FLOAT.getSize());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putFloat(value);
        buf.flip();
        return buf;
    }

    private static ByteBuffer doubleToLittleEndianBuffer(double value) {
        ByteBuffer buf = ByteBuffer.allocate(TypeSize.DOUBLE.getSize());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putDouble(value);
        buf.flip();
        return buf;
    }

    private static ByteBuffer longToLittleEndianBuffer(long value) {
        ByteBuffer buf = ByteBuffer.allocate(TypeSize.LONG.getSize());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putLong(value);
        buf.flip();
        return buf;
    }

    private static ByteBuffer intToLittleEndianBuffer(int value) {
        ByteBuffer buf = ByteBuffer.allocate(TypeSize.INT.getSize());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(value);
        buf.flip();
        return buf;
    }

    private static ByteBuffer shortToLittleEndianBuffer(short value) {
        ByteBuffer buf = ByteBuffer.allocate(TypeSize.SHORT.getSize());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putShort(value);
        buf.flip();
        return buf;
    }

    @Before
    public void setUp() {
        rawReader = null;
        process = new NativeProcessImpl(123, null, null);
        reader = new MemoryReaderImpl((a, b, c) -> rawReader.read(a, b, c));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfNullArgumentPassed() {
        new MemoryReaderImpl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsIfReadingZeroBytes() {
        reader.read(process, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsIfReadingNegativeBytes() {
        reader.read(process, 0, -151);
    }

    @Test
    public void readReadsOneByte() {
        byte[] source = new byte[]{123};
        rawReader = new ByteArrayRawMemoryReader(source);

        byte[] readBytes = reader.read(process, 0, source.length);

        assertArrayEquals(source, readBytes);
    }

    @Test
    public void readReadsManyBytes() {
        byte[] source = new byte[0xFF];
        for (int i = 0; i < source.length; i++) {
            source[i] = (byte) i;
        }
        rawReader = new ByteArrayRawMemoryReader(source);

        byte[] readBytes = reader.read(process, 0, source.length);

        assertArrayEquals(source, readBytes);
    }

    @Test
    public void readReadsBytesAtOffset() {
        String text = "the quick brown fox jumps over the fence";
        int offset = 10;
        int length = 15;
        byte[] source = text.getBytes(StandardCharsets.US_ASCII);
        rawReader = new ByteArrayRawMemoryReader(source);

        byte[] readBytes = reader.read(process, offset, length);

        byte[] expected = text.substring(offset, offset + length).getBytes(StandardCharsets.US_ASCII);
        assertArrayEquals(expected, readBytes);
    }

    @Test
    public void readBooleanReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readBoolean(process, 0);

        assertEquals(BOOLEAN_SIZE, bytesTriedToRead.get());
    }

    @Test
    public void readBooleanReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readBoolean(process, 0);
    }

    @Test
    public void readByteReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readByte(process, 0);

        assertEquals(BYTE_SIZE, bytesTriedToRead.get());
    }

    @Test
    public void readByteReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readByte(process, 0);
    }

    @Test
    public void readCharUTF8ReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readCharUTF8(process, 0);

        assertEquals(CHAR_UTF8_SIZE, bytesTriedToRead.get());
    }

    @Test
    public void readCharUTF8ReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readCharUTF8(process, 0);
    }

    @Test
    public void readCharUTF16LEReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readCharUTF16LE(process, 0);

        assertEquals(CHAR_UTF16LE_SIZE, bytesTriedToRead.get());
    }

    @Test
    public void readCharUTF16LEReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readCharUTF16LE(process, 0);
    }

    @Test
    public void readShortReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readShort(process, 0);

        assertEquals(SHORT_SIZE, bytesTriedToRead.get());
    }

    @Test
    public void readShortReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readShort(process, 0);
    }

    @Test
    public void readIntReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readInt(process, 0);

        assertEquals(INT_SIZE, bytesTriedToRead.get());
    }

    @Test
    public void readIntReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readInt(process, 0);
    }

    @Test
    public void readFloatReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readFloat(process, 0);

        assertEquals(FLOAT_SIZE, bytesTriedToRead.get());
    }

    @Test
    public void readFloatReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readFloat(process, 0);
    }

    @Test
    public void readLongReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readLong(process, 0);

        assertEquals(LONG_SIZE, bytesTriedToRead.get());
    }

    @Test
    public void readLongReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readLong(process, 0);
    }

    @Test
    public void readDoubleReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readDouble(process, 0);

        assertEquals(DOUBLE_SIZE, bytesTriedToRead.get());
    }

    @Test
    public void readDoubleReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readDouble(process, 0);
    }

    @Test
    public void readStringUTF8ReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return toBuffer("a".getBytes(StandardCharsets.UTF_8)); // non-null character
        };

        reader.readStringUTF8(process, 0, 15);

        assertEquals(CHAR_UTF8_SIZE * 15, bytesTriedToRead.get());
    }

    @Test
    public void readStringUTF8ReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readStringUTF8(process, 0, 1);
    }

    @Test
    public void readStringUTF16LEReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return toBuffer("a".getBytes(StandardCharsets.UTF_16LE)); // non-null character
        };

        reader.readStringUTF16LE(process, 0, 15);

        assertEquals(CHAR_UTF16LE_SIZE * 15, bytesTriedToRead.get());
    }

    @Test
    public void readStringUTF16LEReadsRightProcess() {
        NativeProcess proc = this.process;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(proc, process);
            return ByteBuffer.allocate(bytesToRead);
        };

        reader.readStringUTF16LE(process, 0, 1);
    }

    @Test
    public void readStringUTF16LEReadsCorrectString() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        String string = "åäöabcdefghijklmnopqrstuvwxyz123456789";
        rawReader = new RawMemoryReader() { // read `string` one character at a time
            private int i = 0;

            @Override
            public ByteBuffer read(NativeProcess process, int offset, int bytesToRead) {
                bytesTriedToRead.addAndGet(bytesToRead);
                return toBuffer(getNextChar().getBytes(StandardCharsets.UTF_16LE));
            }

            private String getNextChar() {
                int index = i++;
                return string.substring(index, index + 1);
            }
        };
        int charsToRead = 15;

        String readString = reader.readStringUTF16LE(process, 0, charsToRead);

        assertEquals(string.substring(0, charsToRead), readString);
    }

    @Test
    public void readBooleanReadsZeroAsFalse() {
        rawReader = (proc, offset, bytesToRead) -> toBuffer((byte) 0);

        boolean result = reader.readBoolean(process, 0);

        assertFalse(result);
    }

    @Test
    public void readBooleanReadsAnyNonZeroAsTrue() {
        byte[] data = new byte[0xFF - 1];
        for (int i = 1; i < data.length; i++) {
            data[(i - 1)] = (byte) i;
        }

        rawReader = (p, o, b) -> toBuffer(data);

        for (int i = 0; i < data.length; i++) {
            boolean result = reader.readBoolean(process, i);
            assertTrue("Expected " + data[i] + " to be truthy!", result);
        }
    }

    @Test
    public void readBooleanReadsBooleanAtOffset() {
        int expectedOffset = 0xFFEFEF;
        rawReader = (p, offset, b) -> {
            assertEquals(expectedOffset, offset);
            return toBuffer((byte) 1);
        };

        boolean result = reader.readBoolean(process, expectedOffset);

        assertTrue(result);
    }

    @Test
    public void readByteReadsAllBytesCorrectly() {
        byte[] source = new byte[0xFF];
        for (int i = 0; i < source.length; i++) {
            source[i] = (byte) i;
        }
        rawReader = new ByteArrayRawMemoryReader(source);

        for (int i = 0; i < source.length; i++) {
            byte read = reader.readByte(process, i);
            assertEquals(source[i], read);
        }
    }

    @Test
    public void readByteReadsByteAtCorrectOffset() {
        int expectedOffset = 0xB51FF2;
        byte[] data = new byte[]{9};
        rawReader = (process1, offset, bytesToRead) -> {
            assertEquals(expectedOffset, offset);
            return toBuffer(data);
        };

        byte readData = reader.readByte(process, expectedOffset);

        assertEquals(data[0], readData);
    }

    @Test
    public void readCharUTF8ReadsChar() {
        String text = "Toaster";
        rawReader = (p, o, bytesToRead) -> {
            ByteBuffer data = toBuffer(text.getBytes(StandardCharsets.UTF_8));
            data.order(ByteOrder.LITTLE_ENDIAN);
            data.limit(bytesToRead);
            return data;
        };

        char result = reader.readCharUTF8(process, 0);

        assertEquals(text.charAt(0), result);
    }

    @Test
    public void readCharUTF8ReadsCharAtOffset() {
        String text = "Quads";
        int expectedOffset = 0x5E5EBEEF;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(offset, expectedOffset);
            ByteBuffer data = toBuffer(text.getBytes(StandardCharsets.UTF_8));
            data.limit(bytesToRead);
            data.order(ByteOrder.LITTLE_ENDIAN);
            return data;
        };

        char result = reader.readCharUTF8(process, expectedOffset);

        assertEquals(text.charAt(0), result);
    }

    @Test
    public void readCharUTF8ReadsAllSingleCodeUnitCharsCorrectly() {
        byte[] bytes = new byte[0b0111_1111];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }
        rawReader = (process, offset, bytesToRead) ->
            toBuffer(Arrays.copyOfRange(bytes, offset, offset + bytesToRead));

        for (int i = 0; i < bytes.length; i++) {
            char expected = new String(bytes, i, 1, StandardCharsets.UTF_8).charAt(0);
            char result = reader.readCharUTF8(process, i);
            assertEquals(expected, result);
        }
    }

    @Test
    public void readCharUTF16LEReadsChar() {
        String text = "Toaster";
        rawReader = (process, order, bytesToRead) -> {
            ByteBuffer data = toBuffer(text.getBytes(StandardCharsets.UTF_16LE));
            data.order(ByteOrder.LITTLE_ENDIAN);
            data.limit(bytesToRead);
            return data;
        };

        char result = reader.readCharUTF8(process, 0);

        assertEquals(text.charAt(0), result);
    }

    @Test
    public void readCharUTF16LEReadsCharAtOffset() {
        String text = "Quads";
        int expectedOffset = 0xBEEF;
        rawReader = (process, offset, bytesToRead) -> {
            assertEquals(expectedOffset, offset);
            ByteBuffer ret = toBuffer(text.getBytes(StandardCharsets.UTF_16LE));
            ret.order(ByteOrder.LITTLE_ENDIAN);
            ret.limit(bytesToRead);
            return ret;
        };

        char result = reader.readCharUTF16LE(process, 0xBEEF);

        assertEquals(text.charAt(0), result);
    }

    @Test
    public void readShortReadsShortCorrectly() {
        short expectedValue = 0x7EFE;
        rawReader = (p, o, b) -> shortToLittleEndianBuffer(expectedValue);

        short result = reader.readShort(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readShortReadsZeroCorrectly() {
        short expectedValue = 0x0000;
        rawReader = (p, o, b) -> shortToLittleEndianBuffer(expectedValue);

        short result = reader.readShort(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readShortReadsMaxSignedValueCorrectly() {
        short expectedValue = Short.MAX_VALUE;
        rawReader = (p, o, b) -> shortToLittleEndianBuffer(expectedValue);

        short result = reader.readShort(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readShortReadsMinSignedValueCorrectly() {
        short expectedValue = Short.MIN_VALUE;
        rawReader = (p, o, b) -> shortToLittleEndianBuffer(expectedValue);

        short result = reader.readShort(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readShortReadsMinusOneCorrectly() {
        short expectedValue = -1;
        rawReader = (p, o, b) -> shortToLittleEndianBuffer(expectedValue);

        short result = reader.readShort(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readShortReadsOneCorrectly() {
        short expectedValue = 1;
        rawReader = (p, o, b) -> shortToLittleEndianBuffer(expectedValue);

        short result = reader.readShort(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readShortReadsValueAtOffsetCorrectly() {
        short expectedValue = 0x7EFE;
        int expectedOffset = 0x5E5E;
        rawReader = (p, offset, b) -> {
            assertEquals(expectedOffset, offset);
            return shortToLittleEndianBuffer(expectedValue);
        };

        short result = reader.readShort(process, expectedOffset);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readIntReadsCorrectly() {
        int expectedValue = 0x7EFEAA5;
        rawReader = (p, o, b) -> intToLittleEndianBuffer(expectedValue);

        int result = reader.readInt(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readIntReadsOneCorrectly() {
        int expectedValue = 1;
        rawReader = (p, o, b) -> intToLittleEndianBuffer(expectedValue);

        int result = reader.readInt(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readIntReadsZeroCorrectly() {
        int expectedValue = 0;
        rawReader = (p, o, b) -> intToLittleEndianBuffer(expectedValue);

        int result = reader.readInt(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readIntReadsMaxSignedValueCorrectly() {
        int expectedValue = Integer.MAX_VALUE;
        rawReader = (p, o, b) -> intToLittleEndianBuffer(expectedValue);

        int result = reader.readInt(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readIntReadsMinSignedValueCorrectly() {
        int expectedValue = Integer.MIN_VALUE;
        rawReader = (p, o, b) -> intToLittleEndianBuffer(expectedValue);

        int result = reader.readInt(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readIntReadsMinusOneCorrectly() {
        int expectedValue = -1;
        rawReader = (p, o, b) -> intToLittleEndianBuffer(expectedValue);

        int result = reader.readInt(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readIntReadsValueAtOffsetCorrectly() {
        int expectedValue = 123456789;
        int expectedOffset = 0x70000F;
        rawReader = (p, offset, b) -> {
            assertEquals(expectedOffset, offset);
            return intToLittleEndianBuffer(expectedValue);
        };

        int result = reader.readInt(process, expectedOffset);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readFloatReadsValueCorrectly() {
        float expectedValue = 158.5f;
        rawReader = (p, o, b) -> floatToLittleEndianBuffer(expectedValue);

        float result = reader.readFloat(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readFloatReadsOneCorrectly() {
        float expectedValue = 1f;
        rawReader = (p, o, b) -> floatToLittleEndianBuffer(expectedValue);

        float result = reader.readFloat(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readFloatReadsMinusOneCorrectly() {
        float expectedValue = -1f;
        rawReader = (p, o, b) -> floatToLittleEndianBuffer(expectedValue);

        float result = reader.readFloat(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readFloatReadsMinValueCorrectly() {
        float expectedValue = Float.MIN_VALUE;
        rawReader = (p, o, b) -> floatToLittleEndianBuffer(expectedValue);

        float result = reader.readFloat(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readFloatReadsMaxValueCorrectly() {
        float expectedValue = Float.MAX_VALUE;
        rawReader = (p, o, b) -> floatToLittleEndianBuffer(expectedValue);

        float result = reader.readFloat(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readFloatReadsValueAtOffsetCorrectly() {
        float expectedValue = 881924.5123f;
        int expectedOffset = 0xFEFEFEFE;
        rawReader = (p, offset, b) -> {
            assertEquals(expectedOffset, offset);
            return floatToLittleEndianBuffer(expectedValue);
        };

        float result = reader.readFloat(process, expectedOffset);

        assertEquals(expectedValue, result, 0f);
    }

    @Test
    public void readLongReadsCorrectly() {
        long expectedValue = 0xA7677F00F1234L;
        rawReader = (p, o, b) -> longToLittleEndianBuffer(expectedValue);

        long result = reader.readLong(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readLongReadsOneCorrectly() {
        long expectedValue = 1L;
        rawReader = (p, o, b) -> longToLittleEndianBuffer(expectedValue);

        long result = reader.readLong(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readLongReadsMaxSignedValueCorrectly() {
        long expectedValue = Long.MAX_VALUE;
        rawReader = (p, o, b) -> longToLittleEndianBuffer(expectedValue);

        long result = reader.readLong(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readLongReadsMinSignedValueCorrectly() {
        long expectedValue = Long.MIN_VALUE;
        rawReader = (p, o, b) -> longToLittleEndianBuffer(expectedValue);

        long result = reader.readLong(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readLongReadsMinusOneCorrectly() {
        long expectedValue = -1L;
        rawReader = (p, o, b) -> longToLittleEndianBuffer(expectedValue);

        long result = reader.readLong(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readLongReadsShortAtOffsetCorrectly() {
        long expectedValue = 89128586921749L;
        int expectedOffset = 0x51240BB;
        rawReader = (p, offset, b) -> {
            assertEquals(expectedOffset, offset);
            return longToLittleEndianBuffer(expectedValue);
        };

        long result = reader.readLong(process, expectedOffset);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readLongReadsZeroCorrectly() {
        long expectedValue = 0L;
        rawReader = (p, o, b) -> longToLittleEndianBuffer(expectedValue);

        long result = reader.readLong(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readDoubleReadsValueCorrectly() {
        double expectedValue = 51512.315125213d;
        rawReader = (p, o, b) -> doubleToLittleEndianBuffer(expectedValue);

        double result = reader.readDouble(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readDoubleReadsOneCorrectly() {
        double expectedValue = 1d;
        rawReader = (p, o, b) -> doubleToLittleEndianBuffer(expectedValue);

        double result = reader.readDouble(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readDoubleReadsMinusOneCorrectly() {
        double expectedValue = -1d;
        rawReader = (p, o, b) -> doubleToLittleEndianBuffer(expectedValue);

        double result = reader.readDouble(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readDoubleReadsMinValueCorrectly() {
        double expectedValue = Double.MIN_VALUE;
        rawReader = (p, o, b) -> doubleToLittleEndianBuffer(expectedValue);

        double result = reader.readDouble(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readDoubleReadsMaxValueCorrectly() {
        double expectedValue = Double.MAX_VALUE;
        rawReader = (p, o, b) -> doubleToLittleEndianBuffer(expectedValue);

        double result = reader.readDouble(process, 0);

        assertEquals(expectedValue, result, 0);
    }

    @Test
    public void readDoubleReadsValueAtOffsetCorrectly() {
        double expectedValue = 1923951.15121323d;
        int expectedOffset = 0xFEFEFEFE;
        rawReader = (p, offset, b) -> {
            assertEquals(expectedOffset, offset);
            return doubleToLittleEndianBuffer(expectedValue);
        };

        double result = reader.readDouble(process, expectedOffset);

        assertEquals(expectedValue, result, 0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readStringUTF8ThrowsIfTryingToReadZeroCharacters() {
        reader.readStringUTF8(process, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readStringUTF8ThrowsIfTryingToReadNegativeAmountOfCharacters() {
        reader.readStringUTF8(process, 0, -1512);
    }

    @Test
    public void readStringUTF8StopsAtFirstNull() {
        String textBeforeNull = "The quick brown fox";
        String textAfterNull = "does a thing";
        String text = textBeforeNull + '\0' + textAfterNull;
        byte[] source = text.getBytes(StandardCharsets.UTF_8);
        rawReader = new ByteArrayRawMemoryReader(source);

        // try to read longer than null
        String readString = reader.readStringUTF8(process, 0, textBeforeNull.length());
        assertEquals(textBeforeNull, readString);
    }

    @Test
    public void readStringUTF8ReadsFullNormalText() {
        String text = "Liirum laarum lopsem dipsom\r\na bit of \ttabs ayyee.";
        byte[] source = text.getBytes(StandardCharsets.UTF_8);
        rawReader = new ByteArrayRawMemoryReader(source);

        String readString = reader.readStringUTF8(process, 0, source.length);
        assertEquals(text, readString);
    }

    @Test
    public void readStringUTF8ReadsEmojiAsLongAsFullCodePointsAreRead() {
        String text = "👌👀 good shit go౦ԁ sHit👌 thats ✔";
        byte[] source = text.getBytes(StandardCharsets.UTF_8);
        rawReader = new ByteArrayRawMemoryReader(source);

        String readString = reader.readStringUTF8(process, 0, source.length);
        assertEquals(text, readString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readStringUTF16LEThrowsIfTryingToReadZeroCharacters() {
        reader.readStringUTF16LE(process, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readStringUTF16LEThrowsIfTryingToReadNegativeAmountOfCharacters() {
        reader.readStringUTF16LE(process, 0, -1512);
    }

    @Test
    public void readStringUTF16LEStopsAtFirstNull() {
        String textBeforeNull = "The quick brown fox";
        String textAfterNull = "does a thing";
        String text = textBeforeNull + '\0' + textAfterNull;
        byte[] source = text.getBytes(StandardCharsets.UTF_16LE);
        rawReader = new ByteArrayRawMemoryReader(source);

        String readString = reader.readStringUTF16LE(
            process,
            0,
            source.length / TypeSize.CHAR_UTF16LE.getSize());
        assertEquals(textBeforeNull, readString);
    }

    @Test
    public void readStringUTF16LEReadsFullNormalText() {
        String text = "Liirum laarum lopsem dipsom\r\na bit of \ttabs ayyee.";
        byte[] source = text.getBytes(StandardCharsets.UTF_16LE);
        rawReader = new ByteArrayRawMemoryReader(source);

        // try to read longer than null
        String readString = reader.readStringUTF16LE(
            process,
            0,
            source.length / TypeSize.CHAR_UTF16LE.getSize());
        assertEquals(text, readString);
    }

    @Test
    public void readStringUTF16LEReadsEmojiAsLongAsFullCodePointsAreRead() {
        String text = "👌👀 good shit go౦ԁ sHit👌 thats ✔";
        byte[] source = text.getBytes(StandardCharsets.UTF_16LE);
        rawReader = new ByteArrayRawMemoryReader(source);

        // try to read longer than null
        String readString = reader.readStringUTF16LE(
            process,
            0,
            source.length / TypeSize.CHAR_UTF16LE.getSize());
        assertEquals(text, readString);
    }

    private static class ByteArrayRawMemoryReader implements RawMemoryReader {
        private final ByteArrayInputStream stream;
        private int nextByteIndex;

        public ByteArrayRawMemoryReader(byte[] source) {
            stream = new ByteArrayInputStream(source);
        }

        @Override
        public ByteBuffer read(NativeProcess process, int offset, int bytesToRead) {
            stream.reset();
            stream.skip(offset);
            byte[] buffer = new byte[bytesToRead];
            try {
                stream.read(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return toBuffer(buffer);
        }
    }
}
