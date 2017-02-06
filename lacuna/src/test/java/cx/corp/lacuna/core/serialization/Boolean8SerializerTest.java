package cx.corp.lacuna.core.serialization;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Boolean8SerializerTest {

    private static final int TYPE_BYTE_SIZE = 1;
    private Boolean8Serializer serializer;

    @Before
    public void setUp() {
        serializer = new Boolean8Serializer();
    }

    @Test(expected = IllegalArgumentException.class)
    public void serializeThrowsIfNullValuePassed() {
        serializer.serialize(null);
    }

    @Test
    public void serializeReturnsCorrectArrayLength() {
        byte[] bytes = serializer.serialize(true);
        assertEquals(TYPE_BYTE_SIZE, bytes.length);
    }

    @Test
    public void serializeReturnsOneWhenSerializingTrue() {
        byte[] bytes = serializer.serialize(true);
        assertArrayEquals(new byte[] { 1 }, bytes);
    }

    @Test
    public void serializeReturnsZeroWhenSerializingFalse() {
        byte[] bytes = serializer.serialize(false);
        assertArrayEquals(new byte[] { 0 }, bytes);
    }

    @Test
    public void cannotDeserializeNullData() {
        assertFalse(serializer.canDeserialize(null));
    }

    @Test
    public void cannotDeserializeEmptyArray() {
        assertFalse(serializer.canDeserialize(new byte[0]));
    }

    @Test
    public void canDeserializeByteArrayWithOnlyNumberOne() {
        assertTrue(serializer.canDeserialize(new byte[] { 1 }));
    }

    @Test
    public void canDeserializeByteArrayWithOnlyNumberZero() {
        assertTrue(serializer.canDeserialize(new byte[] { 0 }));
    }

    @Test
    public void cannotDeserializeByteArrayWithMoreThanOneElement() {
        assertFalse(serializer.canDeserialize(new byte[] { 0, 0, 0, 0}));
        assertFalse(serializer.canDeserialize(new byte[] { 1, 0, 0, 0}));
        assertFalse(serializer.canDeserialize(new byte[] { 0, 0, 0, 1}));
    }

    @Test
    public void cannotDeserializeOtherNumbersThanOneAndZero() {
        for (int i = 2; i <= 0xFF; i++) {
            byte[] data = new byte[] { (byte) i};
            assertFalse(
                "Expected serializer to not be able to serialize " + Arrays.toString(data),
                serializer.canDeserialize(data));
        }
    }

    @Test
    public void deserializesOneIntoTrue() {
        byte[] payload = new byte[] { 1 };
        assertEquals(true, serializer.deserialize(payload));
    }

    @Test
    public void deserializesZeroIntoFalse() {
        byte[] payload = new byte[] { 0 };
        assertEquals(false, serializer.deserialize(payload));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeThrowsIfDeserializingNullValue() {
        serializer.deserialize(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeThrowsIfDeserializingEmptyValue() {
        serializer.deserialize(new byte[0]);
    }

    @Test
    public void deserializeThrowsIfDeserializingIllegalValues() {
        for (int i = 2; i <= 0xFF; i++) {
            byte[] payload = new byte[] { (byte) i };
            try {
                serializer.deserialize(payload);
                fail("Expected expection when deserializing " + Arrays.toString(payload));
            } catch (IllegalArgumentException e) { }
        }
    }
}
