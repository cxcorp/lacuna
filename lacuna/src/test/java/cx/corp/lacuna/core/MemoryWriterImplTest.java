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

    @Test(expected = IllegalArgumentException.class)
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
        final int targetOFfset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOFfset, offset);
        };
        writer.writeBoolean(process, targetOFfset, false);
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
        final int targetOFfset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOFfset, offset);
        };
        writer.writeByte(process, targetOFfset, (byte) 1);
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
        final int targetOFfset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOFfset, offset);
        };
        writer.writeCharUTF8(process, targetOFfset, 'a');
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
        final int targetOFfset = 0xBADF00D;
        rawWriter = (p, offset, b) -> {
            assertEquals(targetOFfset, offset);
        };
        writer.writeCharUTF16LE(process, targetOFfset, 'z');
    }

    @Test
    public void writeShortWritesZeroCorrectly() {
        writer.writeShort(process, 0, (short) 0);
        byte[] expected = {0, 0};
        assertArrayEquals(expected, rawByteWriter.getBytes());
    }

    @Test
    public void writeShortWritesOneCorrectlyInLittleEndian() {
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

    // int

    // float

    // long

    // double

    // utf-8 string

    // utf-16le string

    // bytes

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
