package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.ProcessEnumerationException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LinuxNativeProcessEnumerator implements NativeProcessEnumerator {

    @Override
    public List<NativeProcess> getProcesses() {
        ArrayList<NativeProcess> processes = new ArrayList<>();

        FileFilter filter = new ProcFileFilter(readPidMax());
        for (File procDirectory : readProcDirectories(filter)) {
            NativeProcess process = readNativeProcess(procDirectory);
            processes.add(process);
        }

        return processes;
    }

    private int readPidMax() {
        try {
            return new Scanner(LinuxConstants.PID_MAX_FILE).nextInt();
        } catch (IOException | NoSuchElementException ex) {
            return LinuxConstants.FALLBACK_PID_MAX;
        }
    }

    private NativeProcess readNativeProcess(File procDirectory) {
        NativeProcess process = new NativeProcess();

        String directoryName = procDirectory.getName();
        int pid = Integer.parseInt(directoryName);
        process.setPid(pid);

        String processCmdLine = readCmdLine(directoryName);
        process.setDescription(processCmdLine);

        String owner = readOwner(procDirectory.toPath());
        process.setOwner(owner);

        return process;
    }

    private File[] readProcDirectories(FileFilter filter) {
        File[] procFiles = new File("/proc").listFiles(filter);
        if (procFiles == null) {
            throw new ProcessEnumerationException("Unable to list contents of /proc!");
        }
        return procFiles;
    }

    private Path constructCmndLinePath(String pid) {
        return Paths.get("/proc", pid, "cmdline");
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
