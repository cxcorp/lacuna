package cx.corp.lacuna.core.serialization;

import java.util.HashMap;
import java.util.Map;

public class TypeSerializersImpl implements TypeSerializers {
    private final Map<Class<? extends Object>, TypeSerializer<? extends Object>> serializers;

    public TypeSerializersImpl() {
        serializers = new HashMap<>();
    }

    @Override
    public <T> boolean register(Class<T> type, TypeSerializer<T> serializer) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null!");
        }
        if (serializer == null) {
            throw new IllegalArgumentException("registration cannot be null!");
        }
        return serializers.putIfAbsent(type, serializer) == null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeSerializer<T> find(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null!");
        }
        return (TypeSerializer<T>) serializers.get(type);
    }
}
