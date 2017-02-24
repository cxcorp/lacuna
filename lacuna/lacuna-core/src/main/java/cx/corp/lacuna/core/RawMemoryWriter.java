package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

/**
 * Provides functionality for writing an array of bytes to a native process's
 * memory.
 */
public interface RawMemoryWriter {
    /**
     * Writes the specified bytes to the offset of the process's memory.
     * @param process the native process whose memory the data is written to.
     * @param offset the memory address offset at which the data is written to.
     *               This value is interpreted as an unsigned integer, meaning
     *               that negative values are allowed.
     * @param buffer the data to write.
     * @throws MemoryAccessException if writing fails due to, for eaxmple, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException if {@code process} or {@code buffer} is null.
     */
    void write(NativeProcess process, int offset, byte[] buffer) throws MemoryAccessException;
}
