package cx.corp.lacuna.core.linux;

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
     * Opens the specified process for reading. This method opens a channel
     * to the virtual {@code /proc/[pid]/mem} file.
     * @param pid The ID of the process.
     * @throws IOException if the channel cannot be opened.
     */
    @Override
    public SeekableByteChannel openRead(int pid) throws IOException {
        Path memFile = createPathToProcessMemoryFile(pid);
        return Files.newByteChannel(memFile, StandardOpenOption.READ);
    }

    /**
     * Opens the specified process for writing. This method opens a channel
     * to the virtual {@code /proc/[pid]/mem} file.
     * @param pid The ID of the process.
     * @throws IOException if the channel cannot be opened.
     */
    @Override
    public SeekableByteChannel openWrite(int pid) throws IOException {
        Path memFile = createPathToProcessMemoryFile(pid);
        return Files.newByteChannel(memFile, StandardOpenOption.WRITE);
    }

    private Path createPathToProcessMemoryFile(int pid) {
        String pidStr = Integer.toString(pid);
        return procRoot.resolve(pidStr).resolve(MEM_FILE_NAME);
    }
}
