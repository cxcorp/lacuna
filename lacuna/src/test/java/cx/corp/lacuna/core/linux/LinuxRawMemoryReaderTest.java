package cx.corp.lacuna.core.linux;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.TestUtils;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LinuxRawMemoryReaderTest {

    private LinuxRawMemoryReader reader;
    private ReadableMemoryProvider readableMemoryProvider;
    private NativeProcess process;
    private FileSystem fs;
    private Path tempFile;

    @Before
    public void setUp() {
        fs = Jimfs.newFileSystem(Configuration.unix());
        tempFile = fs.getPath("tempfile");
        readableMemoryProvider = pid -> Files.newByteChannel(tempFile, StandardOpenOption.READ);
        // capture the local readableMemoryProvider via a closure so we can change it in the unit tests
        ReadableMemoryProvider proxyProvider = pid -> readableMemoryProvider.openRead(pid);
        reader = new LinuxRawMemoryReader(proxyProvider);
        process = new NativeProcessImpl();
        process.setPid(123);
        process.setDescription("ayy");
        process.setOwner("lmao");
    }

    @After
    public void tearDown() {
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException e) {
            }
        }
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
    public void readThrowsWhenReadingZeroBytes() {
        reader.read(process, 0, 0);
    }

    @Test(expected = MemoryAccessException.class)
    public void readThrowsWhenReadingFromOutOfBoundsOffset() throws IOException {
        int bytesInSource = 1;
        int offsetOverSource = 123;
        byte[] sourceBytes = new byte[bytesInSource];
        Files.write(tempFile, sourceBytes);

        reader.read(process, offsetOverSource, bytesInSource);
    }

    @Test(expected = MemoryAccessException.class)
    public void readThrowsIfAnExceptionOccursWhenOpeningMemoryProvider() {
        readableMemoryProvider = process -> {
            throw new IOException();
        };

        reader.read(process, 0, 1);
    }

    @Test(expected = MemoryAccessException.class)
    public void readThrowsIfReadCanOnlyBeCompletedPartially() throws IOException {
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        Files.write(tempFile, data);

        reader.read(process, data.length - 5, data.length);
    }

    @Test
    public void readCorrectlyReadsOneByteFromStart() throws IOException {
        byte[] data = TestUtils.generateRandomBytes(123);
        Files.write(tempFile, data);

        ByteBuffer buffer = reader.read(process, 0, 1);
        assertEquals(1, buffer.limit());
        assertEquals(data[0], buffer.get());
    }

    @Test
    public void readCorrectlyReadsLastByte() throws IOException {
        byte[] data = TestUtils.generateRandomBytes(4096 * 2 * 2);
        Files.write(tempFile, data);

        ByteBuffer buffer = reader.read(process, data.length - 1, 1);
        assertEquals(1, buffer.limit());
        assertEquals(data[data.length - 1], buffer.get());
    }

    @Test
    public void readCorrectlyReadsAllBytesFromStartToEnd() throws IOException {
        byte[] memoryBytes = TestUtils.generateRandomBytes(4096 * 16);
        Files.write(tempFile, memoryBytes);

        ByteBuffer readBuffer = reader.read(process, 0, memoryBytes.length);
        byte[] readBytes = new byte[readBuffer.remaining()];
        readBuffer.get(readBytes);

        assertArrayEquals(memoryBytes, readBytes);
    }

    @Test
    public void readCorrectlyReadsSegmentOfBytesAtOffset() throws IOException {
        byte[] memoryBytes = TestUtils.generateRandomBytes(16);
        Files.write(tempFile, memoryBytes);
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
}