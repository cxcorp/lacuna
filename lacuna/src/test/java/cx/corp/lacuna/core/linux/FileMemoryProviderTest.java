package cx.corp.lacuna.core.linux;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FileMemoryProviderTest {

    private FileSystem fs;
    private FileMemoryProvider provider;
    private Path procRoot;
    private Path relativeMemPath;

    @Before
    public void setUp() {
        fs = Jimfs.newFileSystem(Configuration.unix());
        procRoot = fs.getPath("/proc");
        relativeMemPath = fs.getPath("mem");
        provider = new FileMemoryProvider(procRoot, relativeMemPath);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfProcRootIsNull() {
        new FileMemoryProvider(null, relativeMemPath);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfMemPathIsNull() {
        new FileMemoryProvider(procRoot, null);
    }

    @Test(expected = IOException.class)
    public void openThrowsIfProcessMemFileDoesntExist() throws IOException {
        assertFalse(Files.exists(procRoot));
        provider.openRead(123);
    }

    @Test
    public void providesRightData() throws IOException {
        Integer pid = 5567;
        Path memFile = procRoot.resolve(pid.toString()).resolve(relativeMemPath);
        byte[] inputData = {0, 1, 41, 51, 126, -41, -1, 42, 0, 0, -45};
        Files.createDirectories(memFile.getParent());
        Files.write(memFile, inputData);

        SeekableByteChannel stream = provider.openRead(pid);
        ByteBuffer readBuffer = ByteBuffer.allocate(inputData.length);
        int bytesRead = stream.read(readBuffer);

        assertEquals(inputData.length, bytesRead);
        assertArrayEquals(inputData, readBuffer.array());
    }
}
