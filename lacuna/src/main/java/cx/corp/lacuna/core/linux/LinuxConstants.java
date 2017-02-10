package cx.corp.lacuna.core.linux;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contains constant values specific to the Linux platform.
 */
public class LinuxConstants {
    /**
     * Depicts the lowest possible process identifier value.
     */
    public static final int LOWEST_LEGAL_PID = 1;

    /**
     * Fallback value of {@code /proc/sys/kernel/pid_max} to use if the file cannot be read.
     * <p>
     * {@code man proc(5)} specifies that {@code 32768} is the default value. On 64-bit systems,
     * {@code pid_max} can be set to up to 2^22 (PID_MAX_LIMIT).
     */
    public static final int FALLBACK_PID_MAX = 32768;

    /**
     * Default path to the process root directory on Linux platforms.
     */
    public static final Path DEFAULT_PROC_ROOT = Paths.get("/proc");

    /**
     * Default path to the {@code pid_max} constant on Linux platforms.
     */
    public static final Path DEFAULT_PID_MAX = Paths.get("/proc/sys/kernel/pid_max");
}
