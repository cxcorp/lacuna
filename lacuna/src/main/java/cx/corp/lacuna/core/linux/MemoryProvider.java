package cx.corp.lacuna.core.linux;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides access to the memory of a process via a stream.
 */
@FunctionalInterface
public interface MemoryProvider {
    /**
     * Opens a stream to the memory of the specified process.
     * @param pid The ID of the process.
     * @throws IOException if the stream cannot be opened.
     */
    InputStream open(int pid) throws IOException;
}
