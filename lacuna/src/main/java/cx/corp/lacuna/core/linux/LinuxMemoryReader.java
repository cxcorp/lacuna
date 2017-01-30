package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.MemoryReadException;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.common.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class LinuxMemoryReader implements MemoryReader {

    private static final String PROC_MEM_FILENAME = "mem";
    private static final String FILE_MODE = "r";

    private final Path procRoot;

    public LinuxMemoryReader(Path procRoot) {
        this.procRoot = procRoot;
    }

    @Override
    public byte[] read(NativeProcess process, int offset, int bytesToRead) {
        File memFile = getProcessMemFile(process);

        byte[] bytes = new byte[bytesToRead];
        int actuallyRead = 0;

        try (RandomAccessFile stream = new RandomAccessFile(memFile, FILE_MODE)) {
            stream.seek(offset);
            actuallyRead = stream.read(bytes, 0, bytesToRead);
        } catch (IOException ex) {
            String msg =
                    "Reading process memory failed, see getCause()! Memory path is "
                    + memFile + ".";
            throw new MemoryReadException(msg, ex);
        }

        if (actuallyRead < 1 && bytesToRead > actuallyRead) {
            // was meant to read more bytes, but actually read nothing ??
            String msg = "No bytes were read when expecting to read more! Memory path is "
                    + memFile + ".";
            throw new MemoryReadException(msg);
        }

        if (actuallyRead < bytesToRead) {
            // managed to read fewer bytes than expecting, trim array
            return Utilities.copyToFittedArray(bytes, actuallyRead);
        }

        return bytes;
    }

    private File getProcessMemFile(NativeProcess process) {
        Path mem = procRoot.resolve(Paths.get(process.getPid() + "", PROC_MEM_FILENAME));
        return mem.toFile();
    }
}
