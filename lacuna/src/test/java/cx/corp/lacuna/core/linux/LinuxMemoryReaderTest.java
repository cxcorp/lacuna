package cx.corp.lacuna.core.linux;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import cx.corp.lacuna.core.MemoryReadException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;

public class LinuxMemoryReaderTest {

    private LinuxMemoryReader reader;

    @Before
    public void setUp() {
        reader = new LinuxMemoryReader(Paths.get("."));
    }

    @Test(expected = MemoryReadException.class)
    public void readingFromOffsetOutOfBoundsThrows() {
        int bytesInSource = 1;
        int offsetOverSource = 123;
        ByteArrayInputStream input = new ByteArrayInputStream(new byte[bytesInSource]);
        reader.read(input, offsetOverSource, bytesInSource);
    }
}
