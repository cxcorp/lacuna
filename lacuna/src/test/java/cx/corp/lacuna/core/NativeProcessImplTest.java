package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcessImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void equalsFindsSameObjectsEqual() {
        process = new NativeProcessImpl(123, "ayy", "lmao");
        assertTrue(process.equals(process));
    }

    @Test
    public void equalsFindsNullNotEqual() {
        process = new NativeProcessImpl(321, "yea", "boiiiiiiiiiii");
        assertFalse(process.equals(null));
    }

    @Test
    public void equalsFindsProcsWithSamePropertyValuesEqual() {
        NativeProcessImpl a = new NativeProcessImpl(123, "yuss", "boii");
        NativeProcessImpl b = new NativeProcessImpl(123, "yuss", "boii");
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }

    @Test
    public void equalsReturnsFalseIfPidIsDifferent() {
        NativeProcessImpl a = new NativeProcessImpl(123, "yuss", "boii");
        NativeProcessImpl b = new NativeProcessImpl(123, "yuss", "boii");
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        a.setPid(321);
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
    }

    @Test
    public void equalsReturnsFalseIfDescriptionIsDifferent() {
        NativeProcessImpl a = new NativeProcessImpl(123, "yuss", "boii");
        NativeProcessImpl b = new NativeProcessImpl(123, "yuss", "boii");
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        a.setDescription("wot");
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
    }

    @Test
    public void equalsReturnsFalseIfOwnerIsDifferent() {
        NativeProcessImpl a = new NativeProcessImpl(123, "yuss", "boii");
        NativeProcessImpl b = new NativeProcessImpl(123, "yuss", "boii");
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        a.setOwner("me");
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
    }

    @Test
    public void hashCodeIsSameForSameObject() {
        NativeProcessImpl a = new NativeProcessImpl(123, "yuss", "boii");
        assertEquals(a.hashCode(), a.hashCode());
    }

    @Test
    public void hashCodeIsSameForObjectsWithSamePropertyValues() {
        NativeProcessImpl a = new NativeProcessImpl(123, "yuss", "boii");
        NativeProcessImpl b = new NativeProcessImpl(123, "yuss", "boii");
        assertEquals(a.hashCode(), b.hashCode());
    }
}
