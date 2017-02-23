package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

/**
 * Provides methods for reading bytes from the memory of a native process
 * and constructing common Java types from them.
 *
 * @see NativeProcessEnumerator
 * @see RawMemoryReader
 */
public interface MemoryReader {
    /**
     * Reads a byte from the specified offset and interprets it as a boolean value.
     * Any nonzero value will be interpreted as true, whereas zero will be interpreted as false.
     *
     * @param process The native process whose memory to read.
     * @param offset  The memory address offset to read from.
     * @return True if the read byte is nonzero, otherwise false.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     */
    boolean readBoolean(NativeProcess process, int offset) throws MemoryAccessException;

    /**
     * Reads a byte from the specified offset.
     *
     * @param process The native process whose memory to read.
     * @param offset  The memory address offset to read from.
     * @return The read value.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     */
    byte readByte(NativeProcess process, int offset) throws MemoryAccessException;

    /**
     * Reads a byte from the specified offset and decodes the data using UTF-8.
     * <p>
     * <p>Notice that this method only cannot read multi-byte UTF-8 code points.
     * To read more than one byte at a time, see {@link #readStringUTF8(NativeProcess, int, int)}.
     *
     * @param process The native process whose memory to read.
     * @param offset  The memory address offset to read from.
     * @return The decoded byte.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @see #readCharUTF16LE
     * @see #readStringUTF8
     * @see #readStringUTF16LE
     */
    char readCharUTF8(NativeProcess process, int offset) throws MemoryAccessException;

    /**
     * Reads two bytes from the specified offset and decodes the data using
     * little-endian UTF-16.
     *
     * @param process The native process whose memory to read.
     * @param offset  The memory address offset to read from.
     * @return The decoded value.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @see #readCharUTF8
     * @see #readStringUTF16LE
     * @see #readStringUTF8
     */
    char readCharUTF16LE(NativeProcess process, int offset) throws MemoryAccessException;

    /**
     * Reads two bytes from the specified offset and
     * constructs a short integer from the data.
     *
     * @param process The native process whose memory to read.
     * @param offset  The memory address offset to read from.
     * @return The read value.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    short readShort(NativeProcess process, int offset) throws MemoryAccessException;

    /**
     * Reads four bytes from the specified offset and
     * constructs an integer from the data.
     *
     * @param process The native process whose memory to read.
     * @param offset  The memory address offset to read from.
     * @return The read value.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    int readInt(NativeProcess process, int offset) throws MemoryAccessException;

    /**
     * Reads four bytes from the specified offset and constructs
     * a single precision IEEE 754 floating point value from the data.
     *
     * @param process The native process whose memory to read.
     * @param offset  The memory address offset to read from.
     * @return The read value.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    float readFloat(NativeProcess process, int offset) throws MemoryAccessException;

    /**
     * Reads eight bytes from the specified offset and
     * constructs a long integer from the data.
     *
     * @param process The native process whose memory to read.
     * @param offset  The memory address offset to read from.
     * @return The read value.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    long readLong(NativeProcess process, int offset) throws MemoryAccessException;

    /**
     * Reads eight bytes from the specified offset and constructs
     * a double  precision IEEE 754 floating point value from the data.
     *
     * @param process The native process whose memory to read.
     * @param offset  The memory address offset to read from.
     * @return The read value.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    double readDouble(NativeProcess process, int offset) throws MemoryAccessException;

    /**
     * Reads 8-bit UTF-8 code units from the specified offset until either
     * a null character or the specified limit is met, then constructs a String
     * from the data.
     * <p>
     * <p>Notice that UTF-8 is a variable length character encoding, in which
     * Unicode code points higher than U+007F are encoded with two to four 8-bit
     * code units. This method does not synchronize reads to read a full code point,
     * so reads ending at the middle of a code point are possible.
     * <p>This method stops reading when the 8-bit pattern {@code 0x00} is met, or
     * when {@code maxCodeUnitsToRead} has been reached.
     *
     * @param process            The native process whose memory to read.
     * @param offset             The memory address offset to read from.
     * @param maxCodeUnitsToRead The maximum amount of 8-bit code units to read.
     * @return A non-null String consisting of the read characters.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @throws IllegalArgumentException if attempting to read less than one code unit.
     */
    String readStringUTF8(NativeProcess process, int offset, int maxCodeUnitsToRead) throws MemoryAccessException;

    /**
     * Reads two-byte UTF-16 little-endian code units from the specified offset
     * until either a null character or the specified limit is met, then
     * constructs a String from the data.
     * <p>
     * <p>Notice that UTF-16 is a variable length character encoding, in which
     * Unicode code points higher than U+10000 are encoded as two 16-bit surrogate pairs.
     * This method does not synchronize reads to read a full code point, so reads
     * ending before the trailing surrogate pair are possible.
     * <p>This method stops reading when the 16-bit pattern {@code 0x0000} is met, or
     * when {@code maxCodeUnitsToRead} has been reached.
     *
     * @param process            The native process whose memory to read.
     * @param offset             The memory address offset to read from.
     * @param maxCodeUnitsToRead The maximum amount of 16-bit code units to read.
     * @return A non-null String consisting of the read characters.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @throws IllegalArgumentException if attempting to read less than one code unit.
     */
    String readStringUTF16LE(NativeProcess process, int offset, int maxCodeUnitsToRead) throws MemoryAccessException;

    /**
     * Reads a region of memory from the specified offset.
     *
     * @param process     The native process whose memory to read.
     * @param offset      The memory address offset to read from.
     * @param bytesToRead The amount of bytes to read, starting from the {@code offset}.
     * @return The read bytes.
     * @throws MemoryAccessException if reading fails due to, for example, an
     *                               access violation or insufficient rights, or
     *                               if the read request is only partially
     *                               completed.
     * @throws NullPointerException  if {@code process} is null.
     * @throws IllegalArgumentException if attempting to read less than one byte.
     */
    byte[] read(NativeProcess process, int offset, int bytesToRead) throws MemoryAccessException;
}
