package cx.corp.lacuna.core.serialization;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Int32SerializerTest {
    private static final Map<Integer, byte[]> EXPECTED_VALUES = new HashMap<>();
    private static final int TYPE_BYTE_SIZE = 4;
    private Int32Serializer serializer;

    static {
        EXPECTED_VALUES.put(0, new byte[] { 0, 0, 0, 0 });
        EXPECTED_VALUES.put(1, new byte[] { 1, 0, 0, 0 });
        EXPECTED_VALUES.put(0x12345678, new byte[] { 0x78, 0x56, 0x34, 0x12 });
        EXPECTED_VALUES.put(-1, new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
        EXPECTED_VALUES.put(Integer.MAX_VALUE, new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x7F });
        EXPECTED_VALUES.put(Integer.MIN_VALUE, new byte[] { 0, 0, 0, (byte) 0x80 });
    }

    @Before
    public void setUp() {
        serializer = new Int32Serializer();
    }

    @Test(expected = IllegalArgumentException.class)
    public void serializeThrowsIfNullValuePassed() {
        serializer.serialize(null);
    }

    @Test
    public void serializeReturnsCorrectArrayLength() {
        byte[] bytes = serializer.serialize(0);
        assertEquals(TYPE_BYTE_SIZE, bytes.length);
    }

    @Test
    public void serializeSerializesExpectedValuesCorrectly() {
        for (Map.Entry<Integer, byte[]> entry : EXPECTED_VALUES.entrySet()) {
            int value = entry.getKey();
            byte[] expected = entry.getValue();

            byte[] serialized = serializer.serialize(value);
            assertArrayEquals(
                "Expected value " + value + " to be serialized to " + Arrays.toString(expected) + ", not " + Arrays.toString(serialized),
                expected,
                serialized);
        }
    }

    @Test
    public void cannotDeserializeNullArray() {
        assertFalse(serializer.canDeserialize(null));
    }

    @Test
    public void cannotDeserializeEmptyArray() {
        assertFalse(serializer.canDeserialize(new byte[0]));
    }

    @Test
    public void cannotDeserializeTooLongArray() {
        assertFalse(serializer.canDeserialize(new byte[TYPE_BYTE_SIZE * 2]));
    }

    @Test
    public void canDeserializeCorrectLengthArray() {
        assertTrue(serializer.canDeserialize(new byte[TYPE_BYTE_SIZE]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeThrowsOnNullArray() {
        serializer.deserialize(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeThrowsOnEmptyArray() {
        serializer.deserialize(new byte[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeThrowsOnTooLongArray() {
        serializer.deserialize(new byte[TYPE_BYTE_SIZE * 2]);
    }

    @Test
    public void deserializesExpectedValues() {
        for (Map.Entry<Integer, byte[]> entry : EXPECTED_VALUES.entrySet()) {
            int expected = entry.getKey();
            byte[] value = entry.getValue();

            int result = serializer.deserialize(value);
            assertEquals(
                "Expected value " + Arrays.toString(value) + " to be deserialized to " + expected + ", got " + result,
                expected,
                result);
        }
    }
}
