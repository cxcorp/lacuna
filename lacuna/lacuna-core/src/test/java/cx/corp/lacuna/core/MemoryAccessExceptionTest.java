package cx.corp.lacuna.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MemoryAccessExceptionTest {
    @Test
    public void messageIsSetCorrectly() {
        String message = "An error occurred: 4321";
        MemoryAccessException ex = new MemoryAccessException(message);
        assertEquals(message, ex.getMessage());
    }

    @Test
    public void causeIsNullWhenNotSet() {
        MemoryAccessException ex = new MemoryAccessException("ayy");
        assertNull(ex.getCause());
    }

    @Test
    public void causeMessageIsCorrectWhenSet() {
        String message = "done goofed";
        Exception cause = new Exception(message);
        MemoryAccessException ex = new MemoryAccessException("ayy", cause);
        assertEquals(message, ex.getCause().getMessage());
    }
}
