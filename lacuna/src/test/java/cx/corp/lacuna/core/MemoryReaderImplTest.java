package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
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
    public void readStringUTF8ReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.wrap("a".getBytes(StandardCharsets.UTF_8)); // non-null character
        };

        String read = reader.readStringUTF8(process, 0, 15);

        assertEquals(CHAR_UTF8_SIZE * 15, bytesTriedToRead.get());
    }

    @Test
    public void readStringUTF16LEReadsCorrectAmountOfBytes() {
        AtomicInteger bytesTriedToRead = new AtomicInteger(0);
        rawReader = (process, offset, bytesToRead) -> {
            bytesTriedToRead.addAndGet(bytesToRead);
            return ByteBuffer.wrap("a".getBytes(StandardCharsets.UTF_16LE)); // non-null character
        };

        String read = reader.readStringUTF16LE(process, 0, 15);

        assertEquals(CHAR_UTF16LE_SIZE * 15, bytesTriedToRead.get());
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
                return ByteBuffer.wrap(getNextChar().getBytes(StandardCharsets.UTF_16LE));
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
        rawReader = (proc, offset, bytesToRead) -> ByteBuffer.wrap(new byte[]{0});

        boolean result = reader.readBoolean(process, 0);

        assertFalse(result);
    }

    @Test
    public void readBooleanReadsAnyNonZeroAsTrue() {
        byte[] data = new byte[0xFF - 1];
        for (int i = 1; i < data.length; i++) {
            data[(i - 1)] = (byte) i;
        }

        rawReader = (p, o, b) -> ByteBuffer.wrap(data);

        for (int i = 0; i < data.length; i++) {
            boolean result = reader.readBoolean(process, i);
            assertTrue("Expected " + data[i] + " to be truthy!", result);
        }
    }

    @Test
    public void readBooleanReadsBooleanAtOffset() {
        rawReader = (p, o, b) -> ByteBuffer.wrap(new byte[]{1});

        boolean result = reader.readBoolean(process, 0xFFEFEF);

        assertTrue(result);
    }

    @Test
    public void readCharUTF8ReadsChar() {
        String text = "Toaster";
        rawReader = (p, o, b) -> ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));

        char result = reader.readCharUTF8(process, 0);

        assertEquals(text.charAt(0), result);
    }

    @Test
    public void readCharUTF8ReadsCharAtOffset() {
        String text = "Quads";
        rawReader = (p, o, b) -> ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));

        char result = reader.readCharUTF8(process, 0xBEEF);

        assertEquals(text.charAt(0), result);
    }

    @Test
    public void readCharUTF8ReadsAllSingleCodeUnitCharsCorrectly() {
        byte[] bytes = new byte[0b0111_1111];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }
        rawReader = (process, offset, bytesToRead) ->
            ByteBuffer.wrap(Arrays.copyOfRange(bytes, offset, offset + 1));

        for (int i = 0; i < bytes.length; i++) {
            char expected = new String(bytes, i, 1, StandardCharsets.UTF_8).charAt(0);
            char result = reader.readCharUTF8(process, i);
            assertEquals(expected, result);
        }
    }

    @Test
    public void readCharUTF16LEReadsChar() {
        String text = "Toaster";
        rawReader = (p, o, b) -> ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_16LE));

        char result = reader.readCharUTF8(process, 0);

        assertEquals(text.charAt(0), result);
    }

    @Test
    public void readCharUTF16LEReadsCharAtOffset() {
        String text = "Quads";
        rawReader = (p, o, b) -> ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_16LE));

        char result = reader.readCharUTF8(process, 0xBEEF);

        assertEquals(text.charAt(0), result);
    }
}
