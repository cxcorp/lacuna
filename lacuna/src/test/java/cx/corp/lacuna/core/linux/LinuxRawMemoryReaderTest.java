package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class LinuxRawMemoryReaderTest {

    private LinuxRawMemoryReader reader;
    private MemoryProvider memoryProvider;
    private NativeProcess process;

    @Before
    public void setUp() {
        // capture the local memoryProvider via a closure so we can change it in the unit tests
        MemoryProvider proxyProvider = pid -> memoryProvider.open(pid);
        reader = new LinuxRawMemoryReader(proxyProvider);
        process = new NativeProcessImpl();
        process.setPid(123);
        process.setDescription("ayy");
        process.setOwner("lmao");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfMemoryProviderIsNull() {
        new LinuxRawMemoryReader(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsWhenReadingFromNullProcess() {
        reader.read(null, 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsWhenReadingFromNegativeOffset() {
        reader.read(process, -100, 123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readThrowsWhenReadingZeroBytes() {
        reader.read(process, 0, 0);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsWhenReadingFromOutOfBoundsOffset() {
        int bytesInSource = 1;
        int offsetOverSource = 123;
        byte[] sourceBytes = new byte[bytesInSource];
        memoryProvider = process -> new ByteArrayInputStream(sourceBytes);

        reader.read(process, offsetOverSource, bytesInSource);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfAnExceptionOccursWhenOpeningMemoryProvider() {
        memoryProvider = process -> {
            throw new IOException();
        };

        reader.read(process, 0, 1);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfMemoryProviderReturnsNullStream() {
        memoryProvider = process -> null;

        reader.read(process, 0, 123);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfProvidedStreamCannotBeSkipped() {
        memoryProvider = proc -> new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public long skip(long n) {
                return -1; // -1 = fail
            }
        };

        reader.read(process, 10, 1);
    }

    @Test(expected = MemoryReadException.class)
    public void readThrowsIfProvidedStreamReadFails() {
        memoryProvider = proc -> new InputStream() {
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

        reader.read(process, 10, 1);
    }

    @Test
    public void readCorrectlyReadsAllBytesFromStart() {
        byte[] memoryBytes = generateRandomBytes(16);
        memoryProvider = p -> new ByteArrayInputStream(memoryBytes);

        ByteBuffer readBuffer = reader.read(process, 0, memoryBytes.length);
        byte[] readBytes = new byte[readBuffer.remaining()];
        readBuffer.get(readBytes);

        assertArrayEquals(memoryBytes, readBytes);
    }

    @Test
    public void readCorrectlyReadsSegmentOfBytesAtOffset() {
        byte[] memoryBytes = generateRandomBytes(16);
        memoryProvider = p -> new ByteArrayInputStream(memoryBytes);
        int offset = 10;
        byte[] expected = Arrays.copyOfRange(memoryBytes, offset, memoryBytes.length);

        ByteBuffer readBuffer = reader.read(
            process,
            offset,
            memoryBytes.length - offset);
        byte[] readBytes = new byte[readBuffer.remaining()];
        readBuffer.get(readBytes);

        assertArrayEquals(expected, readBytes);
    }

    private byte[] generateRandomBytes(int count) {
        byte[] buf = new byte[count];
        ThreadLocalRandom.current().nextBytes(buf);
        return buf;
    }
}