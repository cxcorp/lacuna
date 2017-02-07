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
     * Reads an 8-bit boolean value from the specified offset.
     * Any nonzero value will be interpreted as true, whereas zero will be interpreted as false.
     * @param process The native process whose memory to read.
     * @param offset The memory address offset to read from.
     * @return True if the read byte is nonzero, otherwise false.
     */
    boolean readBoolean(NativeProcess process, int offset); // 1-byte
    byte readByte(NativeProcess process, int offset); // 1-byte
    char readChar(NativeProcess process, int offset); // 1-byte
    char readWChar(NativeProcess process, int offset); // 2-byte, utf-16
    short readShort(NativeProcess process, int offset); // 2-byte
    int readInt(NativeProcess process, int offset); // 4-byte
    float readFloat(NativeProcess process, int offset); // 4-byte
    long readLong(NativeProcess process, int offset); // 8-byte
    double readDouble(NativeProcess process, int offset); // 8-byte
    String readString(NativeProcess process, int offset, int maxByteLength); // until a null is met or maxlength met, utf-8
    String readWString(NativeProcess process, int offset, int maxByteLength); // until a null is met or maxlength met, utf-16

    /**
     * Reads a region of memory of the specified {@link NativeProcess}.
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
