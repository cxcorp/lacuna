package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MemoryWriterImplTest {

    private ByteArrayRawMemoryWriter rawByteWriter;
    private RawMemoryWriter rawWriter;
    private MemoryWriterImpl writer;
    private NativeProcess process;

    @Before
    public void setUp() {
        // By default, use rawByteWriter to easily get the written byte array
        // after using one of writer's methods, use `rawByteWriter.getBytes()`
        // to get the all bytes that were written
        rawByteWriter = new ByteArrayRawMemoryWriter();
        // rawWriter exposed separately so that unit tests can just plug in
        // a lambda and replace rawByteWriter
        rawWriter = rawByteWriter;
        // use closure to let us change rawWriter in tests without having to
        // construct a new MemoryWriterImpl instance
        RawMemoryWriter proxy = (process, offset, bytes) -> rawWriter.write(process, offset, bytes);
        writer = new MemoryWriterImpl(proxy);
        process = new NativeProcessImpl(
            1234,
            NativeProcess.UNKNOWN_DESCRIPTION, // description/owner is not important
            NativeProcess.UNKNOWN_OWNER);
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullArgPassed() {
        writer = new MemoryWriterImpl(null);
    }

    @Test(expected = MemoryAccessException.class)
    public void writeBubblesUpRawWriterException() {
        rawWriter = (p, o, b) -> {
            throw new MemoryAccessException("fail");
        };
        writer.write(process, 12312, new byte[]{123});
    }

    @Test
    public void writeBooleanWritesOneWhenWritingTrue() {
        writer.writeBoolean(process, 0, true);

        byte[] written = rawByteWriter.getBytes();
        assertEquals(1, written.length);
        assertEquals(1, written[0]);
    }

    @Test
    public void writeBooleanWritesZeroWhenWritingFalse() {
        writer.writeBoolean(process, 0, false);

        byte[] written = rawByteWriter.getBytes();
        assertEquals(1, written.length);
        assertEquals(0, written[0]);
    }

    @Test
    public void writeBooleanWritesToOffsetCorrectly() {
        writer.writeBoolean(process, 0xBA1BA1, true);

        byte[] written = rawByteWriter.getBytes();
        assertEquals(1, written.length);
        assertEquals(1, written[0]);
    }

    @Test
    public void writeBooleanWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeBoolean(process, targetOffset, false);
    }

    @Test
    public void writeByteWritesAllBytesCorrectly() {
        byte[] bytesToWrite = new byte[0xFF];
        for (int i = 0; i < bytesToWrite.length; i++) {
            bytesToWrite[i] = (byte) i;
        }

        for (int i = 0; i < bytesToWrite.length; i++) {
            writer.writeByte(process, i, bytesToWrite[i]);
        }

        byte[] written = rawByteWriter.getBytes();
        assertArrayEquals(bytesToWrite, written);
    }

    @Test
    public void writeByteWritesToOffsetCorrectly() {
        byte data = 123;
        writer.writeByte(process, 0xF00F00, data);

        byte[] written = rawByteWriter.getBytes();
        assertEquals(1, written.length);
        assertEquals(data, written[0]);
    }

    @Test
    public void writeByteWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeByte(process, targetOffset, (byte) 1);
    }

    @Test
    public void writeCharWritesLowControlCharactersCorrectly() {
        byte[] expected = {'\n', '\t', '\b', 0x2, 0x15};
        char[] data = {'\n', '\t', '\b', '\u0002', '\u0015'};

        for (int i = 0; i < data.length; i++) {
            writer.writeCharUTF8(process, i, data[i]);
        }

        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeCharWritesNormalAsciiRangeStuffCorrectly() {
        String source = "liirum Laarum TOASTER quick$ort w0t 5619234 + 4123 - f23 * 14 / ,.-^[]//()}";
        byte[] expected = source.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < source.length(); i++) {
            writer.writeCharUTF8(process, i, source.charAt(i));
        }

        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeCharOnlyWritesOneByteWithMultiByteChars() {
        char[] source = {
            'ß', '߷', // U+0080 - U+07FF is encoded with two 8-bit code units
            'က', 'Ｎ', // U+0800 - U+FFFF is encoded with three
        };

        for (int i = 0; i < source.length; i++) {
            writer.writeCharUTF8(process, i, source[i]);
        }

        assertEquals(4, rawByteWriter.getBytes().length);
    }

    @Test
    public void writeCharWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeCharUTF8(process, targetOffset, 'a');
    }

    @Test
    public void writeCharUTF16WritesOneCodeUnitCharsCorrectly() {
        char[] chars = {'\n', 'a', 'Z', '5', '*', 'ä', 'ß', '߷', 'က', 'Ｎ'};
        byte[] expected = new String(chars).getBytes(StandardCharsets.UTF_16LE);

        for (int i = 0; i < chars.length; i++) {
            writer.writeCharUTF16LE(process, i, chars[i]);
        }

        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeCharUTF16WritesToRightOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeCharUTF16LE(process, targetOffset, 'z');
    }

    @Test
    public void writeShortWritesZeroCorrectly() {
        writer.writeShort(process, 0, (short) 0);
        byte[] expected = {0, 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeShortWritesOneCorrectly() {
        writer.writeShort(process, 0, (short) 1);
        byte[] expected = {1, 0}; // little endian
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeShortWritesMinusOneCorrectly() {
        writer.writeShort(process, 0, (short) -1);
        byte[] expected = {(byte) 0xFF, (byte) 0xFF};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeShortWritesCorrectly() {
        writer.writeShort(process, 0, (short) 1234);
        byte[] expected = {(byte) 0xD2, 0x04};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeShortWritesMaxSignedShortCorrectly() {
        writer.writeShort(process, 0, Short.MAX_VALUE);
        byte[] expected = {(byte) 0xFF, 0x7F};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeShortWritesMinSignedShortCorrectly() {
        writer.writeShort(process, 0, Short.MIN_VALUE);
        byte[] expected = {0, (byte) 0x80};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeShortWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeShort(process, targetOffset, (short) 1584);
    }

    @Test
    public void writeIntWritesZeroCorrectly() {
        writer.writeInt(process, 0, 0);
        byte[] expected = {0, 0, 0, 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeIntWritesOneCorrectly() {
        writer.writeInt(process, 0, 1);
        byte[] expected = {1, 0, 0, 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeIntWritesMinusOneCorrectly() {
        writer.writeInt(process, 0, -1);
        byte[] expected = {-1, -1, -1, -1};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeIntWritesCorrectly() {
        writer.writeInt(process, 0, 12581239);
        byte[] expected = {0x77, (byte) 0xF9, (byte) 0xBF, 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeIntWritesMaxSignedIntCorrectly() {
        writer.writeInt(process, 0, Integer.MAX_VALUE);
        byte[] expected = {-1, -1, -1, 0x7F};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeIntWritesMinSignedIntCorrectly() {
        writer.writeInt(process, 0, Integer.MIN_VALUE);
        byte[] expected = {0, 0, 0, (byte) 0x80};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeIntWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeInt(process, targetOffset, 51923918);
    }

    @Test
    public void writeLongWritesZeroCorrectly() {
        writer.writeLong(process, 0, 0);
        byte[] expected = {0, 0, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeLongWritesOneCorrectly() {
        writer.writeLong(process, 0, 1);
        byte[] expected = {1, 0, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeLongWritesMinusOneCorrectly() {
        writer.writeLong(process, 0, -1);
        byte[] expected = {-1, -1, -1, -1, -1, -1, -1, -1};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeLongWritesCorrectly() {
        writer.writeLong(process, 0, 941825812485729141L);
        byte[] expected = {0x75, 0x6B, (byte) 0x81, 0x21, (byte) 0x96, 0x09, 0x12, 0x0D};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeLongWritesMaxSignedLongCorrectly() {
        writer.writeLong(process, 0, Long.MAX_VALUE);
        byte[] expected = {-1, -1, -1, -1, -1, -1, -1, 0x7F};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeLongWritesMinSignedLongCorrectly() {
        writer.writeLong(process, 0, Long.MIN_VALUE);
        byte[] expected = {0, 0, 0, 0, 0, 0, 0, (byte) 0x80};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeLongWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeLong(process, targetOffset, 1239121924301245L);
    }

    @Test
    public void writeFloatWritesCorrectly() {
        writer.writeFloat(process, 0, 5123.412f);
        byte[] expected = {0x4C, 0x1B, (byte) 0xA0, 0x45};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeFloatWritesZeroCorrectly() {
        writer.writeFloat(process, 0, 0f);
        byte[] expected = {0, 0, 0, 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeFloatWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeFloat(process, targetOffset, 1234f);
    }

    @Test
    public void writeDoubleWritesCorrectly() {
        writer.writeDouble(process, 0, 2342.5125161243454d);
        byte[] expected = {0x79, 0x71, (byte) 0x83, 0x68, 0x06, 0x4D, (byte) 0xA2, 0x40};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeDoubleWritesZeroCorrectly() {
        writer.writeDouble(process, 0, 0d);
        byte[] expected = {0, 0, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeDoubleWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeDouble(process, targetOffset, 12315239d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeStringUTF8ThrowsIfWritingEmptyString() {
        writer.writeStringUTF8(process, 0, "");
    }

    @Test
    public void writeStringUTF8WritesOneCharStringCorrectly() {
        String source = "a";
        writer.writeStringUTF8(process, 0, source);
        assertArrayEquals(new byte[]{'a'}, rawByteWriter.getBytes());
    }

    @Test(expected = NullPointerException.class)
    public void writeStringUTF8ThrowsIfWritingNullString() {
        writer.writeStringUTF8(process, 0, null);
    }

    @Test
    public void writeStringUTF8WritesAsciiRangeString() {
        String source = "Quick 12345 toasters Ran m-,.-,.<>(={}}[7L'^\" ayy lmao";
        writer.writeStringUTF8(process, 0, source);
        assertArrayEquals(source.getBytes(StandardCharsets.UTF_8), rawByteWriter.getBytes());
    }

    @Test
    public void writeStringWritesStringWithNulls() {
        String source = "a\u0000y";
        writer.writeStringUTF8(process, 0, source);
        byte[] expected = {'a', 0, 'y'};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeStringWritesEmojiAndOtherMultiByteChars() {
        String source = "ß߷ကＮ😂";
        writer.writeStringUTF8(process, 0, source);
        byte[] expected = {-61, -97, -33, -73, -31, -128, -128, -17, -68, -82, -16, -97, -104, -126};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeStringWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeStringUTF8(process, targetOffset, "ayy lmao");
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeStringUTF16ThrowsIfStringIsEmpty() {
        writer.writeStringUTF16LE(process, 0, "");
    }

    @Test
    public void writeStringUTF16WritesOneCharStringCorrectly() {
        String source = "a";
        writer.writeStringUTF16LE(process, 0, source);
        assertArrayEquals(new byte[]{'a', 0}, rawByteWriter.getBytes());
    }

    @Test(expected = NullPointerException.class)
    public void writeStringUTF16ThrowsIfStringIsNull() {
        writer.writeStringUTF16LE(process, 0, null);
    }

    @Test
    public void writeStringUTF16WritesAsciiRangeString() {
        String source = "Quick 12345 toasters Ran m-,.-,.<>(={}}[7L'^\" ayy lmao";
        writer.writeStringUTF16LE(process, 0, source);
        assertArrayEquals(source.getBytes(StandardCharsets.UTF_16LE), rawByteWriter.getBytes());
    }

    @Test
    public void writeStringUTF16WritesStringWithNulls() {
        String source = "a\u0000y";
        writer.writeStringUTF16LE(process, 0, source);
        byte[] expected = {'a', 0, 0, 0, 'y', 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeStringUTF16WritesEmojiAndOtherMultiCodeUnitChars() {
        String source = "ß߷ကＮ😂";
        writer.writeStringUTF16LE(process, 0, source);
        byte[] expected = {-33, 0, -9, 7, 0, 16, 46, -1, 61, -40, 2, -34};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeStringUTF16LEWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.writeStringUTF16LE(process, targetOffset, "ayy lmao");
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeThrowsIfEmptyArrayPassed() {
        writer.write(process, 0, new byte[0]);
    }

    @Test(expected = NullPointerException.class)
    public void writeThrowsIfNullArrayPassed() {
        writer.write(process, 0, null);
    }

    @Test
    public void writeWritesOneByte() {
        byte[] data = {123};
        writer.write(process, 0, data);
        assertArrayEquals(data, rawByteWriter.getBytes());
    }

    @Test
    public void writeWritesSubsequentBytesCorrectly() {
        byte[] wholeData = {1, 2, 3, 4, 5};
        for (int i = 0; i < wholeData.length; i++) {
            writer.write(process, i, new byte[]{wholeData[i]});
        }
        assertArrayEquals(wholeData, rawByteWriter.getBytes());
    }

    @Test
    public void writeWritesBiggerPileOfData() {
        byte[] data = new byte[4096 * 2 * 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 0xFF);
        }
        writer.write(process, 0, data);
        assertArrayEquals(data, rawByteWriter.getBytes());
    }

    @Test
    public void writeWritesToCorrectOffset() {
        final int targetOffset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOffset, offset);
        };
        writer.write(process, targetOffset, new byte[]{123, 5, 91});
    }

    private static class ByteArrayRawMemoryWriter implements RawMemoryWriter {

        private List<Byte> buffer = new ArrayList<>();

        @Override
        public void write(NativeProcess process, int offset, byte[] buffer) throws MemoryAccessException {
            for (byte b : buffer) {
                this.buffer.add(b);
            }
        }

        public byte[] getBytes() {
            byte[] buf = new byte[buffer.size()];
            for (int i = 0; i < buf.length; i++) {
                buf[i] = buffer.get(i);
            }
            return buf;
        }
    }
}
