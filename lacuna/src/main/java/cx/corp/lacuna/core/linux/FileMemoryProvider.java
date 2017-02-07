package cx.corp.lacuna.core.linux;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMemoryProvider implements MemoryProvider {

    private final Path procRoot;
    private final String memFileName;

    public FileMemoryProvider(Path procRoot, String memFileName) {
        if (procRoot == null) {
            throw new IllegalArgumentException("procRoot cannot be null");
        }
        if (memFileName == null) {
            throw new IllegalArgumentException("memFileName cannot be null");
        }

        this.procRoot = procRoot;
        this.memFileName = memFileName;
    }

    @Override
    public InputStream open(int pid) throws IOException {
        Path memFile = createPathToProcessMemoryFile(pid);
        return new FileInputStream(memFile.toFile());
    }

    private Path createPathToProcessMemoryFile(int pid) {
        String pidStr = Integer.toString(pid);
        Path relativeMemLocation = Paths.get(pidStr, memFileName);
        return procRoot.resolve(relativeMemLocation);
    }
}
