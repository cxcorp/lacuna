package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NativeProcessImplTest {
    private NativeProcessImpl process;

    @Before
    public void setUp() {
        process = new NativeProcessImpl();
    }

    @Test
    public void setPidIsCorrect() {
        int pid = 123;
        process.setPid(pid);
        assertEquals(pid, process.getPid());
    }

    @Test
    public void descriptionIsCorrect() {
        String desc = "htop.exe";
        process.setDescription(desc);
        assertEquals(desc, process.getDescription());
    }

    @Test
    public void setOwnerIsCorrect() {
        String owner = "root";
        process.setOwner(owner);
        assertEquals(owner, process.getOwner());
    }
}
