package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.ProcessOpenException;

import java.nio.channels.SeekableByteChannel;

/**
 * Provides read access to the memory of a process via a stream.
 */
@FunctionalInterface
public interface ReadableMemoryProvider {
    /**
     * Opens a readable {@link SeekableByteChannel} to the memory of the specified process.
     *
     * @param pid The ID of the process.
     * @return a readable, non-null {@link SeekableByteChannel} to the memory of the specified process.
     * @throws ProcessOpenException if the process does not exist, privileges are
     *                              insufficient or the channel cannot be opened for
     *                              other reasons.
     */
    SeekableByteChannel openRead(int pid) throws ProcessOpenException;
}
