package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.PidEnumerator;
import cx.corp.lacuna.core.ProcessEnumerationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class LinuxPidEnumerator implements PidEnumerator {

    private final Path procRoot;
    private final Path pathToPidMax;

    /**
     * Constructs a new {@link LinuxPidEnumerator} with the specified process
     * directory path and memory path. In most cases, {@code procRoot} should
     * be {@code Paths.get("/proc")} and {@code pathToPidMax} should be
     * {@code Pats.get("mem")}.
     * @param procRoot the memory provider.
     * @param pathToPidMax the path to the memory file, relative to
     *                     {@code procRoot/:pid/}.
     * @throws NullPointerException if {@code procRoot} is null or if
     *                              {@code pathToPidMax} is null.
     */
    public LinuxPidEnumerator(Path procRoot, Path pathToPidMax) {
        if (procRoot == null || pathToPidMax == null) {
            throw new NullPointerException("Arguments cannot be null!");
        }
        this.procRoot = procRoot;
        this.pathToPidMax = pathToPidMax;
    }

    @Override
    public List<Integer> getPids() {
        int pidMax = readPidMax();
        BiPredicate<Path, BasicFileAttributes> filter = new ProcPathFilter(pidMax);

        try {
            // assumption: the used filter removes any non-integer files from the stream
            return Files.find(procRoot, 1, filter)
                .map(path -> Integer.parseUnsignedInt(path.getFileName().toString()))
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new ProcessEnumerationException("An error occurred while enumerating PIDs", ex);
        }
    }

    private int readPidMax() {
        try {
            return new Scanner(pathToPidMax).nextInt();
        } catch (IOException | NoSuchElementException ex) {
            return LinuxConstants.FALLBACK_PID_MAX;
        }
    }
}
