package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.NativeProcess;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class LinuxMemoryReader implements MemoryReader {

    private static final String PROC_MEM_FILENAME = "mem";
    private static final String FILE_MODE = "r";

    private final Path procRoot;

    public LinuxMemoryReader(Path procRoot) {
        this.procRoot = procRoot;
    }

    /** {@inheritDoc}
     *
     * @throws MemoryReadException if the virtual memory file for the process cannot be opened,
     *                             seeking to the requested offset fails, or reading the requested
     *                             bytes fails.
     */
    @Override
    public byte[] read(NativeProcess process, int offset, int bytesToRead) {
        File memFile = getProcessMemFile(process);

        try (FileInputStream stream = new FileInputStream(memFile)) {
            return read(stream, offset, bytesToRead);
        } catch (IOException ex) {
            throw new MemoryReadException("Failed to open memory file for reading, see getCause()!", ex);
        }
    }


    byte[] read(InputStream input, int offset, int bytesToRead) {
        byte[] bytes = new byte[bytesToRead];
        int bytesRead;

        try {
            long skippedBytes = input.skip(offset);
            if ((offset & 0xFFFFFFFFL) != skippedBytes) {
                throw new MemoryReadException("Failed to seek to offset " + offset);
            }

            bytesRead = input.read(bytes);
        } catch (IOException ex) {
            throw new MemoryReadException("Reading process memory failed, see getCause()!", ex);
        }

        if (bytesRead == -1) {
            throw new MemoryReadException("Reading process memory failed!");
        }

        return Arrays.copyOf(bytes, bytesRead);
    }

    private File getProcessMemFile(NativeProcess process) {
        Path mem = procRoot.resolve(Paths.get(process.getPid() + "", PROC_MEM_FILENAME));
        return mem.toFile();
    }
}
