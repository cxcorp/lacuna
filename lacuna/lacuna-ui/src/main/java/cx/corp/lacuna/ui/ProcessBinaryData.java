package cx.corp.lacuna.ui;

import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.MemoryWriter;
import cx.corp.lacuna.core.ProcessOpenException;
import cx.corp.lacuna.core.domain.NativeProcess;
import org.apache.commons.lang3.NotImplementedException;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.ByteArrayEditableData;
import org.exbin.utils.binary_data.EditableBinaryData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.function.Consumer;

// no docs for EditableBinaryData for some reason, accessible here:
// https://github.com/exbin/exbin-utils-java/blob/46d00e1c832163d957cdea0670c7d783776bb55c/modules/exbin-binary_data/src/main/java/org/exbin/utils/binary_data/EditableBinaryData.java
public class ProcessBinaryData implements EditableBinaryData {

    private static final long MEMORY_ACCESS_TIMEOUT_MS = 500;
    private static final byte NULL_BYTE = 0;
    private static final byte REMOVE_REPLACEMENT = NULL_BYTE;

    private final ExceptionEaterProxy<MemoryWriter> writer;
    private final ExceptionEaterProxy<MemoryReader> reader;
    private NativeProcess process;

    public ProcessBinaryData(MemoryReader reader, MemoryWriter writer) {
        this.reader = createMemoryAccessProxy(reader);
        this.writer = createMemoryAccessProxy(writer);
    }

    private <T> ExceptionEaterProxy<T> createMemoryAccessProxy(T object) {
        InvocationProxy<T> proxy = new PassthroughProxy<>(object);
        ExceptionEaterProxy<T> eaterProxy = new ExceptionEaterProxy<>(
            proxy,
            MemoryAccessException.class,
            ProcessOpenException.class
        );

        // If we get a `ProcessOpenException`, we can assume that subsequent reads
        // will fail as well so mark the current process as dead and prevent
        // subsequent reads!
        Consumer<Exception> processDeadListener = ex -> this.process = null;
        eaterProxy.addEatListener(ProcessOpenException.class, processDeadListener);

        return eaterProxy;
    }

    public void setActiveProcess(NativeProcess process) {
        this.process = process;
    }

    public void setMemoryAccessExceptionHandler(Class<? extends Exception> type, Consumer<Exception> handler) {
        writer.addEatListener(type, handler);
        reader.addEatListener(type, handler);
    }

