package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.nio.ByteBuffer;

/**
 * Provides functionality for reading a region of bytes from a native process's
 * memory.
 */
public interface RawMemoryReader {
    /**
     * Reads the specified amount of bytes from the specified offset at the native
     * process' memory.
     * @param process the native process whose memory will be read.
     * @param offset the memory address offset to read from. This value is
     *               interpreted as an unsigned value, meaning that negative
     *               values are allowed.
     * @param bytesToRead the amount of bytes to read.
     * @return the read bytes.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights, or
     *                               if the read request was only partially completed.
     * @throws NullPointerException if {@code process} is null.
     * @throws IllegalArgumentException if {@code bytesToRead} is negative.
     */
    ByteBuffer read(NativeProcess process, int offset, int bytesToRead) throws MemoryAccessException;
}
