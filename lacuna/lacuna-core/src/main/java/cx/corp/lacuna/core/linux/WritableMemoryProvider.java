package cx.corp.lacuna.core.linux;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

/**
 * Provides write access to the memory of a process via a stream.
 */
@FunctionalInterface
public interface WritableMemoryProvider {
    /**
     * Opens a writable {@link SeekableByteChannel} to the memory of the specified process.
     *
     * @param pid The ID of the process.
     * @return a writable, non-null {@link SeekableByteChannel} to the memory of the specified process.
     * @throws IOException if the process does not exist, privileges are
     *                     insufficient or the channel cannot be opened for
     *                     other reasons.
     */
    SeekableByteChannel openWrite(int pid) throws IOException;
}
