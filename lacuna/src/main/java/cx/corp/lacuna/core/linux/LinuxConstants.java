package cx.corp.lacuna.core.linux;

import java.io.File;

public class LinuxConstants {
    /** Fallback value of {@code /proc/sys/kernel/pid_max} to use if the file cannot be read.
     *
     * {@code man proc(5)} specifies that {@code 32768} is the default value. On 64-bit systems,
     * {@code pid_max} can be set to up to 2^22 (PID_MAX_LIMIT).
     */
    public static final int FALLBACK_PID_MAX = 32768;

    public static final File PID_MAX_FILE = new File("/proc/sys/kernel/pid_max");
}
