package cx.corp.lacuna.core.serialization;

public interface TypeSerializers {
    <T> boolean register(Class<T> type, TypeSerializer<T> serializer);

    <T> TypeSerializer<T> find(Class<T> type);
}
