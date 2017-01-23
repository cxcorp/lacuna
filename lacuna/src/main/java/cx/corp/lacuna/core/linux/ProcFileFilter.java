package cx.corp.lacuna.core.linux;

import java.io.File;
import java.io.FileFilter;

class ProcFileFilter implements FileFilter {

    private final int pidMax;

    public ProcFileFilter(int pidMax) {
        this.pidMax = pidMax;
    }

    @Override
    public boolean accept(File pathname) {
        if (pathname == null) {
            return false;
        }

        if (!pathname.isDirectory()) {
            return false;
        }

        Integer numericalPid = tryParseInt(pathname.getName());
        return numericalPid != null && numericalPid >= 0 && numericalPid < pidMax;
    }

    private Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
