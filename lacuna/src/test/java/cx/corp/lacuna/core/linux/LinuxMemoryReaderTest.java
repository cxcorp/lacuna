package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.NativeProcess;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class LinuxMemoryReaderTest {

    private LinuxMemoryReader reader;
    private NativeProcess process;

    @Before
    public void setUp() {
        reader = new LinuxMemoryReader(p -> new ByteArrayInputStream(new byte[256]));
        process = new NativeProcess();
        process.setPid(123);
        process.setDescription("ayy");
        process.setOwner("lmao");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfMemoryProviderIsNull() {
        new LinuxMemoryReader(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsWhenReadingFromNullProcess() {
        reader.read(null, 0, 1);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsWhenReadingFromOutOfBoundsOffset() {
        int bytesInSource = 1;
        int offsetOverSource = 123;
        byte[] sourceBytes = new byte[bytesInSource];
        reader = new LinuxMemoryReader(process -> new ByteArrayInputStream(sourceBytes));

        reader.read(process, offsetOverSource, bytesInSource);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfAnExceptionOccursWhenOpeningMemoryProvider() {
        reader = new LinuxMemoryReader(process -> {
            throw new IOException();
        });

        reader.read(process, 0, 1);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfMemoryProviderReturnsNullStream() {
        reader = new LinuxMemoryReader(process -> null);

        reader.read(process, 0, 123);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfProvidedStreamCannotBeSkipped() {
        MemoryProvider provider = proc -> new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public long skip(long n) {
                return -1; // -1 = fail
            }
        };
        reader = new LinuxMemoryReader(provider);

        reader.read(process, 10, 1);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfProvidedStreamReadFails() {
        MemoryProvider provider = proc -> new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public int read(byte[] buf, int off, int len) {
                return -1; // fail
            }

            @Override
            public long skip(long n) {
                return n;
            }
        };
        reader = new LinuxMemoryReader(provider);

        reader.read(process, 10, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsWhenReadingFromNegativeOffset() {
        reader.read(process, -100, 123);
    }

    @Test
    public void readCorrectlyReadsZeroBytesFromStart() {
        byte[] memoryBytes = generateRandomBytes(16);
        reader = new LinuxMemoryReader(p -> new ByteArrayInputStream(memoryBytes));

        byte[] readBytes = reader.read(process, 0, 0);
        assertTrue(readBytes.length == 0);
    }

    @Test
    public void readCorrectlyReadsZeroBytesFromStartWhenStreamOffersMore() {
        byte[] memoryBytes = generateRandomBytes(16);
        reader = new LinuxMemoryReader(p -> new ByteArrayInputStream(memoryBytes));

        byte[] readBytes = reader.read(process, 0, 1);
        byte[] expected = Arrays.copyOf(memoryBytes, 1);
        assertArrayEquals(expected, readBytes);
    }

    @Test
    public void readCorrectlyReadsAllBytesFromStart() {
        byte[] memoryBytes = generateRandomBytes(16);
        reader = new LinuxMemoryReader(p -> new ByteArrayInputStream(memoryBytes));

        byte[] readBytes = reader.read(process, 0, memoryBytes.length);
        assertArrayEquals(memoryBytes, readBytes);
    }

    @Test
    public void readCorrectlyReadsSegmentOfBytesAtOffset() {
        byte[] memoryBytes = generateRandomBytes(16);
        reader = new LinuxMemoryReader(p -> new ByteArrayInputStream(memoryBytes));

        int offset = 10;
        byte[] readBytes = reader.read(
            process,
            offset,
            memoryBytes.length - offset);
        byte[] expected = Arrays.copyOfRange(memoryBytes, offset, memoryBytes.length);
        assertArrayEquals(expected, readBytes);
    }

    private byte[] generateRandomBytes(int count) {
        byte[] buf = new byte[count];
        ThreadLocalRandom.current().nextBytes(buf);
        return buf;
    }
}