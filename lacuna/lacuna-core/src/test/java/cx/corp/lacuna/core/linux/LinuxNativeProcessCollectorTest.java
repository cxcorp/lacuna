package cx.corp.lacuna.core.linux;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import cx.corp.lacuna.core.domain.NativeProcess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserPrincipal;

import static org.junit.Assert.assertEquals;

public class LinuxNativeProcessCollectorTest {

    private LinuxNativeProcessCollector collector;
    private Path procRoot;
    private FileSystem fs;

    @Before
    public void setUp() {
        Configuration fsConfig =
            Configuration.unix()
                .toBuilder()
                .setAttributeViews("basic", "owner") // needs owner view to let us use Files.setOwner
                .build();
        fs = Jimfs.newFileSystem(fsConfig);
        procRoot = fs.getPath("/proc");
        collector = new LinuxNativeProcessCollector(procRoot);
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

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfProcRootIsNull() {
        new LinuxNativeProcessCollector(null);
    }

    @Test
    public void collectReturnsUnknownOwnerIfProcessCannotBeAccessed() {
        NativeProcess process = collector.collect(1234);
        assertEquals(NativeProcess.UNKNOWN_OWNER, process.getOwner());
    }

    @Test
    public void collectReturnsUnknownDescriptionIfProcessCannotBeAccessed() {
        NativeProcess process = collector.collect(1234);
        assertEquals(NativeProcess.UNKNOWN_DESCRIPTION, process.getDescription());
    }

    @Test
    public void collectReturnsCorrectPidEvenIfProcessCannotBeAccessed() {
        int pid = 4321;
        NativeProcess process = collector.collect(pid);
        assertEquals(pid, process.getPid());
    }

    @Test
    public void collectReturnsEmptyDescriptionIfCmdLineIsEmpty() throws IOException {
        Integer pid = 4552;

        Path cmdlinePath = procRoot.resolve(pid + "").resolve("cmdline");
        Files.createDirectories(cmdlinePath.getParent());
        Files.createFile(cmdlinePath);
        assertEquals(0L, Files.size(cmdlinePath));

        NativeProcess process = collector.collect(pid);
        assertEquals("", process.getDescription());
    }

    @Test
    public void collectReturnsCorrectCommandLineAsDescription() throws IOException {
        Integer pid = 4552;
        String description = "toaster.sh --flags=ayy -f 4214.ayy";

        Path cmdlinePath = procRoot.resolve(pid + "").resolve("cmdline");
        Files.createDirectories(cmdlinePath.getParent());
        String cmdLine = description.replace(' ', '\0') + '\0';
        Files.write(cmdlinePath, cmdLine.getBytes());

        NativeProcess process = collector.collect(pid);
        assertEquals(description, process.getDescription());
    }

    @Test
    public void collectReturnsCorrectOwner() throws IOException {
        Integer pid = 6812;
        String owner = "Apple";

        Path procPath = procRoot.resolve(pid + "");
        Files.createDirectories(procPath);
        UserPrincipal user = fs.getUserPrincipalLookupService().lookupPrincipalByName(owner);
        Files.setOwner(procPath, user);

        NativeProcess process = collector.collect(pid);
        assertEquals(owner, process.getOwner());
    }
}
