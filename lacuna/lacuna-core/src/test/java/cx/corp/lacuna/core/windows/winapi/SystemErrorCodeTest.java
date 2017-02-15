package cx.corp.lacuna.core.windows.winapi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SystemErrorCodeTest {

    @Test
    public void getDescriptionReturnsCorrectDescription() {
        SystemErrorCode error = SystemErrorCode.PARTIAL_COPY;
        String description = "Only part of a ReadProcessMemory or WriteProcessMemory request was completed";

        assertEquals(description, error.getDescription());
    }

    @Test
    public void getSystemErrorIdReturnsCorrectId() {
        SystemErrorCode error = SystemErrorCode.INSUFFICIENT_BUFFER;
        int expectedId = 122;

        assertEquals(expectedId, error.getSystemErrorId());
    }

    @Test
    public void fromIdFindsCorrectEnumConstants() {
        for (SystemErrorCode errorCode : SystemErrorCode.values()) {
            SystemErrorCode foundConstant = SystemErrorCode.fromId(errorCode.getSystemErrorId());
            assertEquals(errorCode, foundConstant);
        }
    }

    @Test
    public void toStringContainsSystemErrorIdAndDescription() {
        for (SystemErrorCode error : SystemErrorCode.values()) {
            int id = error.getSystemErrorId();
            String description = error.getDescription();

            String toString = error.toString();

            assertTrue(toString.contains(Integer.toString(id)));
            assertTrue(toString.contains(description));
        }
    }
}
