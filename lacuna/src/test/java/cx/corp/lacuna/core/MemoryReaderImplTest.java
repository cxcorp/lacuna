package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

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
        rawReader = new RawMemoryReader() {
            int i = 0;

            @Override
            public ByteBuffer read(NativeProcess process, int offset, int bytesToRead) {
                int index = nextIndex();
                byte[] next = Arrays.copyOfRange(source, index, index + bytesToRead);
                return toBuffer(next);
            }

            private int nextIndex() {
                return i++;
            }
        };

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
    public void readShortReads0xFFFFCorrectly() {
        short expectedValue = (short) 0xFFFF;
        rawReader = (p, o, b) -> shortToLittleEndianBuffer(expectedValue);

        short result = reader.readShort(process, 0);

        assertEquals(expectedValue, result);
    }

    @Test
    public void readShortReadsShortAtOffsetCorrectly() {
        short expectedValue = 0x7EFE;
        int expectedOffset = 0x5E5E;
        rawReader = (p, offset, b) -> {
            assertEquals(expectedOffset, offset);
            return shortToLittleEndianBuffer(expectedValue);
        };

        short result = reader.readShort(process, expectedOffset);

        assertEquals(expectedValue, result);
    }

    // public void readInt...

    private static ByteBuffer toBuffer(byte... bytes) {
        return ByteBuffer.wrap(bytes);
    }

    private static ByteBuffer shortToLittleEndianBuffer(short value) {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putShort(value);
        buf.flip();
        return buf;
    }
}
