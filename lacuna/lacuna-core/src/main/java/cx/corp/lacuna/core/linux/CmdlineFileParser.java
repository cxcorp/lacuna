package cx.corp.lacuna.core.linux;

import org.apache.commons.lang3.StringUtils;
import java.util.Optional;

public class CmdlineFileParser {
    private static final char ARG_SEPARATOR = '\0';
    private static final char ARG_SEPARATOR_REPLACEMENT = ' ';

    // get process command line for description
    // "/proc/[pid]/cmdline
    //     This read-only file holds the complete command line for the
    //     process, unless the process is a zombie.  In the latter case,
    //     there is nothing in this file: that is, a read on this file
    //     will return 0 characters.  The command-line arguments appear
    //     in this file as a set of strings separated by null bytes
    //     ('\0'), with a further null byte after the last string."

    public Optional<String> parse(String cmdlineFileContents) {
        return Optional.ofNullable(cmdlineFileContents)
            .map(s -> StringUtils.strip(s, ARG_SEPARATOR + "")) // trim nulls from the end before replacing so that args ending in space don't get cut
            .map(s -> s.replace(ARG_SEPARATOR, ARG_SEPARATOR_REPLACEMENT));

    }
}
