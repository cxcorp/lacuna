package cx.corp.lacuna.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProcessAccessExceptionTest {
    @Test
    public void messageIsSetCorrectly() {
        String message = "An error occurred: 4321";
        ProcessAccessException ex = new ProcessAccessException(message);
        assertEquals(message, ex.getMessage());
    }

    @Test
    public void causeIsNullWhenNotSet() {
        ProcessAccessException ex = new ProcessAccessException("ayy");
        assertNull(ex.getCause());
    }

    @Test
    public void causeMessageIsCorrectWhenSet() {
        String message = "done goofed";
        Exception cause = new Exception(message);
        ProcessAccessException ex = new ProcessAccessException("ayy", cause);
        assertEquals(message, ex.getCause().getMessage());
    }
}
