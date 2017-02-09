package cx.corp.lacuna.core.linux;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileMemoryProvider implements MemoryProvider {

    private final Path procRoot;
    private final Path relativeMemFilePath;

    public FileMemoryProvider(Path procRoot, Path relativeMemFilePath) {
        if (procRoot == null || relativeMemFilePath == null) {
            throw new IllegalArgumentException("Paths cannot be null!");
        }
        this.procRoot = procRoot;
        this.relativeMemFilePath = relativeMemFilePath;
    }

    @Override
    public InputStream open(int pid) throws IOException {
        Path memFile = createPathToProcessMemoryFile(pid);
        return Files.newInputStream(memFile, StandardOpenOption.READ);
    }

    private Path createPathToProcessMemoryFile(int pid) {
        String pidStr = Integer.toString(pid);
        return procRoot.resolve(pidStr).resolve(relativeMemFilePath);
    }
}
