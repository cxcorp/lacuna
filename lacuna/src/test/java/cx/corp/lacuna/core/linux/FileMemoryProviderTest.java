package cx.corp.lacuna.core.linux;

import com.sun.jna.Native;
import cx.corp.lacuna.core.NativeProcess;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileMemoryProviderTest {

    private FileMemoryProvider provider;

    @Before
    public void setUp() {
        provider = new FileMemoryProvider(Paths.get("proc"), "mem");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfProcRootIsNull() {
        new FileMemoryProvider(null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfMemFileNameIsNull() {
        new FileMemoryProvider(Paths.get("."), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void openThrowsIfProcessIsNull() throws IOException {
        provider.open(null);
    }

    @Test(expected = IOException.class)
    public void openThrowsIfProcessMemFileDoesntExist() throws IOException {
        NativeProcess proc = new NativeProcess();
        proc.setPid(-1);
        provider.open(proc);
    }
}
