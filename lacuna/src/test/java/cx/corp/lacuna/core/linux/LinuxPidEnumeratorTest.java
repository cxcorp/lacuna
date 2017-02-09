package cx.corp.lacuna.core.linux;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import cx.corp.lacuna.core.ProcessEnumerationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LinuxPidEnumeratorTest {
    private Path defaultProcRoot;
    private Path defaultPidMaxFilePath;
    private FileSystem fs;
    private LinuxPidEnumerator enumerator;

    @Before
    public void setUp() {
        // in-memory FS
        fs = Jimfs.newFileSystem(Configuration.unix());
        defaultProcRoot = fs.getPath("/proc");
        defaultPidMaxFilePath = fs.getPath("/proc/sys/kernel/pid_max");
        enumerator = new LinuxPidEnumerator(defaultProcRoot, defaultPidMaxFilePath);
        // file system starts empty
    }

    @After
    public void tearDown() {
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException ex) {
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullProcrootPassed() {
        new LinuxPidEnumerator(null, defaultPidMaxFilePath);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullPidMaxPathPassed() {
        new LinuxPidEnumerator(defaultProcRoot, null);
    }

    @Test(expected = ProcessEnumerationException.class)
    public void getPidsThrowsIfProcRootDoesntExist() {
        enumerator.getPids();
    }

    @Test
    public void getPidsReturnsAllPidsFromProcRoot() throws IOException {
        Files.createDirectories(defaultProcRoot);
        // don't create defaultPidMaxFilePath, make it use fallback max in these tests
        List<Integer> existingPids = createIncrementingPidsInRoot(
            defaultProcRoot,
            LinuxConstants.LOWEST_LEGAL_PID,
            50);

        List<Integer> pids = enumerator.getPids();

        assertTrue(existingPids.containsAll(pids));
        assertTrue(pids.containsAll(existingPids));
    }

    @Test
    public void getPidsDoesntGetChildDirectoryPids() throws IOException {
        Files.createDirectories(defaultProcRoot);

        // create pid-like directories in some named child directories
        Path firstSubfolder = defaultProcRoot.resolve(fs.getPath("firstSubfolder"));
        Path secondSubfolder = defaultProcRoot.resolve(fs.getPath("secondSubfolder"));
        Files.createDirectory(firstSubfolder);
        Files.createDirectory(secondSubfolder);
        createIncrementingPidsInRoot(firstSubfolder, LinuxConstants.LOWEST_LEGAL_PID, 3);
        createIncrementingPidsInRoot(secondSubfolder, LinuxConstants.LOWEST_LEGAL_PID, 7);

        List<Integer> pids = enumerator.getPids();

        assertTrue(pids.isEmpty());
    }

    @Test
    public void getPidsObeysMaxPidsValue() throws IOException {
        Files.createDirectories(defaultProcRoot);
        Files.createDirectories(defaultPidMaxFilePath.getParent());

        // Set pid max to some low value
        int pidMax = 15;
        Files.write(defaultPidMaxFilePath, (pidMax + "").getBytes());
        createIncrementingPidsInRoot(defaultProcRoot, LinuxConstants.LOWEST_LEGAL_PID, pidMax + 25);

        List<Integer> pids = enumerator.getPids();

        // pidMax is one greater than the highest legal pid, so we should have pids [1, pidMax[ now
        List<Integer> expected = IntStream.range(1, pidMax).mapToObj(i -> i).collect(Collectors.toList());
        assertTrue(expected.containsAll(pids));
        assertTrue(pids.containsAll(expected));
    }

    @Test
    public void getPidsUsesFallbackPidMaxIfPidMaxCannotBeRead() throws IOException {
        Files.createDirectories(defaultProcRoot);
        // don't create defaultPidMaxFilePath
        assertFalse("pid_max cannot exist before test!", Files.exists(defaultPidMaxFilePath));
        createIncrementingPidsInRoot(
            defaultProcRoot,
            LinuxConstants.FALLBACK_PID_MAX,
            LinuxConstants.FALLBACK_PID_MAX + 50);

        List<Integer> pids = enumerator.getPids();

        assertTrue(pids.isEmpty());
    }

    private List<Integer> createIncrementingPidsInRoot(Path root, int lowestPidCreated, int highestPidCreated) throws IOException {
        List<Integer> createdFolders = new ArrayList<>();
        for (int i = lowestPidCreated; i < highestPidCreated; i++) {
            Path procFolder = fs.getPath(i + "");
            Files.createDirectory(root.resolve(procFolder));
            createdFolders.add(i);
        }
        return createdFolders;
    }
}
