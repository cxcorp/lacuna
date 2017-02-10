package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.nio.ByteBuffer;

/**
 * Provides functionality to read a region of bytes from a native process's memory.
 */
public interface RawMemoryReader {
    /**
     * Reads the specified amount of bytes from the specified offset at the native
     * process' memory.
     * @param process The native process whose memory to read.
     * @param offset The memory address offset to read from. This value is
     *               interpreted as an unsigned value, meaning that negative
     *               values are allowed.
     * @param bytesToRead The amount of bytes to read.
     * @return The read bytes.
     * @throws MemoryReadException if reading fails due to, for example, an
     *                             access violation or insufficient rights.
     * @throws NullPointerException if {@code process} is null.
     * @throws IllegalArgumentException if {@code bytesToRead} is negative.
     */
    ByteBuffer read(NativeProcess process, int offset, int bytesToRead);
}
