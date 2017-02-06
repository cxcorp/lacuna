package cx.corp.lacuna.core.serialization;

/**
 * Provides methods for serializing and deserializing a Java type field-wise into
 * a byte array.
 * @param <T> The supported Java type.
 */
public interface TypeSerializer<T> {
    byte[] serialize(T value);
    T deserialize(byte[] data);
    boolean canDeserialize(byte[] data);
}
