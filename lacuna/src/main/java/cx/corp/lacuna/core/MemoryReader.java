package cx.corp.lacuna.core;

public interface MemoryReader {
    /** Reads an array of bytes from the memory of the specified {@link NativeProcess}.
     *
     * @param process The native process whose memory to read.
     * @param offset The memory address offset to read from.
     * @param bytesToRead The amount of bytes to read, starting from the {@code offset}.
     * @return The read bytes.
     * @see cx.corp.lacuna.core.NativeProcessEnumerator
     */
    byte[] read(NativeProcess process, int offset, int bytesToRead);
}
