package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.ProcessOpenException;
import cx.corp.lacuna.core.RawMemoryReader;
import cx.corp.lacuna.core.domain.NativeProcess;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;

/**
 * {@inheritDoc}
 *
 * <p>This implementation reads a process's memory using
 * a {@link ReadableMemoryProvider}.
 */
public class LinuxRawMemoryReader implements RawMemoryReader {

    private final ReadableMemoryProvider readableMemoryProvider;

    /**
     * Constructs a new {@code LinuxRawMemoryReader} with the specified
     * readable memory provider.
     * @param readableMemoryProvider the memory provider.
     * @throws NullPointerException if {@code readableMemoryProvider} is null.
     */
    public LinuxRawMemoryReader(ReadableMemoryProvider readableMemoryProvider) {
        if (readableMemoryProvider == null) {
            throw new NullPointerException("Memory provider cannot be null");
        }
        this.readableMemoryProvider = readableMemoryProvider;
    }

    @Override
    public ByteBuffer read(NativeProcess process, int offset, int bytesToRead) {
        Objects.requireNonNull(process, "process cannot be null!");
        if (bytesToRead < 1) {
            throw new IllegalArgumentException("Cannot read fewer than 1 byte!");
        }

        ByteBuffer buffer = ByteBuffer.allocate(bytesToRead);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        try (SeekableByteChannel input = readableMemoryProvider.openRead(process.getPid())) {

            long bytesToSkip = 0xFFFFFFFFL & offset; // interpret the offset as an unsigned value
            input.position(bytesToSkip);

            int bytesRead = input.read(buffer);
            if (bytesRead == -1) {
                // SeekableByteChannel.position sets position even if it goes out of bounds,
                // but any subsequent reads or writes will return EOF
                throw new MemoryAccessException("Reading process memory failed! Reached end of memory!");
            }
            if (bytesRead != bytesToRead) {
                throw new MemoryAccessException("Only " + bytesRead + " bytes out of " + bytesToRead + " could be read!");
            }

            buffer.flip();
            return buffer;
        } catch (ProcessOpenException | IOException ex) {
            throw new MemoryAccessException("Reading process memory failed, see getCause()!", ex);
        }
    }
}
