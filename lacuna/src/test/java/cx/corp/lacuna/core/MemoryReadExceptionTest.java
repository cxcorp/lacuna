package cx.corp.lacuna.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MemoryReadExceptionTest {
    @Test
    public void messageIsSetCorrectly() {
        String message = "An error occurred: 4321";
        MemoryReadException ex = new MemoryReadException(message);
        assertEquals(message, ex.getMessage());
    }

    @Test
    public void causeIsNullWhenNotSet() {
        MemoryReadException ex = new MemoryReadException("ayy");
        assertNull(ex.getCause());
    }

    @Test
    public void causeMessageIsCorrectWhenSet() {
        String message = "done goofed";
        Exception cause = new Exception(message);
        MemoryReadException ex = new MemoryReadException("ayy", cause);
        assertEquals(message, ex.getCause().getMessage());
    }
}
