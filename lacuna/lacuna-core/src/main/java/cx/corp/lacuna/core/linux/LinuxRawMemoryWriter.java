package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.RawMemoryWriter;
import cx.corp.lacuna.core.domain.NativeProcess;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;

/**
 * {@inheritDoc}
 *
 * <p>The {@link LinuxRawMemoryWriter} class reads a process's memory using
 * a {@link ReadableMemoryProvider}.
 */
public class LinuxRawMemoryWriter implements RawMemoryWriter {

    private final WritableMemoryProvider memoryProvider;

    /**
     * Constructs a new {@link LinuxRawMemoryWriter} with the specified
     * readable memory provider.
     * @param memoryProvider the memory provider.
     * @throws NullPointerException if {@code memoryProvider} is null.
     */
    public LinuxRawMemoryWriter(WritableMemoryProvider memoryProvider) {
        if (memoryProvider == null) {
            throw new NullPointerException("Memory provider cannot be null!");
        }
        this.memoryProvider = memoryProvider;
    }

    @Override
    public void write(NativeProcess process, int offset, byte[] buffer) throws MemoryAccessException {
        Objects.requireNonNull(process, "process cannot be null!");
        Objects.requireNonNull(buffer, "buffer cannot be null!");
        if (buffer.length < 1) {
            throw new IllegalArgumentException("Cannot write fewer than 1 byte!");
        }

        try (SeekableByteChannel output = memoryProvider.openWrite(process.getPid())) {

            long bytesToSkip = 0xFFFFFFFFL & offset; // interpret the offset as an unsigned value
            output.position(bytesToSkip);

            int bytesWritten = output.write(ByteBuffer.wrap(buffer));
            if (bytesWritten != buffer.length) {
                throw new MemoryAccessException("Only " + bytesWritten + " bytes out of " + buffer.length + " could be written!");
            }
        } catch (IOException ex) {
            throw new MemoryAccessException("Writing process memory failed, see getCause()!", ex);
        }
    }
}
