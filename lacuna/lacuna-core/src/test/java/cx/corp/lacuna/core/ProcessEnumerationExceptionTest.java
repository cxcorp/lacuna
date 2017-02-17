package cx.corp.lacuna.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProcessEnumerationExceptionTest {

    @Test
    public void messageIsSetCorrectly() {
        String message = "An error occurred: 1234";
        ProcessEnumerationException ex = new ProcessEnumerationException(message);
        assertEquals(message, ex.getMessage());
    }

    @Test
    public void causeIsNullWhenNotSet() {
        ProcessEnumerationException ex = new ProcessEnumerationException("ayy");
        assertNull(ex.getCause());
    }
}
