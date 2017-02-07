package cx.corp.lacuna.core.linux;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

class ProcPathFilter implements BiPredicate<Path, BasicFileAttributes> {

    private final int pidMax;

    public ProcPathFilter(int pidMax) {
        if (pidMax <= LinuxConstants.LOWEST_LEGAL_PID) {
            throw new IllegalArgumentException("pidMax cannot be lower than or equal to 1!");
        }
        this.pidMax = pidMax;
    }

    @Override
    public boolean test(Path path, BasicFileAttributes basicFileAttributes) {
        return accept(Files.isDirectory(path), path.getFileName().toString());
    }

    boolean accept(boolean isDirectory, String fileName) {
        if (!isDirectory) {
            return false;
        }

        Integer numericalPid = tryParseInt(fileName);
        return numericalPid != null
            && numericalPid >= LinuxConstants.LOWEST_LEGAL_PID
            && numericalPid < pidMax;
    }

    private Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