    @Override
    public void setDataSize(long newSize) {
        // no-op
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException if attempting to write to offsets higher than
     *                                       {@link Integer#MAX_VALUE}. 64-bit addresses may be
     *                                       supported in a future release.
     */
    @Override
    public void setByte(long offset, byte b) throws MemoryAccessException {
        throwIfOffsetOver32Bit(offset);
        if (process == null) {
            return;
        }

        writer.invoke(w -> w.writeByte(process, toLacunaOffset(offset), b));
    }

    @Override
    public void insertUninitialized(long offset, long length) {
        // can't "insert" into process memory
    }

    @Override
    public void insert(long offset, long length) {
        // can't "insert" into process memory
    }

    @Override
    public void insert(long offset, byte[] bytes) {
        replace(offset, bytes);
    }

    @Override
    public void insert(long offset, byte[] bytes, int dataOffset, int length) {
        replace(offset, bytes, dataOffset, length);
    }

    @Override
    public void insert(long offset, BinaryData binaryData) {
        replace(offset, binaryData);
    }

    @Override
    public void insert(long offset, BinaryData binaryData, long dataOffset, long length) {
        replace(offset, binaryData, dataOffset, length);
    }

    @Override
    public long insert(long l, InputStream inputStream, long length) throws IOException {
        // can't "insert" into process memory
        return 0;
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException if attempting to write more than {@link Integer#MAX_VALUE}
     *                                       bytes, or attempting to write to offsets higher than
     *                                       {@link Integer#MAX_VALUE}. Buffered writes and 64-bit
     *                                       addresses may be supported in a future release.
     */
    @Override
    public void replace(long offset, BinaryData binaryData) throws MemoryAccessException {
        throwIfOffsetOver32Bit(offset);
        throwIfCopyingTooManyBytes(binaryData.getDataSize());
        if (process == null) {
            return;
        }

        byte[] buffer = new byte[(int) binaryData.getDataSize()]; // plz forgive, no time for buffering
        binaryData.copyToArray(0, buffer, 0, buffer.length);
        writer.invoke(w -> w.write(process, toLacunaOffset(offset), buffer));
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException if attempting to write more than {@link Integer#MAX_VALUE}
     *                                       bytes, or attempting to write to offsets higher than
     *                                       {@link Integer#MAX_VALUE}. Buffered writes and 64-bit
     *                                       addresses may be supported in a future release.
     */
    @Override
    public void replace(long offset, BinaryData replacingData, long startFrom, long length) {
        throwIfOffsetOver32Bit(offset);
        throwIfCopyingTooManyBytes(length);
        if (process == null) {
            return;
        }

        byte[] buffer = new byte[(int) length];
        replacingData.copyToArray(startFrom, buffer, 0, (int) length);
        writer.invoke(w -> w.write(process, toLacunaOffset(offset), buffer));
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException if attempting to write to offsets higher than
     *                                       {@link Integer#MAX_VALUE}. 64-bit addresses may be
     *                                       supported in a future release.
     */
    @Override
    public void replace(long offset, byte[] bytes) {
        throwIfOffsetOver32Bit(offset);
        if (process == null) {
            return;
        }

        writer.invoke(w -> w.write(process, toLacunaOffset(offset), bytes));
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException if attempting to write to offsets higher than
     *                                       {@link Integer#MAX_VALUE}. 64-bit addresses may be
     *                                       supported in a future release.
     */
    @Override
    public void replace(long offset, byte[] replacingData, int replacingDataOffset, int length) {
        throwIfOffsetOver32Bit(offset);
        byte[] data = Arrays.copyOfRange(replacingData, replacingDataOffset, replacingDataOffset + length);
        writer.invoke(w -> w.write(process, toLacunaOffset(offset), data));
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException if attempting to write more than {@link Integer#MAX_VALUE}
     *                                       bytes, or attempting to write to offsets higher than
     *                                       {@link Integer#MAX_VALUE}. Buffered writes and 64-bit
     *                                       addresses may be supported in a future release.
     */
    @Override
    public void fillData(long offset, long length) {
        fillData(offset, length, NULL_BYTE);
    }

    /**
     * Fills a buffer of length {@code length} with the byte {@code b}, then writes
     * it to the offset {@code offset}.
     * {@inheritDoc}
     * @throws UnsupportedOperationException if attempting to write more than {@link Integer#MAX_VALUE}
     *                                       bytes, or attempting to write to offsets higher than
     *                                       {@link Integer#MAX_VALUE}. Buffered writes and 64-bit
     *                                       addresses may be supported in a future release.
     */
    @Override
    public void fillData(long offset, long length, byte b) {
        throwIfOffsetOver32Bit(offset);
        throwIfCopyingTooManyBytes(length);
        if (process == null) {
            return;
        }

        System.out.printf("filldata %d offset, len: %d\n", offset, length);
        byte[] data = new byte[(int) length];
        Arrays.fill(data, b);
        writer.invoke(w -> w.write(process, toLacunaOffset(offset), data));
    }

    @Override
    public void remove(long offset, long length) {
        fillData(offset, length, REMOVE_REPLACEMENT);
    }

    @Override
    public void clear() {
        // nah, can't do that with memory
    }

    @Override
    public void loadFromStream(InputStream inputStream) throws IOException {
        // nah, won't do that with memory
    }

    @Override
    public OutputStream getDataOutputStream() {
        throw new NotImplementedException("");
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getDataSize() {
        return process == null ? 0 : 0xFFFFFFFFL;
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException if attempting to read from offsets higher than
     *                                       {@link Integer#MAX_VALUE}. 64-bit addresses may be
     *                                       supported in a future release.
     */
    @Override
    public byte getByte(long offset) {
        throwIfOffsetOver32Bit(offset);
        if (process == null) {
            return '?';
        }

        return reader.invoke(r -> r.readByte(process, toLacunaOffset(offset)), (byte) '?');
    }

    @Override
    public BinaryData copy() {
        throw new NotImplementedException("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryData copy(long offset, long length) {
        throwIfCopyingTooManyBytes(length);
        byte[] data = reader.invoke(
            r -> r.read(process, toLacunaOffset(offset), (int) length), new byte[0]);
        return new ByteArrayEditableData(data);
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException if attempting to read from offsets higher than
     *                                       {@link Integer#MAX_VALUE}. 64-bit addresses may be
     *                                       supported in a future release.
     */
    @Override
    public void copyToArray(long startFrom, byte[] target, int offset, int length) {
        throwIfOffsetOver32Bit(offset);
        if (process == null) {
            return;
        }

        byte[] readBytes = reader.invoke(r -> r.read(process, toLacunaOffset(startFrom), length), new byte[0]);
        System.arraycopy(readBytes, 0, target, offset, readBytes.length);
    }

    @Override
    public void saveToStream(OutputStream outputStream) throws IOException {
        throw new NotImplementedException("");
    }

    @Override
    public InputStream getDataInputStream() {
        throw new NotImplementedException("");
    }

    @Override
    public void dispose() {

    }

    private static int toLacunaOffset(long offset) {
        return (int) offset; // plz forgive
    }

    private static void throwIfOffsetOver32Bit(long offset) {
        if (offset > Integer.MAX_VALUE) {
            //throw new UnsupportedOperationException("Writing to 64-bit addresses is not supported at this time!");
        }
    }

    private static void throwIfCopyingTooManyBytes(long count) {
        // uhh...no, // TODO: buffering
        if (count > Integer.MAX_VALUE) {
            //throw new UnsupportedOperationException("Length cannot be higher than Integer.MAX_VALUE!");
        }
    }
}
