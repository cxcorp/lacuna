package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.domain.NativeProcess;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class LinuxMemoryReader implements MemoryReader {

    private final MemoryProvider memoryProvider;

    public LinuxMemoryReader(MemoryProvider memoryProvider) {
        if (memoryProvider == null) {
            throw new IllegalArgumentException("memoryProvider cannot be null");
        }
        this.memoryProvider = memoryProvider;
    }

    /** {@inheritDoc}
     *
     * @throws MemoryReadException if the virtual memory file for the process cannot be opened,
     *                             seeking to the requested offset fails, or reading the requested
     *                             bytes fails.
     */
    @Override
    public byte[] read(NativeProcess process, int offset, int bytesToRead) {
        validateArguments(process, offset);

        byte[] bytes = new byte[bytesToRead];
        int bytesRead;

        try (InputStream input = memoryProvider.open(process)) {
            if (input == null) {
                throw new MemoryReadException("MemoryProvider provided a null stream!");
            }

            long skippedBytes = input.skip(offset);
            if (offset != skippedBytes) {
                throw new MemoryReadException("Failed to seek to offset " + offset);
            }

            bytesRead = input.read(bytes, 0, bytesToRead);

            if (bytesRead == -1) {
                throw new MemoryReadException("Reading process memory failed!");
            }

            return Arrays.copyOf(bytes, bytesRead);
        } catch (IOException ex) {
            throw new MemoryReadException("Reading process memory failed, see getCause()!", ex);
        }
    }

    private static void validateArguments(NativeProcess process, int offset) {
        if (process == null) {
            throw new IllegalArgumentException("process cannot be null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
    }
}
