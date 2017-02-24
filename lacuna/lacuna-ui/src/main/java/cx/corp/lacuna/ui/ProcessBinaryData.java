package cx.corp.lacuna.ui;

import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.MemoryWriter;
import cx.corp.lacuna.core.domain.NativeProcess;
import org.apache.commons.lang3.NotImplementedException;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.EditableBinaryData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

// no docs for EditableBinaryData for some reason, accessible here:
// https://github.com/exbin/exbin-utils-java/blob/46d00e1c832163d957cdea0670c7d783776bb55c/modules/exbin-binary_data/src/main/java/org/exbin/utils/binary_data/EditableBinaryData.java
public class ProcessBinaryData implements EditableBinaryData {

    private static final byte NULL_BYTE = 0;

    //private final MemoryReader reader;
    //private final MemoryWriter writer;
    private final ExceptionEater<MemoryWriter> writer;
    private final ExceptionEater<MemoryReader> reader;
    private NativeProcess process;

    public ProcessBinaryData(MemoryReader reader, MemoryWriter writer) {
        this.reader = new ExceptionEater<MemoryReader>(reader, MemoryAccessException.class);
        this.writer = new ExceptionEater<MemoryWriter>(writer, MemoryAccessException.class);
    }

    public void setActiveProcess(NativeProcess process) {
        this.process = process;
    }

    @Override
    public void setDataSize(long newSize) {
        // no-op
    }

    /**
     * {@inheritDoc}
     * @throws MemoryAccessException if memory writing fails. See {@link MemoryWriter#write}.
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
        writer.safeInvoke(w -> w.writeByte(process, toLacunaOffset(offset), b));
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
        // can't "insert" into process memory
    }

    @Override
    public void insert(long offset, byte[] bytes, int i, int i1) {
        // can't "insert" into process memory
    }

    @Override
    public void insert(long offset, BinaryData binaryData) {
        // can't "insert" into process memory
    }

    @Override
    public void insert(long offset, BinaryData binaryData, long l1, long l2) {
        // can't "insert" into process memory
    }

    @Override
    public long insert(long l, InputStream inputStream, long l1) throws IOException {
        // can't "insert" into process memory
        return 0;
    }

    /**
     * {@inheritDoc}
     * @throws MemoryAccessException if memory writing fails. See {@link MemoryWriter#write}.
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
        writer.safeInvoke(w -> w.write(process, toLacunaOffset(offset), buffer));
    }

    /**
     * {@inheritDoc}
     * @throws MemoryAccessException if memory writing fails. See {@link MemoryWriter#write}.
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
        writer.safeInvoke(w -> w.write(process, toLacunaOffset(offset), buffer));
    }

    /**
     * {@inheritDoc}
     * @throws MemoryAccessException if memory writing fails. See {@link MemoryWriter#write}.
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

        writer.safeInvoke(w -> w.write(process, toLacunaOffset(offset), bytes));
    }

    /**
     * {@inheritDoc}
     * @throws MemoryAccessException if memory writing fails. See {@link MemoryWriter#write}.
     * @throws UnsupportedOperationException if attempting to write to offsets higher than
     *                                       {@link Integer#MAX_VALUE}. 64-bit addresses may be
     *                                       supported in a future release.
     */
    @Override
    public void replace(long offset, byte[] replacingData, int replacingDataOffset, int length) {
        throwIfOffsetOver32Bit(offset);
        byte[] data = Arrays.copyOfRange(replacingData, replacingDataOffset, replacingDataOffset + length);
        writer.safeInvoke(w -> w.write(process, toLacunaOffset(offset), data));
    }

    /**
     * {@inheritDoc}
     * @throws MemoryAccessException if memory writing fails. See {@link MemoryWriter#write}.
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
     * Fills a buffer of length {@code length} with the byte {@byte b}, then writes
     * it to the offset {@code offset}.
     * {@inheritDoc}
     * @throws MemoryAccessException if memory writing fails. See {@link MemoryWriter#write}.
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

        byte[] data = new byte[(int) length];
        Arrays.fill(data, b);
        writer.safeInvoke(w -> w.write(process, toLacunaOffset(offset), data));
    }

    @Override
    public void remove(long offset, long length) {
        // nah, can't do that with memory
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
     * @throws MemoryAccessException if memory reading fails. See {@link MemoryReader#readByte}.
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

        return reader.safeInvokeReturn(r -> r.readByte(process, toLacunaOffset(offset)), (byte) '?');
    }

    @Override
    public BinaryData copy() {
        throw new NotImplementedException("");
    }

    @Override
    public BinaryData copy(long offset, long l1) {
        throw new NotImplementedException("");
    }

    /**
     * {@inheritDoc}
     * @throws MemoryAccessException if memory reading fails. See {@link MemoryReader#read}.
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

        byte[] readBytes = reader.safeInvokeReturn(r -> r.read(process, toLacunaOffset(startFrom), length), new byte[0]);
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
            throw new UnsupportedOperationException("Writing to 64-bit addresses is not supported at this time!");
        }
    }

    private static void throwIfCopyingTooManyBytes(long count) {
        // uhh...no, // TODO: buffering
        if (count > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("Length cannot be higher than Integer.MAX_VALUE!");
        }
    }

    private static class ExceptionEater<T> {
        private final T object;
        private final List<Class> eatenExceptions;

        private ExceptionEater(T object, Class... eatenExceptions) {
            this.object = object;
            this.eatenExceptions = Arrays.<Class>asList(eatenExceptions);
        }

        private void safeInvoke(Consumer<T> method) {
            try {
                method.accept(object);
            } catch (Exception ex) {
                System.out.println("SafeInvoke " + object.getClass().getName() + " - " + ex);
                if (!eatenExceptions.contains(ex.getClass())) {
                    throw ex;
                }
            }
        }

        private <R> R safeInvokeReturn(Function<T, R> method, R defaultRet) {
            try {
                return method.apply(object);
            } catch(Exception ex) {
                System.out.println("SafeInvoke " + object.getClass().getName() + " - " + ex);
                if (!eatenExceptions.contains(ex.getClass())) {
                    throw ex;
                }
                return defaultRet;
            }
        }
    }
}
