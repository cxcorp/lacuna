package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.ProcessOpenException;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Provides read and write access to a process's memory via the virtual
 * {@code /proc/[pid]/mem} file.
 */
public class FileMemoryProvider implements ReadableMemoryProvider, WritableMemoryProvider {

    private static final String MEM_FILE_NAME = "mem";
    private final Path procRoot;

    /**
     * Constructs a new {@link FileMemoryProvider} with the specified process
     * directory path. In most cases, this should be {@code Paths.get("/proc")}.
     * @param procRoot the parent folder of the processes.
     * @throws NullPointerException if {@code procRoot} is null.
     */
    public FileMemoryProvider(Path procRoot) {
        if (procRoot == null) {
            throw new NullPointerException("Paths cannot be null!");
        }
        this.procRoot = procRoot;
    }

    /**
     * {@inheritDoc}
     * <p>This method opens a channel to the virtual
     * {@code /proc/[pid]/mem} file.
     */
    @Override
    public SeekableByteChannel openRead(int pid) throws ProcessOpenException {
        Path memFile = createPathToProcessMemoryFile(pid);
        try {
            return Files.newByteChannel(memFile, StandardOpenOption.READ);
        } catch (IOException ex) {
            String message = String.format(
                "Failed to open process %d for reading! See getCause()!",
                pid
            );
            throw new ProcessOpenException(message, ex);
        }
    }

    /**
     * {@inheritDoc}
     * <p>This method opens a channel to the virtual
     * {@code /proc/[pid]/mem} file.
     */
    @Override
    public SeekableByteChannel openWrite(int pid) throws ProcessOpenException {
        Path memFile = createPathToProcessMemoryFile(pid);
        try {
            return Files.newByteChannel(memFile, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            String message = String.format(
                "Failed to open process %d for writing! See getCause()!",
                pid
            );
            throw new ProcessOpenException(message, ex);
        }
    }

    private Path createPathToProcessMemoryFile(int pid) {
        String pidStr = Integer.toString(pid);
        return procRoot.resolve(pidStr).resolve(MEM_FILE_NAME);
    }
}
