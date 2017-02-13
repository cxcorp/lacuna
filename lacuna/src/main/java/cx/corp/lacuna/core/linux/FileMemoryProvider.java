package cx.corp.lacuna.core.linux;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Provides access to a process's memory via the virtual {@code /proc/[pid]/mem} file.
 */
public class FileMemoryProvider implements ReadableMemoryProvider, WritableMemoryProvider {

    private final Path procRoot;
    private final Path relativeMemFilePath;

    public FileMemoryProvider(Path procRoot, Path relativeMemFilePath) {
        if (procRoot == null || relativeMemFilePath == null) {
            throw new IllegalArgumentException("Paths cannot be null!");
        }
        this.procRoot = procRoot;
        this.relativeMemFilePath = relativeMemFilePath;
    }

    /**
     * Opens a stream to the virtual {@code /proc/[pid]/mem} file.
     * @param pid The ID of the process.
     * @throws IOException if the stream cannot be opened.
     */
    @Override
    public SeekableByteChannel openRead(int pid) throws IOException {
        Path memFile = createPathToProcessMemoryFile(pid);
        return Files.newByteChannel(memFile, StandardOpenOption.READ);
    }

    @Override
    public SeekableByteChannel openWrite(int pid) throws IOException {
        Path memFile = createPathToProcessMemoryFile(pid);
        return Files.newByteChannel(memFile, StandardOpenOption.WRITE);
    }

    private Path createPathToProcessMemoryFile(int pid) {
        String pidStr = Integer.toString(pid);
        return procRoot.resolve(pidStr).resolve(relativeMemFilePath);
    }
}
