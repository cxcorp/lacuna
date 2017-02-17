package cx.corp.lacuna.core;

import com.sun.jna.Platform;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LacunaBootstrapTest {

    @Test
    public void linuxBootstrapReturnsNonNullMemoryReader() {
        MemoryReader reader = LacunaBootstrap.forLinux().getMemoryReader();
        assertNotNull(reader);
    }

    @Test
    public void windowsBootstrapReturnsNonNullMemoryReader() {
        assumeWindows();
        MemoryReader reader = LacunaBootstrap.forWindows().getMemoryReader();
        assertNotNull(reader);
    }

    @Test
    public void linuxBootstrapReturnsNonNullMemoryWriter() {
        MemoryWriter value = LacunaBootstrap.forLinux().getMemoryWriter();
        assertNotNull(value);
    }

    @Test
    public void windowsBootstrapReturnsNonNullMemoryWriter() {
        assumeWindows();
        MemoryWriter value = LacunaBootstrap.forWindows().getMemoryWriter();
        assertNotNull(value);
    }

    @Test
    public void linuxBootstrapReturnsNonNullNativeProcessEnumerator() {
        NativeProcessEnumerator value = LacunaBootstrap.forLinux().getNativeProcessEnumerator();
        assertNotNull(value);
    }

    @Test
    public void windowsBootstrapReturnsNonNullNativeProcessEnumerator() {
        assumeWindows();
        NativeProcessEnumerator value = LacunaBootstrap.forWindows().getNativeProcessEnumerator();
        assertNotNull(value);
    }

    @Test
    public void linuxBootstrapReturnsNonNullNativeProcessCollector() {
        NativeProcessCollector collector = LacunaBootstrap.forLinux().getNativeProcessCollector();
        assertNotNull(collector);
    }

    @Test
    public void windowsBootstrapReturnsNonNullNativeProcessCollector() {
        assumeWindows();
        NativeProcessCollector collector = LacunaBootstrap.forWindows().getNativeProcessCollector();
        assertNotNull(collector);
    }

    private static void assumeWindows() {
        Assume.assumeTrue(Platform.isWindows());
    }
}
