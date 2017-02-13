package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.RawMemoryWriter;
import cx.corp.lacuna.core.domain.NativeProcess;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public class LinuxRawMemoryWriter implements RawMemoryWriter {

    private final WritableMemoryProvider memoryProvider;

    public LinuxRawMemoryWriter(WritableMemoryProvider memoryProvider) {
        if (memoryProvider == null) {
            throw new IllegalArgumentException("Memory provider cannot be null!");
        }
        this.memoryProvider = memoryProvider;
    }

    @Override
    public void write(NativeProcess process, int offset, byte[] buffer) throws MemoryAccessException {
        if (process == null || buffer == null) {
            throw new IllegalArgumentException("Arguments cannot be null!");
        }
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
