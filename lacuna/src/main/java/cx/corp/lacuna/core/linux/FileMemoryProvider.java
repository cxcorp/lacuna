package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.NativeProcess;

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
    public InputStream open(NativeProcess process) throws IOException {
        if (process == null) {
            throw new IllegalArgumentException("process cannot be null!");
        }

        Path memFile = createPathToProcessMemoryFile(process);
        return new FileInputStream(memFile.toFile());
    }

    private Path createPathToProcessMemoryFile(NativeProcess process) {
        String pid = Integer.toString(process.getPid());
        Path relativeMemLocation = Paths.get(pid, memFileName);
        return procRoot.resolve(relativeMemLocation);
    }
}
