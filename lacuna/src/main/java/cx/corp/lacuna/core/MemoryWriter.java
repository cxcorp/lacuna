package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

/**
 * Provides methods for writing bytes and common Java data types to the
 * memory of a native process.
 *
 * @see NativeProcessEnumerator
 * @see MemoryReader
 */
public interface MemoryWriter {
    /**
     * Writes an 8-bit boolean value to the specified offset.
     * True will be written as a {@code 1}, and false as a {@code 0}.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   THe value to write to memory. True will write 1, false will write 0.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     */
    void writeBoolean(NativeProcess process, int offset, boolean value) throws MemoryAccessException;

    /**
     * Writes a byte to the specified offset.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     */
    void writeByte(NativeProcess process, int offset, byte value) throws MemoryAccessException;

    /**
     * Writes one 8-bit UTF-8 code unit to the specified offset.
     * <p>Notice that this method only writes one byte, so characters which
     * are encoded into multiple bytes are not supported. If attempting to
     * write characters higher than U+007F, this method will throw an exception.
     * To write multi-byte characters, use {@link #writeStringUTF8}.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException    if writing fails due to, for example, an
     *                                  access violation or insufficient rights.
     * @throws NullPointerException     if {@code process} is null.
     * @throws IllegalArgumentException if the character is encoded into multiple bytes.
     * @see #writeCharUTF16LE
     * @see #writeStringUTF8
     * @see #writeStringUTF16LE
     */
    void writeCharUTF8(NativeProcess process, int offset, char value) throws MemoryAccessException;

    /**
     * Writes one 16-bit UTF-16 little endian code unit to the specified offset.
     * <p>Notice that this method only writes one 16-bit code unit, so characters
     * which are encoded with two surrogate units are not supported. If attempting
     * to write characters higher than U+FFFFF, this method will throw an exception.
     * To write multiple code units, use {@link #writeStringUTF16LE}
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException    if writing fails due to, for example, an
     *                                  access violation or insufficient rights.
     * @throws NullPointerException     if {@code process} is null.
     * @throws IllegalArgumentException if the character is encoded into multiple bytes.
     * @see #writeCharUTF8
     * @see #writeStringUTF16LE
     * @see #writeStringUTF8
     */
    void writeCharUTF16LE(NativeProcess process, int offset, char value) throws MemoryAccessException;

    /**
     * Writes a little-endian 16-bit short integer to the specified offset.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    void writeShort(NativeProcess process, int offset, short value) throws MemoryAccessException;

    /**
     * Writes a little-endian 32-bit integer to the specified offset.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    void writeInt(NativeProcess process, int offset, int value) throws MemoryAccessException;

    /**
     * Writes an 32-bit IEEE 754 single precision floating point value
     * to the specified offset.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    void writeFloat(NativeProcess process, int offset, float value) throws MemoryAccessException;

    /**
     * Writes a little-endian 64-bit integer to the specified offset.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    void writeLong(NativeProcess process, int offset, long value) throws MemoryAccessException;

    /**
     * Writes an 64-bit IEEE 754 double precision floating point value
     * to the specified offset.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} is null.
     * @cx.littleendian
     */
    void writeDouble(NativeProcess process, int offset, double value) throws MemoryAccessException;

    /**
     * Writes an UTF-8 encoded string to the specified offset.
     * <p>Notice that UTF-8 is a variable length character encoding, in which
     * Unicode code points higher than U+007F are encoded with two to four 8-bit
     * code units. Users should be careful when writing characters which use multiple
     * code units, as {@link String#length()} returns the amount of UTF-16 code units.
     * Use {@code String.getBytes(StandardCharsets.UTF_8)} to retrieve the amount of
     * bytes written by this method.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} or {@code value} is null.
     */
    void writeStringUTF8(NativeProcess process, int offset, String value) throws MemoryAccessException;

    /**
     * Writes an UTF-16 little-endian encoded string to the specified offset.
     * <p>Notice that UTF-16 is a variable length character encoding, in which
     * Unicode code points higher than U+FFFF are encoded with two 16-bit surrogate
     * code units. Users should be careful when writing characters which use multiple
     * code units, as {@link String#length()} returns the amount of 16-bit UTF-16 code units.
     * Use {@code String.getBytes(StandardCharsets.UTF_16LE)} to retrieve the amount of
     * bytes written by this method.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param value   The value to write to memory.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} or {@code value} is null.
     */
    void writeStringUTF16LE(NativeProcess process, int offset, String value) throws MemoryAccessException;

    /**
     * Writes an array of bytes to the specified offset.
     *
     * @param process The native process whose memory to write to.
     * @param offset  The memory address offset to write to.
     * @param values   The value to write to memory.
     * @throws MemoryAccessException if writing fails due to, for example, an
     *                               access violation or insufficient rights.
     * @throws NullPointerException  if {@code process} or {@code values} is null.
     */
    void write(NativeProcess process, int offset, byte[] values) throws MemoryAccessException;
}
