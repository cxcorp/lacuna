package cx.corp.lacuna.core.linux;

import java.io.File;
import java.io.FileFilter;

class ProcFileFilter implements FileFilter {

    private final int pidMax;

    public ProcFileFilter(int pidMax) {
        if (pidMax <= LinuxConstants.LOWEST_LEGAL_PID) {
            throw new IllegalArgumentException("pidMax cannot be lower than or equal to 1!");
        }
        this.pidMax = pidMax;
    }

    @Override
    public boolean accept(File pathname) {
        if (pathname == null) {
            return false;
        }

        return accept(pathname.isDirectory(), pathname.getName());
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
