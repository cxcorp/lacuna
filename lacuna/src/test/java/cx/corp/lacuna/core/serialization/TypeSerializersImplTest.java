package cx.corp.lacuna.core.serialization;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TypeSerializersImplTest {

    TypeSerializers serializers;

    @Before
    public void  setUp() {
        serializers = new TypeSerializersImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerThrowsIfNullTypePassed() {
        serializers.register(null, new TypeSerializer<Object>() {
            @Override
            public byte[] serialize(Object object) {
                return new byte[0];
            }

            @Override
            public Object deserialize(byte[] data) {
                return null;
            }

            @Override
            public boolean canDeserialize(byte[] data) {
                return false;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerThrowsIfNullSerializerPassed() {
        serializers.register(Integer.class, null);
    }

    @Test
    public void registerReturnsTrueWhenRegisteringTypeForFirstTime() {
        TypeSerializer<Integer> serializer = new TypeSerializer<Integer>() {
            @Override
            public byte[] serialize(Integer object) {
                return new byte[0];
            }

            @Override
            public Integer deserialize(byte[] data) {
                return null;
            }

            @Override
            public boolean canDeserialize(byte[] data) {
                return false;
            }
        };

        boolean ret = serializers.register(Integer.class, serializer);

        assertTrue(ret);
    }

    @Test
    public void registerDoesntReplaceExistingRegistration() {
        TypeSerializer<Character> originalSerializer = new TypeSerializer<Character>() {
            @Override
            public byte[] serialize(Character object) {
                return new byte[0];
            }

            @Override
            public Character deserialize(byte[] data) {
                return null;
            }

            @Override
            public boolean canDeserialize(byte[] data) {
                return false;
            }
        };

        boolean success = serializers.register(Character.class, originalSerializer);
        assertTrue(success);

        TypeSerializer<Character> newSerializer = new TypeSerializer<Character>() {
            @Override
            public byte[] serialize(Character object) {
                return new byte[123];
            }

            @Override
            public Character deserialize(byte[] data) {
                return 'a';
            }

            @Override
            public boolean canDeserialize(byte[] data) {
                return false;
            }
        };

        boolean reregisterSuccess = serializers.register(Character.class, newSerializer);
        assertFalse(reregisterSuccess);

        TypeSerializer<Character> foundSerializer = serializers.find(Character.class);
        assertEquals(originalSerializer, foundSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findThrowsIfNullClassPassed() {
        serializers.find(null);
    }

    @Test
    public void findFindsRegistration() {
        TypeSerializer<Integer> serializer = new TypeSerializer<Integer>() {
            @Override
            public byte[] serialize(Integer object) {
                return new byte[0];
            }

            @Override
            public Integer deserialize(byte[] data) {
                return null;
            }

            @Override
            public boolean canDeserialize(byte[] data) {
                return false;
            }
        };
        serializers.register(Integer.class, serializer);

        TypeSerializer<Integer> found = serializers.find(Integer.class);
        assertEquals(serializer, found);
    }

    @Test
    public void findDoesntFindNonExistingRegistration() {
        TypeSerializer<String> serializer = serializers.find(String.class);

        assertNull(serializer);
    }
}
