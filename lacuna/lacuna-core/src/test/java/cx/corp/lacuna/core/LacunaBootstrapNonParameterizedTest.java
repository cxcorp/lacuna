package cx.corp.lacuna.core;

import com.sun.jna.Platform;
import cx.corp.lacuna.core.linux.LinuxNativeProcessCollector;
import cx.corp.lacuna.core.windows.WindowsNativeProcessCollector;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class LacunaBootstrapNonParameterizedTest {

    @Before
    public void setUp() {
        // forWindows() calls fail without this
        Assume.assumeTrue(Platform.isWindows());
    }

    @Test
    public void forWindowsAndForLinuxReturnDifferent() {
        LacunaBootstrap linuxBootstrap = LacunaBootstrap.forLinux();
        LacunaBootstrap windowsBootstrap = LacunaBootstrap.forWindows();

        // only the NativeProcessCollectors are provided directly by the platform-specific
        // classes, others use an Impl class
        NativeProcessCollector linuxCollector = linuxBootstrap.getNativeProcessCollector();
        NativeProcessCollector winCollector = windowsBootstrap.getNativeProcessCollector();

        assertNotEquals(linuxCollector.getClass(), winCollector.getClass());
    }

    @Test
    public void forPlatformDetectedReturnsWindowsBootstrapCorrectly() {
        LacunaBootstrap.PlatformDetector detector = new LacunaBootstrap.PlatformDetector() {
            @Override
            public boolean isWindows() {
                return true;
            }
        };
        LacunaBootstrap bootstrap = LacunaBootstrap.forDetectedPlatform(detector);
        NativeProcessCollector collector = bootstrap.getNativeProcessCollector();

        assertEquals(collector.getClass(), WindowsNativeProcessCollector.class);
    }

    @Test
    public void forPlatformDetectedReturnsLinuxootstrapCorrectly() {
        LacunaBootstrap.PlatformDetector detector = new LacunaBootstrap.PlatformDetector() {
            @Override
            public boolean isWindows() {
                return false;
            }
        };
        LacunaBootstrap bootstrap = LacunaBootstrap.forDetectedPlatform(detector);
        NativeProcessCollector collector = bootstrap.getNativeProcessCollector();

        assertEquals(collector.getClass(), LinuxNativeProcessCollector.class);
    }

    @Test
    public void forCurrentPlatformDoesntReturnNull() { // kind of a worthless test but eh
        assertNotNull(LacunaBootstrap.forCurrentPlatform());
    }
}
