package cx.corp.lacuna.core.linux;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import cx.corp.lacuna.core.ProcessOpenException;
import cx.corp.lacuna.core.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileMemoryProviderTest {

    private FileSystem fs;
    private FileMemoryProvider provider;
    private Path procRoot;

    @Before
    public void setUp() {
        fs = Jimfs.newFileSystem(Configuration.unix());
        procRoot = fs.getPath("/proc");
        provider = new FileMemoryProvider(procRoot);
    }

    @After
    public void tearDown() {
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException ex) {
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void constructorThrowsIfProcRootIsNull() {
        new FileMemoryProvider(null);
    }

    @Test(expected = ProcessOpenException.class)
    public void openReadThrowsIfProcRootDoesntExist() throws IOException {
        assertFalse(Files.exists(procRoot));
        provider.openRead(123);
    }

    @Test(expected = ProcessOpenException.class)
    public void openWriteThrowsIfProcRootDoesntExist() throws IOException {
        assertFalse(Files.exists(procRoot));
        provider.openWrite(123);
    }

    @Test(expected = ProcessOpenException.class)
    public void openReadThrowsIfProcessDoesntExist() throws IOException {
        Files.createDirectories(procRoot);
        Integer pid = 3551;
        assertFalse(Files.exists(procRoot.resolve(pid + "")));
        provider.openRead(pid);
    }

    @Test(expected = ProcessOpenException.class)
    public void openWriteThrowsIfProcessDoesntExist() throws IOException {
        Files.createDirectories(procRoot);
        Integer pid = 3551;
        assertFalse(Files.exists(procRoot.resolve(pid + "")));
        provider.openWrite(pid);
    }

    @Test
    public void readsCorrectDataFromCorrectFile() throws IOException {
        Integer pid = 5567;
        Path memFile = procRoot.resolve(pid + "").resolve("mem");
        byte[] inputData = TestUtils.generateRandomBytes(200);
        assertTrue(Files.notExists(memFile));
        Files.createDirectories(memFile.getParent());
        Files.write(memFile, inputData);

        SeekableByteChannel stream = provider.openRead(pid);
        ByteBuffer readBuffer = ByteBuffer.allocate(inputData.length);
        int bytesRead = stream.read(readBuffer);

        assertEquals(inputData.length, bytesRead);
        assertArrayEquals(inputData, readBuffer.array());
    }

    @Test
    public void writesDataCorrectlyToCorrectFile() throws IOException {
        Integer pid = 10001;
        Path memFile = procRoot.resolve(pid + "").resolve("mem");
        assertTrue(Files.notExists(memFile));
        Files.createDirectories(memFile.getParent());
        Files.createFile(memFile);

        byte[] bytesToWrite = TestUtils.generateRandomBytes(2048);
        SeekableByteChannel writeChannel = provider.openWrite(pid);
        writeChannel.write(ByteBuffer.wrap(bytesToWrite));

        byte[] writtenBytes = Files.readAllBytes(memFile);
        assertArrayEquals(bytesToWrite, writtenBytes);
    }
}
