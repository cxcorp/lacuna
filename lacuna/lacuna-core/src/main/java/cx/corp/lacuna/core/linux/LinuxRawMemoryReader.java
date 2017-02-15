package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.RawMemoryReader;
import cx.corp.lacuna.core.domain.NativeProcess;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;


public class LinuxRawMemoryReader implements RawMemoryReader {

    private final ReadableMemoryProvider readableMemoryProvider;

    public LinuxRawMemoryReader(ReadableMemoryProvider readableMemoryProvider) {
        if (readableMemoryProvider == null) {
            throw new IllegalArgumentException("Memory provider cannot be null");
        }
        this.readableMemoryProvider = readableMemoryProvider;
    }

    @Override
    public ByteBuffer read(NativeProcess process, int offset, int bytesToRead) {
        if (process == null) {
            throw new IllegalArgumentException("Process cannot be null!");
        }
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
        } catch (IOException ex) {
            throw new MemoryAccessException("Reading process memory failed, see getCause()!", ex);
        }
    }
}
