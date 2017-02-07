package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LinuxNativeProcessCollector implements NativeProcessCollector {

    private static final String PROC_CMDLINE_PATH = "cmdline";
    private final Path procRoot;

    public LinuxNativeProcessCollector(Path procRoot) {
        this.procRoot = procRoot;
    }

    @Override
    public NativeProcess collect(int pid) {
        Path procPath = procRoot.resolve(Integer.toUnsignedString(pid));
        return readNativeProcess(procPath.toFile());
    }

    private NativeProcess readNativeProcess(File procDirectory) {
        NativeProcess process = new NativeProcessImpl();

        String directoryName = procDirectory.getName();
        int pid = Integer.parseInt(directoryName);
        process.setPid(pid);

        String processCmdLine = readCmdLine(directoryName);
        process.setDescription(processCmdLine);

        String owner = readOwner(procDirectory.toPath());
        process.setOwner(owner);

        return process;
    }

    private Path constructCmndLinePath(String pid) {
        Path childPath = Paths.get(pid, PROC_CMDLINE_PATH);
        return procRoot.resolve(childPath);
    }

    private String readCmdLine(String pidString) {
        // get process command line for description
        // "/proc/[pid]/cmdline
        //     This read-only file holds the complete command line for the
        //     process, unless the process is a zombie.  In the latter case,
        //     there is nothing in this file: that is, a read on this file
        //     will return 0 characters.  The command-line arguments appear
        //     in this file as a set of strings separated by null bytes
        //     ('\0'), with a further null byte after the last string."
        Path cmdLinePath = constructCmndLinePath(pidString);
        try {
            List<String> lines = Files.readAllLines(cmdLinePath, StandardCharsets.UTF_8);
            return lines.size() > 0
                ? lines.get(0).replace('\0', ' ').trim()
                : NativeProcess.UNKNOWN_DESCRIPTION;
        } catch (IOException e) {
            return NativeProcess.UNKNOWN_DESCRIPTION;
        }
    }

    private String readOwner(Path procDirectory) {
        try {
            return Files.getOwner(procDirectory).getName();
        } catch (UnsupportedOperationException | IOException | SecurityException e) {
            return NativeProcess.UNKNOWN_OWNER;
        }
    }
}
