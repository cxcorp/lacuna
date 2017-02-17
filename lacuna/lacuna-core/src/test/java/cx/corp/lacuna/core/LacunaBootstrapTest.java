package cx.corp.lacuna.core;

import cx.corp.lacuna.core.linux.LinuxConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class LacunaBootstrapTest {

    @Parameterized.Parameter
    public LacunaBootstrap bootstrap;

    @Parameterized.Parameters
    public static Object[] data() {
        return new Object[]{
            LacunaBootstrap.forLinux(),
            LacunaBootstrap.forWindows()
        };
    }

    @Test
    public void bootstrapReturnsNonNullMemoryReader() {
        MemoryReader reader = bootstrap.getMemoryReader();
        assertNotNull(reader);
    }

    @Test
    public void bootstrapReturnsNonNullMemoryWriter() {
        MemoryWriter value = bootstrap.getMemoryWriter();
        assertNotNull(value);
    }

    @Test
    public void bootstrapReturnsNonNullNativeProcessEnumerator() {
        NativeProcessEnumerator value = bootstrap.getNativeProcessEnumerator();
        assertNotNull(value);
    }

    @Test
    public void bootstrapReturnsNonNullNativeProcessCollector() {
        NativeProcessCollector collector = bootstrap.getNativeProcessCollector();
        assertNotNull(collector);
    }
}
