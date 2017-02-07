package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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

    @Test(expected = IOException.class)
    public void openThrowsIfProcessMemFileDoesntExist() throws IOException {
        provider.open(-1);
    }
}
