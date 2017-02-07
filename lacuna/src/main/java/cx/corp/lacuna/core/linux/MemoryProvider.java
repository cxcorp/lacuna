package cx.corp.lacuna.core.linux;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface MemoryProvider {
    InputStream open(int pid) throws IOException;
}
