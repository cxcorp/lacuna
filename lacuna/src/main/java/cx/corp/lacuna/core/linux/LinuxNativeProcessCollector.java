package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class LinuxNativeProcessCollector implements NativeProcessCollector {

    private static final String PROC_CMDLINE_PATH = "cmdline";
    private final CmdlineFileParser cmdlineParser;
    private final Path procRoot;

    public LinuxNativeProcessCollector(Path procRoot) {
        if (procRoot == null) {
            throw new IllegalArgumentException("Proc root cannot be null!");
        }
        this.cmdlineParser = new CmdlineFileParser();
        this.procRoot = procRoot;
    }

    @Override
    public NativeProcess collect(int pid) {
        Path procPath = procRoot.resolve(Integer.toUnsignedString(pid));
        NativeProcess process = readNativeProcess(procPath);
        process.setPid(pid);
        return process;
    }

    private NativeProcess readNativeProcess(Path processDir) {
        NativeProcess process = new NativeProcessImpl();

        String processCmdLine = readCmdLine(processDir);
        process.setDescription(processCmdLine);

        String owner = readOwner(processDir);
        process.setOwner(owner);

        return process;
    }

    private String readCmdLine(Path processDirectory) {
        // get process command line for description
        // "/proc/[pid]/cmdline
        //     This read-only file holds the complete command line for the
        //     process, unless the process is a zombie.  In the latter case,
        //     there is nothing in this file: that is, a read on this file
        //     will return 0 characters.  The command-line arguments appear
        //     in this file as a set of strings separated by null bytes
        //     ('\0'), with a further null byte after the last string."
        Path cmdLinePath = constructCmdLinePath(processDirectory);
        return readContentsFully(cmdLinePath)
            .flatMap(cmdlineParser::parse)
            .orElse(NativeProcess.UNKNOWN_DESCRIPTION);
    }

    private Path constructCmdLinePath(Path processDirectory) {
        return processDirectory.resolve(PROC_CMDLINE_PATH);
    }

    private Optional<String> readContentsFully(Path path) {
        try {
            String contents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            return Optional.of(contents);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    private String readOwner(Path procDirectory) {
        try {
            return Files.getOwner(procDirectory).getName();
        } catch (UnsupportedOperationException | IOException e) {
            return NativeProcess.UNKNOWN_OWNER;
        }
    }
}
