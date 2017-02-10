package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.RawMemoryReader;
import cx.corp.lacuna.core.domain.NativeProcess;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LinuxRawMemoryReader implements RawMemoryReader {

    private final MemoryProvider memoryProvider;

    public LinuxRawMemoryReader(MemoryProvider memoryProvider) {
        if (memoryProvider == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }
        this.memoryProvider = memoryProvider;
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
        try (InputStream input = memoryProvider.open(process.getPid())) {
            if (input == null) {
                throw new MemoryReadException("MemoryProvider provided a null stream!");
            }

            long bytesToSkip = 0xFFFFFFFFL & offset;
            long skippedBytes = input.skip(bytesToSkip); // interpret the offset as an unsigned value
            if (skippedBytes != bytesToSkip) {
                throw new MemoryReadException("Failed to seek to offset " + offset + "! Actually skipped " + skippedBytes + " bytes.");
            }

            // write directly to ByteBuffer's backing array
            int bytesRead = input.read(buffer.array(), 0, bytesToRead);

            if (bytesRead == -1) {
                throw new MemoryReadException("Reading process memory failed!");
            }

            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(bytesRead);
            return buffer;
        } catch (IOException ex) {
            throw new MemoryReadException("Reading process memory failed, see getCause()!", ex);
        }
    }
}
