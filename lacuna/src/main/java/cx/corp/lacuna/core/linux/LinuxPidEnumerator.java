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

    private final Path procRooot;

    public LinuxPidEnumerator(Path procRooot) {
        this.procRooot = procRooot;
    }

    @Override
    public List<Integer> getPids() {
        int pidMax = readPidMax();
        BiPredicate<Path, BasicFileAttributes> filter = new ProcFileFilter(pidMax);

        try {
            // assumption: the used filter removes any non-integer files from the stream
            return Files.find(procRooot, 1, filter)
                .map(path -> Integer.parseUnsignedInt(path.getFileName().toString()))
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new ProcessEnumerationException("An error occurred while enumerating PIDs", ex);
        }
    }

    private int readPidMax() {
        try {
            Path pidMaxPath = procRooot.resolve(LinuxConstants.PID_MAX_RELATIVE_PATH);
            return new Scanner(pidMaxPath).nextInt();
        } catch (IOException | NoSuchElementException ex) {
            return LinuxConstants.FALLBACK_PID_MAX;
        }
    }
}
