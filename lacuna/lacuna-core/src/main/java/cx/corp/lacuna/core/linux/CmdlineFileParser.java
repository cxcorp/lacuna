package cx.corp.lacuna.core.linux;

import org.apache.commons.lang3.StringUtils;
import java.util.Optional;

/**
 * A parser for {@code /proc/:pid/cmdline} files.
 *
 * <p>On Linux systems, the command line that was used to launch a process can
 * be fetched from {@code /proc/:pid/cmdline}, where {@code :pid} is the
 * process's identifier. The file is described in {@code man proc(5)}:
 * <p><pre>
 * {@code
 * /proc/[pid]/cmdline
 *     This read-only file holds the complete command line for the process,
 *     unless the process is a zombie.  In the latter case, there is nothing
 *     in this file: that is, a read on this file will return 0 characters.
 *     The command-line arguments appear in this file as a set of strings
 *     separated by null bytes ('\0'), with a further null byte after the last
 *     string.
 * }
 * </pre>
 */
public class CmdlineFileParser {
    private static final char ARG_SEPARATOR = '\0';
    private static final char ARG_SEPARATOR_REPLACEMENT = ' ';

    /**
     * Parses a {@code cmdline} file and returns it in space delimited form.
     * @param cmdlineFileContents the contents of the {@code cmdline} file.
     * @return the command line in space delimited form.
     */
    public Optional<String> parse(String cmdlineFileContents) {
        return Optional.ofNullable(cmdlineFileContents)
            .map(s -> StringUtils.strip(s, ARG_SEPARATOR + "")) // trim nulls from the end before replacing so that args ending in space don't get cut
            .map(s -> s.replace(ARG_SEPARATOR, ARG_SEPARATOR_REPLACEMENT));

    }
}
