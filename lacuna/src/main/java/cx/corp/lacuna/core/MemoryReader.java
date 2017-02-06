package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

/**
 * The {@link MemoryReader} interface provides methods for reading bytes from the memory
 * of a {@link NativeProcess}.
 *
 * @see cx.corp.lacuna.core.NativeProcessEnumerator
 */
public interface MemoryReader {
    /**
     * Reads an array of bytes from the memory of the specified {@link NativeProcess}.
     * <p>
     * <p>This method may return a byte array with fewer elements than {@code bytesToRead}.
     * The return value will always be a non-null byte array with a length between
     * {@code 0} and {@code bytesToRead}.
     *
     * @param process     The native process whose memory to read.
     * @param offset      The memory address offset to read from.
     * @param bytesToRead The amount of bytes to read, starting from the {@code offset}.
     * @return The read bytes.
     * @throws MemoryReadException if reading the memory fails.
     */
    byte[] read(NativeProcess process, int offset, int bytesToRead);
}
